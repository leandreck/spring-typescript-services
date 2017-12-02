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

import org.leandreck.endpoints.processor.config.TemplateConfiguration;
import org.leandreck.endpoints.processor.model.EnumValue;
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.model.TypeNodeFactory;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

final class TypeVarTypeNodeFactory implements ConcreteTypeNodeFactory {

    private final TypeNodeFactory typeNodeFactory;
    private final TemplateConfiguration configuration;
    private final Types typeUtils;
    private final TypeMirror objectMirror;

    /**
     * Prototype Constructor for Registration in {@link TypeNodeKind}.
     */
    TypeVarTypeNodeFactory() {
        typeUtils = null;
        configuration = null;
        typeNodeFactory = null;
        objectMirror = null;
    }

    private TypeVarTypeNodeFactory(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        this.typeNodeFactory = typeNodeFactory;
        this.configuration = configuration;
        this.typeUtils = typeUtils;
        this.objectMirror = TypeNodeUtils.getObjectMirror(elementUtils);
    }

    @Override
    public ConcreteTypeNodeFactory newConfiguredInstance(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        return new TypeVarTypeNodeFactory(typeNodeFactory, configuration, typeUtils, elementUtils);
    }

    @Override
    public TypeNode createTypeNode(final String fieldName, final String parameterName, final boolean optional, final TypeMirror typeMirror, final TypeMirror containingType) {
        final Element element = typeUtils.asElement(typeMirror);
        final TypeMirror boundMirror = typeUtils.asMemberOf((DeclaredType) containingType, element);
        final TypeNode boundType = typeNodeFactory.createTypeNode(fieldName, parameterName, boundMirror, containingType);
        return new TypeVarTypeNode(optional, element.getSimpleName().toString(), boundType);
    }

    private final class TypeVarTypeNode extends TypeNode {

        private final String typeNameVariable;
        private final TypeNode boundType;

        private TypeVarTypeNode(final boolean optional, final String typeNameVariable, final TypeNode boundType) {
            super(optional);
            this.typeNameVariable = typeNameVariable;
            this.boundType = boundType;
        }

        @Override
        public String getFieldName() {
            return boundType.getFieldName();
        }

        @Override
        public String getParameterName() {
            return boundType.getParameterName();
        }

        @Override
        public String getTypeName() {
            return boundType.getTypeName();
        }

        @Override
        public String getType() {
            return boundType.getType();
        }

        @Override
        public String getTemplate() {
            return boundType.getTemplate();
        }

        @Override
        public boolean isMappedType() {
            return boundType.isMappedType();
        }

        @Override
        public TypeNodeKind getKind() {
            return TypeNodeKind.TYPEVAR;
        }

        @Override
        public List<TypeNode> getTypeParameters() {
            return boundType.getTypeParameters();
        }

        @Override
        public List<TypeNode> getChildren() {
            return boundType.getChildren();
        }

        @Override
        public Set<TypeNode> getTypes() {
            return boundType.getTypes();
        }

        @Override
        public Set<TypeNode> getImports() {
            return boundType.getImports();
        }

        @Override
        public Set<EnumValue> getEnumValues() {
            return boundType.getEnumValues();
        }

        @Override
        public boolean isDeclaredComplexType() {
            return boundType.isDeclaredComplexType();
        }

        @Override
        public String getTypeNameVariable() {
            return typeNameVariable;
        }
    }
}
