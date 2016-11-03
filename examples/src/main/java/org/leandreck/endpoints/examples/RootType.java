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
package org.leandreck.endpoints.examples;

import org.leandreck.endpoints.annotations.TypeScriptIgnore;
import org.leandreck.endpoints.annotations.TypeScriptType;

import java.math.BigDecimal;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 19.08.2016.
 */
@TypeScriptType("MyTypeScriptType")
public class RootType {

    private Long id;

    private String name;
    private String givenName;
    private int weight;

    private SimpleEnum simple;

    @TypeScriptIgnore
    private BigDecimal ignoredField;

    private transient String transientField;

    private char noPublicGetter;

    private SubType subType;

    public RootType() {
        this.id = Long.MIN_VALUE;
    }

    public RootType(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGivenName() {
        return givenName;
    }

    public int getWeight() {
        return weight;
    }

    public BigDecimal getIgnoredField() {
        return ignoredField;
    }

    public String getTransientField() {
        return transientField;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubType getSubType() {
        return subType;
    }

    public void setSubType(SubType subType) {
        this.subType = subType;
    }

    public SimpleEnum getSimple() {
        return simple;
    }

    public void setSimple(SimpleEnum simple) {
        this.simple = simple;
    }
}
