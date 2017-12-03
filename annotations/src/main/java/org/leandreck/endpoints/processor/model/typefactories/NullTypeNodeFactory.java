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
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.model.TypeNodeFactory;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.leandreck.endpoints.processor.model.typefactories.TypeNodeUtils.NO_TEMPLATE;

/**
 * Concrete Factory for {@link NullTypeNode}.
 */
final class NullTypeNodeFactory implements ConcreteTypeNodeFactory {

    /**
     * Prototype Constructor for Registration in {@link TypeNodeKind}.
     */
    NullTypeNodeFactory() {
    }

    @Override
    public ConcreteTypeNodeFactory newConfiguredInstance(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        return new NullTypeNodeFactory();
    }

    @Override
    public TypeNode createTypeNode(final String fieldName, final String parameterName, final boolean optional, final TypeMirror typeMirror, final TypeMirror containingType) {
        return new NullTypeNode(fieldName);
    }

    private final class NullTypeNode extends TypeNode {

        private final String fieldName;

        private NullTypeNode(final String fieldName) {
            super(false);
            this.fieldName = fieldName;
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public String getParameterName() {
            return null;
        }

        @Override
        public String getTypeName() {
            return "any | null";
        }

        @Override
        public String getType() {
            return getTypeName();
        }

        @Override
        public String getTemplate() {
            return NO_TEMPLATE;
        }

        @Override
        public boolean isMappedType() {
            return true;
        }

        @Override
        public TypeNodeKind getKind() {
            return TypeNodeKind.NULL;
        }

        @Override
        public List<TypeNode> getTypeParameters() {
            return Collections.emptyList();
        }

        @Override
        public List<TypeNode> getChildren() {
            return Collections.emptyList();
        }

        @Override
        public Set<TypeNode> getTypes() {
            return Collections.emptySet();
        }

        @Override
        public Set<TypeNode> getImports() {
            return Collections.emptySet();
        }
    }
}
