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
import org.leandreck.endpoints.processor.model.TypeNode;
import org.leandreck.endpoints.processor.model.TypeNodeFactory;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.leandreck.endpoints.processor.model.typefactories.TypeNodeUtils.NO_TEMPLATE;

/**
 */
final class MapTypeNodeFactory implements ConcreteTypeNodeFactory {

    private final TypeNodeFactory typeNodeFactory;
    private final Types typeUtils;
    private final TypeMirror objectMirror;

    /**
     * Prototype Constructor for Registration in {@link TypeNodeKind}.
     */
    MapTypeNodeFactory() {
        typeUtils = null;
        typeNodeFactory = null;
        objectMirror = null;
    }

    private MapTypeNodeFactory(final TypeNodeFactory typeNodeFactory,
                               final Types typeUtils,
                               final Elements elementUtils) {
        this.typeNodeFactory = typeNodeFactory;
        this.typeUtils = typeUtils;
        objectMirror = TypeNodeUtils.getObjectMirror(elementUtils);
    }

    @Override
    public ConcreteTypeNodeFactory newConfiguredInstance(final TypeNodeFactory typeNodeFactory, final TemplateConfiguration configuration, final Types typeUtils, final Elements elementUtils) {
        return new MapTypeNodeFactory(typeNodeFactory, typeUtils, elementUtils);
    }

    @Override
    public TypeNode createTypeNode(final String fieldName, final String parameterName, final boolean optional, final TypeMirror typeMirror, final DeclaredType containingType) {
        final List<TypeNode> typeParameters = defineTypeParameters(typeMirror, containingType);
        final TypeScriptType typeScriptTypeAnnotation = TypeNodeUtils.getAnnotationForClass(typeMirror, TypeScriptType.class, typeUtils);
        final String typeName = TypeNodeUtils.defineName(typeMirror, typeScriptTypeAnnotation, it -> defineNameFromMapType(typeParameters));
        return new MapTypeNode(optional, fieldName, parameterName, typeName, typeParameters);
    }

    private List<TypeNode> defineTypeParameters(final TypeMirror typeMirror, final DeclaredType containingType) {
        final List<TypeNode> typeParameters;
        final DeclaredType declaredType = (DeclaredType) typeMirror;
        final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        typeParameters = typeArguments.stream()
                .map(it -> it.getKind().equals(TypeKind.WILDCARD) ? objectMirror : it)
                .map(it -> typeNodeFactory.createTypeNode(it, containingType))
                .collect(toList());

        if (typeParameters.isEmpty()) {
            typeParameters.add(typeNodeFactory.createTypeNode(objectMirror));
            typeParameters.add(typeNodeFactory.createTypeNode(objectMirror));
        }
        return typeParameters;
    }

    private String defineNameFromMapType(final List<TypeNode> typeParameters) {
        final TypeNode key = typeParameters.get(0);
        final TypeNode value = typeParameters.get(1);

        final String keyName = key.getTypeName();
        final String valueName = value.getTypeName();

        return keyName + "/" + valueName;
    }

    class MapTypeNode extends TypeNode {

        private final String fieldName;
        private final String parameterName;
        private final String typeName;
        private final String type;
        private final List<TypeNode> typeParameters;

        //lazy
        private Set<TypeNode> imports;
        private Set<TypeNode> types;

        MapTypeNode(final boolean optional,
                    final String fieldName,
                    final String parameterName,
                    final String typeName,
                    final List<TypeNode> typeParameters) {

            super(optional);
            this.fieldName = fieldName;
            this.parameterName = parameterName;
            this.typeName = typeName;
            this.type = "{ [index: " + typeParameters.get(0).getType() + "]: " + typeParameters.get(1).getType() + " }";
            this.typeParameters = typeParameters;
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
            return type;
        }

        @Override
        public String getTemplate() {
            return NO_TEMPLATE;
        }

        @Override
        public TypeNodeKind getKind() {
            return TypeNodeKind.MAP;
        }

        @Override
        public List<TypeNode> getTypeParameters() {
            return typeParameters;
        }

        @Override
        public List<TypeNode> getChildren() {
            return Collections.emptyList();
        }

        @Override
        public Set<TypeNode> getTypes() {
            if (types == null) {
                types = typeParameters.stream().filter(it -> !it.isMappedType()).flatMap(it -> it.getTypes().stream()).collect(toSet());
            }
            return types;
        }

        @Override
        public Set<TypeNode> getImports() {
            if (imports == null) {
                imports = typeParameters.stream().filter(it -> !it.isMappedType()).flatMap(it -> it.getImports().stream()).collect(toSet());
            }
            return imports;
        }
    }
}
