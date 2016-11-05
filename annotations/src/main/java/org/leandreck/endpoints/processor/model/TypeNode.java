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

import java.util.*;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 27.08.2016.
 */
public class TypeNode {

    private final String fieldName;

    private final String typeName;
    private final String type;
    private final String template;
    private final boolean mappedType;
    private final TypeNodeKind kind;
    private final List<TypeNode> typeParameters;
    private final List<TypeNode> children;
    private final Set<TypeNode> types;
    private final Set<EnumValue> enumValues;
    private final boolean isDeclaredComplexType;

    public TypeNode(final String fieldName, final String typeName, final TypeNodeKind kind) {
        this.fieldName = fieldName;
        this.typeName = typeName;
        this.kind = kind;
        typeParameters = Collections.emptyList();
        template = "";
        children = Collections.emptyList();
        mappedType = true;
        type = defineType();
        types = collectTypes();
        isDeclaredComplexType = false;
        enumValues = Collections.emptySet();
    }

    public TypeNode(final String fieldName, final String typeName, final List<TypeNode> typeParameters, final String template, final TypeNodeKind kind, final List<TypeNode> children, final Set<EnumValue> enumValues) {
        this.fieldName = fieldName;
        this.typeName = typeName;
        this.typeParameters = typeParameters;
        this.template = template;
        this.kind = kind;
        this.children = children;
        this.enumValues = enumValues;
        mappedType = false;
        type = defineType();
        types = collectTypes();
        isDeclaredComplexType = defineIsDeclaredComplexType();
    }

    private boolean defineIsDeclaredComplexType() {
        final boolean isDeclared;
        isDeclared = !(this.isMappedType()
                || TypeNodeKind.MAP.equals(this.getKind()));
        return isDeclared;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTypeName() {
        return typeName;
    }

    private String defineType() {
        final String name;
        switch (kind) {
            case SIMPLE:
                name = typeName;
                break;
            case ARRAY:
            case COLLECTION:
                name = typeName + "[]";
                break;
            case MAP:
                name = "{ [index: " + typeParameters.get(0).type + "]: " + typeParameters.get(1).type + " }";
                break;
            default:
                name = typeName;
                break;
        }

        return name;
    }

    private Set<TypeNode> collectTypes() {
        final Map<String, TypeNode> typeMap = new HashMap<>();
        children.forEach(t -> typeMap.put(t.getTypeName(), t));
        typeParameters.forEach(t -> typeMap.put(t.getTypeName(), t));
        return new HashSet<>(typeMap.values());
    }

    public String getTemplate() {
        return template;
    }

    public List<TypeNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public TypeNodeKind getKind() {
        return kind;
    }

    public String getType() {
        return type;
    }

    public boolean isMappedType() {
        return mappedType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TypeNode typeNode = (TypeNode) o;

        return typeName.equals(typeNode.typeName);

    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

    public List<TypeNode> getTypeParameters() {
        return typeParameters;
    }

    public Set<TypeNode> getTypes() {
        return types;
    }


    public boolean isDeclaredComplexType() {
        return isDeclaredComplexType;
    }

    public Set<EnumValue> getEnumValues() {
        return enumValues;
    }
}
