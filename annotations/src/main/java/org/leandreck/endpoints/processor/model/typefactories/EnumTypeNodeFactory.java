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
/*
  Copyright © 2016 Mathias Kowalzik (Mathias.Kowalzik@leandreck.org)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package org.leandreck.endpoints.processor.model.typefactories;

import org.leandreck.endpoints.annotations.TypeScriptType;
import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.leandreck.endpoints.processor.model.EnumValue;
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.model.TypeNodeFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Concrete Factory for {@link EnumTypeNode}.
 */
final class EnumTypeNodeFactory implements ConcreteTypeNodeFactory {

    private final TypeNodeFactory typeNodeFactory;
    private final TemplateConfiguration configuration;
    private final Types typeUtils;
    private final TypeMirror objectMirror;

    /**
     * Prototype Constructor for Registration in {@link TypeNodeKind}.
     */
    EnumTypeNodeFactory() {
        typeUtils = null;
        configuration = null;
        typeNodeFactory = null;
        objectMirror = null;
    }

    private EnumTypeNodeFactory(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        this.typeNodeFactory = typeNodeFactory;
        this.configuration = configuration;
        this.typeUtils = typeUtils;
        this.objectMirror = TypeNodeUtils.getObjectMirror(elementUtils);
    }

    @Override
    public ConcreteTypeNodeFactory newConfiguredInstance(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        return new EnumTypeNodeFactory(typeNodeFactory, configuration, typeUtils, elementUtils);
    }

    @Override
    public TypeNode createTypeNode(final String fieldName, final String parameterName, final boolean optional, final TypeMirror typeMirror, final TypeMirror containingType) {
        final TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
        final TypeScriptType typeScriptTypeAnnotation = TypeNodeUtils.getAnnotationForClass(typeMirror, TypeScriptType.class, typeUtils);

        return new EnumTypeNode(
                optional,
                fieldName,
                parameterName,
                TypeNodeUtils.defineName(typeMirror, typeScriptTypeAnnotation, this::defineNameForEnumType),
                defineTypeParameters(typeMirror),
                TypeNodeUtils.defineTemplate(configuration.getEnumTemplate(), typeScriptTypeAnnotation, typeElement),
                typeNodeFactory.defineChildren(typeElement, typeMirror),
                defineEnumValues(typeMirror));
    }

    private String defineNameForEnumType(final TypeMirror typeMirror) {
        return typeUtils.asElement(typeMirror).getSimpleName().toString();
    }

    private List<TypeNode> defineTypeParameters(final TypeMirror typeMirror) {
        final DeclaredType declaredType = (DeclaredType) typeMirror;
        final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        return typeArguments.stream()
                .map(t -> t.getKind().equals(TypeKind.WILDCARD) ? objectMirror : t)
                .map(typeNodeFactory::createTypeNode)
                .collect(toList());
    }

    private Set<EnumValue> defineEnumValues(final TypeMirror typeMirror) {
        final Element enumElement = typeUtils.asElement(typeMirror);
        if (enumElement == null) {
            return Collections.emptySet();
        }

        return enumElement.getEnclosedElements().stream()
                .filter(e -> ElementKind.ENUM_CONSTANT.equals(e.getKind()))
                .map(e -> new EnumValue(e.getSimpleName().toString()))
                .collect(Collectors.toSet());
    }

    private final class EnumTypeNode extends TypeNode {

        private final String fieldName;
        private final String parameterName;
        private final String typeName;
        private final List<TypeNode> typeParameters;
        private final String template;
        private final List<TypeNode> children;

        private final Set<EnumValue> enumValues;

        private EnumTypeNode(
                final boolean optional,
                final String fieldName,
                final String parameterName,
                final String typeName,
                final List<TypeNode> typeParameters,
                final String template,
                final List<TypeNode> children,
                final Set<EnumValue> enumValues) {

            super(optional);
            this.fieldName = fieldName;
            this.parameterName = parameterName;
            this.typeName = typeName;
            this.typeParameters = typeParameters;
            this.template = template;
            this.children = children;
            this.enumValues = enumValues;
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public String getParameterName() {
            return parameterName;
        }

        @Override
        public String getTypeName() {
            return typeName;
        }

        @Override
        public String getType() {
            return typeName;
        }

        @Override
        public String getTemplate() {
            return template;
        }

        @Override
        public TypeNodeKind getKind() {
            return TypeNodeKind.ENUM;
        }

        @Override
        public List<TypeNode> getTypeParameters() {
            return typeParameters;
        }

        @Override
        public List<TypeNode> getChildren() {
            return children;
        }

        @Override
        public Set<TypeNode> getTypes() {
            return Collections.singleton(this);
        }

        @Override
        public Set<EnumValue> getEnumValues() {
            return enumValues;
        }

    }
}
