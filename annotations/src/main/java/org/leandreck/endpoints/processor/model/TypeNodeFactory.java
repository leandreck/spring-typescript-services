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
package org.leandreck.endpoints.processor.model;

import org.leandreck.endpoints.annotations.TypeScriptIgnore;
import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.leandreck.endpoints.processor.model.typefactories.ConcreteTypeNodeFactory;
import org.leandreck.endpoints.processor.model.typefactories.TypeNodeKind;
import org.leandreck.endpoints.processor.model.typefactories.TypeNodeUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 */
public final class TypeNodeFactory {

    private final Types typeUtils;
    private final Elements elementUtils;

    private final TemplateConfiguration configuration;

    private final Map<TypeNodeKind, ConcreteTypeNodeFactory> factories;

    TypeNodeFactory(final TemplateConfiguration configuration,
                    final Types typeUtils,
                    final Elements elementUtils) {
        this.configuration = configuration;
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.factories = initFactories();
    }

    private Map<TypeNodeKind, ConcreteTypeNodeFactory> initFactories() {
        final Map<TypeNodeKind, ConcreteTypeNodeFactory> tmpFactories = new EnumMap<>(TypeNodeKind.class);
        Arrays.stream(TypeNodeKind.values()).forEach(it -> {
            try {
                tmpFactories.put(it, it.getTypeNodeFactory().newConfiguredInstance(this, this.configuration, this.typeUtils, this.elementUtils));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
        return tmpFactories;
    }

    /**
     * Factory Method to create new Root-TypeNodes like Returnvalues of Methods.
     *
     * @param typeMirror {@link TypeMirror} of Returnvalue or Parameter.
     * @return created {@link TypeNode} from given typeMirror
     */
    public TypeNode createTypeNode(final TypeMirror typeMirror) {
        return createTypeNode(typeMirror, null);
    }

    /**
     * Factory Method to create new Root-TypeNodes like Generics.
     *
     * @param typeMirror {@link TypeMirror} of TypeVariable.
     * @param containingType {@link TypeMirror} of the Type containing this {@link TypeNode}
     * @return created {@link TypeNode} from given typeMirror
     */
    public TypeNode createTypeNode(final TypeMirror typeMirror, final DeclaredType containingType) {
        final String fieldName = "TYPE-ROOT";
        return initType(fieldName, null, false, typeMirror, containingType);
    }

    /**
     * Factory Method to create a new TypeNode.
     *
     * @param fieldName {@link TypeNode#getFieldName()}
     * @param parameterName {@link TypeNode#getParameterName()}
     * @param typeMirror {@link TypeMirror} of the {@link TypeNode} to create.
     * @param containingType {@link TypeMirror} of the Type containing this {@link TypeNode}
     * @return concrete Instance of a {@link TypeNode}
     */
    public TypeNode createTypeNode(final String fieldName, final String parameterName, final TypeMirror typeMirror, final DeclaredType containingType) {
        return initType(fieldName, parameterName, false, typeMirror, containingType);
    }

    /**
     * Factory Method to create new TypeNodes from Methodparameters or Children-TypeNodes of TypeNodes.
     * TypeNodes created from {@link VariableElement} include the name of the Field in which they were encountered.
     *
     * @param variableElement {@link VariableElement} of Methodparameter or Field.
     * @param containingType {@link TypeMirror} of the Type containing this {@link TypeNode}
     * @return created {@link TypeNode} from given variableElement
     */
    TypeNode createTypeNode(final VariableElement variableElement, final String parameterName, final DeclaredType containingType) {
        final TypeMirror typeMirror = variableElement.asType();
        final String fieldName = variableElement.getSimpleName().toString();
        return initType(fieldName, parameterName, VariableAnnotations.isOptionalByAnnotation(variableElement), typeMirror, containingType);
    }

    public List<TypeNode> defineChildren(final TypeElement typeElement, final DeclaredType typeMirror) {
        final List<String> publicGetter = definePublicGetter(typeElement, typeMirror, typeUtils);

        return ElementFilter.fieldsIn(typeElement.getEnclosedElements()).stream()
                .filter(c -> c.getAnnotation(TypeScriptIgnore.class) == null)
                .filter(c -> !c.getModifiers().contains(Modifier.TRANSIENT))
                .filter(c -> filterVariableElements(c, publicGetter))
                .map(it -> this.createTypeNode(it, /* parameterName */ null, typeMirror))
                .collect(toList());
    }

    private static List<String> definePublicGetter(final TypeElement typeElement, final TypeMirror typeMirror, final Types typeUtils) {

        final List<String> publicGetters = ElementFilter.methodsIn(typeElement.getEnclosedElements()).stream()
                .filter(g -> g.getSimpleName().toString().startsWith("get") || g.getSimpleName().toString().startsWith("is"))
                .filter(g -> g.getModifiers().contains(Modifier.PUBLIC))
                .filter(g -> !g.getModifiers().contains(Modifier.ABSTRACT))//FIXME filter remaining modifiers
                .map(g -> g.getSimpleName().toString())
                .collect(toList());

        if (isLombokAnnotatedType(typeMirror, typeUtils)) {
            ElementFilter.fieldsIn(typeElement.getEnclosedElements()).stream()
                    .filter(g -> !g.getModifiers().contains(Modifier.STATIC))
                    .map(g -> g.getSimpleName().toString())
                    .forEach(publicGetters::add);
        }

        return publicGetters;
    }

    @SuppressWarnings("unchecked")
    private static boolean isLombokAnnotatedType(final TypeMirror typeMirror, final Types typeUtils) {
        return Arrays.stream(new String[]{"lombok.Data", "lombok.Value", "lombok.Getter"})
                .anyMatch(annotationName -> {
                    try {
                        Class dataAnnotationClass = Class.forName(annotationName);
                        Object dataAnnotation = TypeNodeUtils.getAnnotationForClass(typeMirror, dataAnnotationClass, typeUtils);
                        return (dataAnnotation != null);
                    } catch (Exception e) {
                        //ignored
                    }
                    return false;
                });
    }

    private boolean filterVariableElements(final VariableElement variableElement, final List<String> publicGetter) {
        return publicGetter.stream().map(g -> g.toLowerCase(Locale.ENGLISH).endsWith(variableElement.getSimpleName().toString().toLowerCase(Locale.ENGLISH)))
                .reduce(false, (a, b) -> a || b);
    }


    private TypeNode initType(final String fieldName, final String parameterName, final boolean optional, final TypeMirror typeMirror, final DeclaredType containingType) {
        try {
            final TypeNodeKind typeNodeKind = defineKind(typeMirror);
            final ConcreteTypeNodeFactory nodeFactory = factories.get(typeNodeKind);
            return nodeFactory.createTypeNode(fieldName, parameterName, optional, typeMirror, containingType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private TypeNodeKind defineKind(final TypeMirror typeMirror) {
        if (typeMirror == null) {
            return TypeNodeKind.NULL;
        }

        final TypeKind kind = typeMirror.getKind();
        final TypeNodeKind typeNodeKind;

        switch (kind) {
            case ARRAY:
                typeNodeKind = TypeNodeKind.ARRAY;
                break;

            case TYPEVAR:
                typeNodeKind = TypeNodeKind.TYPEVAR;
                break;

            case DECLARED:
                typeNodeKind = defineDeclaredTypeNodeKind(typeMirror);
                break;

            default:
                if (TypeNodeKind.containsMapping(kind.name())) {
                    typeNodeKind = TypeNodeKind.MAPPED;
                } else {
                    typeNodeKind = TypeNodeKind.SIMPLE;
                }
                break;
        }

        return typeNodeKind;
    }

    private TypeNodeKind defineDeclaredTypeNodeKind(final TypeMirror typeMirror) {
        final ElementKind elementKind = typeUtils.asElement(typeMirror).getKind();
        final TypeMirror collectionMirror = elementUtils.getTypeElement("java.util.Collection").asType();
        final TypeMirror mapMirror = typeUtils.getDeclaredType(elementUtils.getTypeElement("java.util.Map"));

        final TypeNodeKind typeNodeKind;
        if (ElementKind.ENUM.equals(elementKind)) {
            typeNodeKind = TypeNodeKind.ENUM;
        } else if (typeUtils.isAssignable(typeMirror, typeUtils.erasure(collectionMirror))) {
            typeNodeKind = TypeNodeKind.COLLECTION;
        } else if (typeUtils.isAssignable(typeMirror, typeUtils.erasure(mapMirror))) {
            typeNodeKind = TypeNodeKind.MAP;
        } else {
            final DeclaredType declaredType = (DeclaredType) typeMirror;
            if (TypeNodeKind.containsMapping(declaredType.asElement().getSimpleName().toString())) {
                typeNodeKind = TypeNodeKind.MAPPED;
            } else if (declaredType.asElement().asType().toString().equals("java.util.Optional<T>")
                    || declaredType.asElement().asType().toString().equals("org.springframework.http.ResponseEntity<T>")) {
                typeNodeKind = TypeNodeKind.OPTIONAL;
            } else {
                typeNodeKind = TypeNodeKind.SIMPLE;
            }
        }
        return typeNodeKind;
    }
}
