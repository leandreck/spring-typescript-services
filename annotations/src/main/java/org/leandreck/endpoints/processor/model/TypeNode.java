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

import org.leandreck.endpoints.processor.model.typefactories.TypeNodeKind;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 */
public abstract class TypeNode {

    private final boolean optional;

    protected TypeNode(final boolean optional) {
        this.optional = optional;
    }


    /**
     * {@link TypeNodeKind} of this TypeNode.
     *
     * @return {@link TypeNodeKind}
     */
    public abstract TypeNodeKind getKind();

    /**
     * Declared name of this Type as Methodparameter or Variable. For example
     * <code>private String someFieldName;</code> results in "someFieldName" as fieldName or
     * <code>public void execute(String anotherFieldName) {...}</code> in "anotherFieldName".
     * @return fieldname
     */
    public abstract String getFieldName();

    /**
     * Declared name or value in {@link org.springframework.web.bind.annotation.RequestParam} or
     * {@link org.springframework.web.bind.annotation.PathVariable} Annotation of this Type.<br>
     * <br>
     * For example:<br>
     * {@code public void delete(@PathVariable(name = "pathVariable") Long id, @RequestParam(name = "queryParam") String queryParameter){...}}<br>
     * results in "pathVariable" as parameterName for <code>id</code> and "queryParam" for <code>queryParameter</code>.
     * @return parametername
     */
    public abstract String getParameterName();

    /**
     * Returns the parameterName if set or the fieldName and appends an '?' if this TypeNode {@link #isOptional()} == true.
     * Templates can use this to declare Method-Parameters or use {@link #getAsVariableName()} and declare optional Parameters them self.
     *
     * @return variable name as function parameter
     */
    public String getAsFunctionParameter() {
        final String functionParameter = getParameterName() == null ? getFieldName() : getParameterName();
        return isOptional() ? functionParameter + '?' : functionParameter;
    }

    /**
     * Returns the parameterName if set or the fieldName and does not append an '?' if this TypeNode {@link #isOptional()} == true.
     * Templates should use this as Variable-Name if used not as Function-Parameter.
     *
     * @return variable name
     */
    public String getAsVariableName() {
        return getParameterName() == null ? getFieldName() : getParameterName();
    }

    /**
     * This is the raw name of this TypeNode without any decorations for collections or maps or bound Generics.<br>
     * Most of the time you should use {@link #getType()} in your templates.
     * For example:<br>
     * <br>
     * <ul>
     * <li>{@code Map<String, Number> -> 'Map'}</li>
     * <li>{@code List<GenericType<Innertype>> -> 'List'}</li>
     * </ul>
     *
     * @return name of this TypeNode.
     * @see #getType()
     */
    public abstract String getTypeName();

    /**
     * Returns the template-usable String representing this {@link TypeNode} with added decorations like '[]' as suffix for collections or
     * bound Generics.<br>
     * For example:<br>
     * <br>
     * <ul>
     * <li>{@code Map<String, Number> -> '{ [index: string]: number }'}</li>
     * <li>{@code List<GenericType<Innertype>> -> 'GenericType<InnerType>[]'}</li>
     * </ul>
     *
     * @return String representing this Instance of TypeNode
     * @see #getTypeName()
     */
    public abstract String getType();

    public abstract String getTemplate();

    /**
     * Typeparameters are bound Generics as in {@code GenericType<BoundType>}.
     * For example {@code Type<TypeOne, TypeTwo>} results in a List with the two Entries with TypeNodes for 'TypeOne' and 'TypeTwo'.
     * @return all bound TypeParameters
     */
    public abstract List<TypeNode> getTypeParameters();

    public abstract List<TypeNode> getChildren();

    public abstract Set<TypeNode> getTypes();

    public Set<EnumValue> getEnumValues() {
        return Collections.emptySet();
    }

    /**
     * Returns true if this TypeNode is a mapped Type.
     *
     * @return <code>true</code> if mapped
     * <code>false</code> otherwise
     */
    public boolean isMappedType() {
        return false;
    }

    public boolean isDeclaredComplexType() {
        return !(this.isMappedType()
                || (TypeNodeKind.MAP == this.getKind()));
    }

    /**
     * Returns wether this TypeNode is an optional Parameter or not.
     *
     * @return true if this TypeNode is wrapped in an {@link java.util.Optional} or is declared as not required in the respective Annotation<br>
     * false in all other cases.
     */
    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TypeNode typeNode = (TypeNode) o;
        return getTypeName().equals(typeNode.getTypeName());
    }

    @Override
    public int hashCode() {
        return getTypeName().hashCode();
    }
}
