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
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.model.TypeNodeFactory;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.leandreck.endpoints.processor.model.typefactories.TypeNodeUtils.NO_TEMPLATE;

class MappedTypeNodeFactory implements ConcreteTypeNodeFactory {

    private final Types typeUtils;

    /**
     * Prototype Constructor for Registration in {@link TypeNodeKind}.
     */
    MappedTypeNodeFactory() {
        typeUtils = null;
    }

    private MappedTypeNodeFactory(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        this.typeUtils = typeUtils;
    }

    @Override
    public ConcreteTypeNodeFactory newConfiguredInstance(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        return new MappedTypeNodeFactory(typeNodeFactory, configuration, typeUtils, elementUtils);
    }

    @Override
    public TypeNode createTypeNode(final String fieldName, final String parameterName, final boolean optional, final TypeMirror typeMirror, final TypeMirror containingType) {
        final TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
        final TypeScriptType typeScriptTypeAnnotation = TypeNodeUtils.getAnnotationForClass(typeMirror, TypeScriptType.class, typeUtils);
        final String typeName = TypeNodeUtils.defineName(typeMirror, typeScriptTypeAnnotation, this::defineNameFromMapped);

        return new MappedTypeNode(optional, fieldName, parameterName, typeName);
    }

    private String defineNameFromMapped(final TypeMirror typeMirror) {
        final TypeKind kind = typeMirror.getKind();
        final String typeName;
        if (kind.isPrimitive() || TypeKind.VOID.equals(kind)) {
            typeName = TypeNodeKind.getMapping(kind.name());
        } else {
            final String key = typeUtils.asElement(typeMirror).getSimpleName().toString();
            typeName = TypeNodeKind.getMapping(key);
        }
        return typeName;
    }

    class MappedTypeNode extends TypeNode {

        private final String fieldName;
        private final String parameterName;
        private final String typeName;

        MappedTypeNode(final boolean optional,
                       final String fieldName,
                       final String parameterName,
                       final String typeName) {
            super(optional);
            this.fieldName = fieldName;
            this.parameterName = parameterName;
            this.typeName = typeName;
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
            return NO_TEMPLATE;
        }

        @Override
        public boolean isMappedType() {
            return true;
        }

        @Override
        public TypeNodeKind getKind() {
            return TypeNodeKind.MAPPED;
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

    }
}
