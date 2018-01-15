/**
 * Copyright © 2016 Mathias Kowalzik (Mathias.Kowalzik@leandreck.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.leandreck.endpoints.processor.model.typefactories;

import org.leandreck.endpoints.annotations.TypeScriptType;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.function.Function;

public class TypeNodeUtils {

    static final String NO_TEMPLATE = "NO_TEMPLATE";
    static final String UNDEFINED_TYPE_NAME = "UNDEFINED";
    static final String JAVA_LANG_OBJECT = "java.lang.Object";
    static TypeMirror objectMirror;

    public static <A extends Annotation> A getAnnotationForClass(final TypeMirror typeMirror, final Class<A> annotation, final Types typeUtils) {
        final TypeKind kind = typeMirror.getKind();
        final TypeMirror realMirror;
        if (TypeKind.ARRAY.equals(kind)) {
            realMirror = ((ArrayType) typeMirror).getComponentType();
        } else {
            realMirror = typeMirror;
        }

        final Element definingElement = typeUtils.asElement(realMirror);
        return (definingElement != null) ? definingElement.getAnnotation(annotation) : null;
    }

    static String defineTemplate(final String typeNodeTemplate,
                                 final TypeScriptType typeScriptTypeAnnotation,
                                 final Element element) {

        if (typeNodeTemplate == null || typeNodeTemplate.isEmpty()) {
            throw new MissingConfigurationTemplateException("TemplateConfiguration is null while processing Element", element);
        }

        final String template;
        if (typeScriptTypeAnnotation == null || typeScriptTypeAnnotation.template().isEmpty()) {
            template = typeNodeTemplate;
        } else {
            template = typeScriptTypeAnnotation.template();
        }

        return template;
    }

    static String defineName(final TypeMirror typeMirror, final TypeScriptType typeScriptTypeAnnotation, final Function<TypeMirror, String> nameFunction) {
        //check if has a annotation and a type
        final String typeFromAnnotation = TypeNodeUtils.defineTypeFromAnnotation(typeScriptTypeAnnotation);
        if (!UNDEFINED_TYPE_NAME.equals(typeFromAnnotation)) {
            return typeFromAnnotation;
        }
        //forward to furter determination
        return nameFunction.apply(typeMirror);
    }

    static TypeMirror getObjectMirror(final Elements elementUtils) {
        if (objectMirror == null) {
            objectMirror = elementUtils.getTypeElement(JAVA_LANG_OBJECT).asType();
        }
        return objectMirror;
    }

    private static String defineTypeFromAnnotation(final TypeScriptType annotation) {
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        return UNDEFINED_TYPE_NAME;
    }

    /**
     * No Instances of this utility class.
     */
    private TypeNodeUtils() {
    }
}
