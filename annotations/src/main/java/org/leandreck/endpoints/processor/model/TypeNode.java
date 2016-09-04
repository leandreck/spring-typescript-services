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
    private final String template;
    private final boolean mappedType;

    private final List<TypeNode> children;

    public TypeNode(final String fieldName, final String typeName) {
        this.fieldName = fieldName;
        this.typeName = typeName;
        this.template = "";
        this.children = Collections.emptyList();
        mappedType = true;
    }

    public TypeNode(final String fieldName, final String typeName, final String template, final List<TypeNode> children) {
        this.fieldName = fieldName;
        this.typeName = typeName;
        this.template = template;
        this.children = children;
        mappedType = false;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getTemplate() {
        return template;
    }

    List<TypeNode> getRealChildren() {
        return children;
    }

    public List<TypeNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeNode typeNode = (TypeNode) o;

        return typeName.equals(typeNode.typeName);

    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

    public boolean isMappedType() {
        return mappedType;
    }
}
