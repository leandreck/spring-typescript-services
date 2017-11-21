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

import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.leandreck.endpoints.processor.model.EnumValue;
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.model.TypeNodeFactory;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

/**
 * Concrete Factory for {@link ArrayTypeNode}.
 */
final class ArrayTypeNodeFactory implements ConcreteTypeNodeFactory {

    private final TypeNodeFactory typeNodeFactory;

    /**
     * Prototype Constructor for Registration in {@link TypeNodeKind}.
     */
    ArrayTypeNodeFactory() {
        typeNodeFactory = null;
    }

    private ArrayTypeNodeFactory(final TypeNodeFactory typeNodeFactory) {
        this.typeNodeFactory = typeNodeFactory;
    }

    @Override
    public ConcreteTypeNodeFactory newConfiguredInstance(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        return new ArrayTypeNodeFactory(typeNodeFactory);
    }

    @Override
    public TypeNode createTypeNode(final String fieldName, final String parameterName, final boolean optional, final TypeMirror typeMirror, final TypeMirror containingType) {
        final ArrayType arrayMirror = (ArrayType) typeMirror;
        final TypeMirror componentMirror = arrayMirror.getComponentType();
        final TypeNode componentType = typeNodeFactory.createTypeNode(fieldName, parameterName, componentMirror, containingType);
        return new ArrayTypeNode(optional, componentType);
    }

    private final class ArrayTypeNode extends TypeNode {

        private final TypeNode componentType;

        private ArrayTypeNode(final boolean optional, final TypeNode componentType) {
            super(optional);
            this.componentType = componentType;
        }

        @Override
        public String getFieldName() {
            return componentType.getFieldName();
        }

        @Override
        public String getParameterName() {
            return componentType.getParameterName();
        }

        @Override
        public String getTypeName() {
            return componentType.getTypeName();
        }

        @Override
        public String getType() {
            return componentType.getType() + "[]";
        }

        @Override
        public String getTemplate() {
            return componentType.getTemplate();
        }

        @Override
        public boolean isMappedType() {
            return componentType.isMappedType();
        }

        @Override
        public TypeNodeKind getKind() {
            return TypeNodeKind.ARRAY;
        }

        @Override
        public List<TypeNode> getTypeParameters() {
            return componentType.getTypeParameters();
        }

        @Override
        public List<TypeNode> getChildren() {
            return componentType.getChildren();
        }

        @Override
        public Set<TypeNode> getTypes() {
            return componentType.getTypes();
        }

        @Override
        public Set<EnumValue> getEnumValues() {
            return componentType.getEnumValues();
        }

        @Override
        public boolean isDeclaredComplexType() {
            return componentType.isDeclaredComplexType();
        }
    }
}
