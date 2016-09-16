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

import java.util.Collections;
import java.util.List;

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
    private final List<TypeNode> children;

    public TypeNode(final String fieldName, final String typeName, final TypeNodeKind kind) {
        this.fieldName = fieldName;
        this.typeName = typeName;
        this.kind = kind;
        template = "";
        children = Collections.emptyList();
        mappedType = true;
        type = defineType();
    }

    public TypeNode(final String fieldName, final String typeName, final String template, final TypeNodeKind kind, final List<TypeNode> children) {
        this.fieldName = fieldName;
        this.typeName = typeName;
        this.template = template;
        this.kind = kind;
        this.children = children;
        mappedType = false;
        type = defineType();
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
                final String[] types = typeName.split("/");
                final String keyName = mappedType ? "I" + types[0] : types[0];
                final String valueName = mappedType ? "I" + types[1] : types[1];
                name = "{ [index: " + keyName + "]: " + valueName + " }";
                break;
            default:
                name = typeName;
        }

        return name;
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
}
