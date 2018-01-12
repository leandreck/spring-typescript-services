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
import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.leandreck.endpoints.processor.model.EnumValue;
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.model.TypeNodeFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Concrete Factory for {@link EnumTypeNode}.
 */
final class EnumTypeNodeFactory implements ConcreteTypeNodeFactory {

    private final TypeNodeFactory typeNodeFactory;
    private final TemplateConfiguration configuration;
    private final Types typeUtils;

    /**
     * Prototype Constructor for Registration in {@link TypeNodeKind}.
     */
    EnumTypeNodeFactory() {
        typeUtils = null;
        configuration = null;
        typeNodeFactory = null;
    }

    private EnumTypeNodeFactory(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils) {
        this.typeNodeFactory = typeNodeFactory;
        this.configuration = configuration;
        this.typeUtils = typeUtils;
    }

    @Override
    public ConcreteTypeNodeFactory newConfiguredInstance(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        return new EnumTypeNodeFactory(typeNodeFactory, configuration, typeUtils);
    }

    @Override
    public TypeNode createTypeNode(final String fieldName, final String parameterName, final boolean optional, final TypeMirror typeMirror, final DeclaredType containingType) {
        final TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
        final TypeScriptType typeScriptTypeAnnotation = TypeNodeUtils.getAnnotationForClass(typeMirror, TypeScriptType.class, typeUtils);

        return new EnumTypeNode(
                optional,
                fieldName,
                parameterName,
                TypeNodeUtils.defineName(typeMirror, typeScriptTypeAnnotation, this::defineNameForEnumType),
                TypeNodeUtils.defineTemplate(configuration.getEnumTemplate(), typeScriptTypeAnnotation, typeElement),
                typeNodeFactory.defineChildren(typeElement, (DeclaredType) typeMirror),
                defineEnumValues(typeMirror));
    }

    private String defineNameForEnumType(final TypeMirror typeMirror) {
        return typeUtils.asElement(typeMirror).getSimpleName().toString();
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
        private final String template;
        private final List<TypeNode> children;
        private final Set<TypeNode> imports;
        private final Set<TypeNode> types;

        private final Set<EnumValue> enumValues;

        private EnumTypeNode(
                final boolean optional,
                final String fieldName,
                final String parameterName,
                final String typeName,
                final String template,
                final List<TypeNode> children,
                final Set<EnumValue> enumValues) {

            super(optional);
            this.fieldName = fieldName;
            this.parameterName = parameterName;
            this.typeName = typeName;
            this.template = template;
            this.children = children;
            this.enumValues = enumValues;
            this.imports = collectImports();
            this.types = collectTypes();
        }

        private Set<TypeNode> collectImports() {
            final Set<TypeNode> nodesSet = new HashSet<>(children.size() + 5);
            children.stream().filter(it -> !it.isMappedType()).flatMap(it -> it.getTypes().stream()).forEach(nodesSet::add);
            return Collections.unmodifiableSet(nodesSet);
        }

        private Set<TypeNode> collectTypes() {
            final Set<TypeNode> nodesSet = new HashSet<>(imports.size() + 5);
            nodesSet.add(this);
            nodesSet.addAll(imports);
            return nodesSet;
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
            return getTypeName();
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
            return Collections.emptyList();
        }

        @Override
        public List<TypeNode> getChildren() {
            return children;
        }

        @Override
        public Set<TypeNode> getTypes() {
            return types;
        }

        @Override
        public Set<TypeNode> getImports() {
            return imports;
        }

        @Override
        public Set<EnumValue> getEnumValues() {
            return enumValues;
        }

    }
}
