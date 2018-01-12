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

import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.leandreck.endpoints.processor.model.EnumValue;
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.model.TypeNodeFactory;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

/**
 * Concrete Factory for {@link OptionalTypeNodeFactory}.
 */
final class OptionalTypeNodeFactory implements ConcreteTypeNodeFactory {

    private final TypeNodeFactory typeNodeFactory;
    private final TypeMirror objectMirror;

    /**
     * Prototype Constructor for Registration in {@link TypeNodeKind}.
     */
    OptionalTypeNodeFactory() {
        typeNodeFactory = null;
        objectMirror = null;
    }

    private OptionalTypeNodeFactory(final TypeNodeFactory typeNodeFactory, final Elements elementUtils) {
        this.typeNodeFactory = typeNodeFactory;
        this.objectMirror = TypeNodeUtils.getObjectMirror(elementUtils);
    }

    @Override
    public ConcreteTypeNodeFactory newConfiguredInstance(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        return new OptionalTypeNodeFactory(typeNodeFactory, elementUtils);
    }

    @Override
    public TypeNode createTypeNode(final String fieldName, final String parameterName, final boolean optional, final TypeMirror typeMirror, final DeclaredType containingType) {
        final TypeNode componentType = typeNodeFactory.createTypeNode(fieldName, parameterName, defineValueMirror(typeMirror), containingType);
        return new OptionalTypeNode(componentType);
    }

    private TypeMirror defineValueMirror(final TypeMirror typeMirror) {
        final DeclaredType declaredType = (DeclaredType) typeMirror;
        final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        return typeArguments.stream()
                .map(t -> t.getKind().equals(TypeKind.WILDCARD) ? objectMirror : t)
                .findFirst().orElse(objectMirror);
    }

    private final class OptionalTypeNode extends TypeNode {

        private final TypeNode valueType;

        private OptionalTypeNode(final TypeNode valueType) {
            super(true); //always true
            this.valueType = valueType;
        }

        @Override
        public String getFieldName() {
            return valueType.getFieldName();
        }

        @Override
        public String getParameterName() {
            return valueType.getParameterName();
        }

        @Override
        public String getTypeName() {
            return valueType.getTypeName();
        }

        @Override
        public String getType() {
            return valueType.getType();
        }

        @Override
        public String getTemplate() {
            return valueType.getTemplate();
        }

        @Override
        public boolean isMappedType() {
            return valueType.isMappedType();
        }

        @Override
        public TypeNodeKind getKind() {
            return TypeNodeKind.OPTIONAL;
        }

        @Override
        public List<TypeNode> getTypeParameters() {
            return valueType.getTypeParameters();
        }

        @Override
        public List<TypeNode> getChildren() {
            return valueType.getChildren();
        }

        @Override
        public Set<TypeNode> getTypes() {
            return valueType.getTypes();
        }

        @Override
        public Set<TypeNode> getImports() {
            return valueType.getImports();
        }

        @Override
        public Set<EnumValue> getEnumValues() {
            return valueType.getEnumValues();
        }

        @Override
        public boolean isDeclaredComplexType() {
            return valueType.isDeclaredComplexType();
        }
    }
}
