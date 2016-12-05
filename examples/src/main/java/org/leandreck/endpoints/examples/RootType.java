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
import java.util.List;
import java.util.Map;

/**
 * Created by Mathias Kowalzik (Mathias.Kowalzik@leandreck.org) on 19.08.2016.
 */
@TypeScriptType("MyTypeScriptType")
public class RootType {

    private Long id;

    private String name;
    private String givenName;
    private int weight;

    private SimpleEnum[] simpleArray;

    @TypeScriptIgnore
    private BigDecimal ignoredField;

    private transient String transientField;

    private char noPublicGetter;

    private List<SubType> subTypeList;

    private Map<String, SubType> subTypeMap;

    private Map<?, MapValueType> mapValue;
    private Map<MapKeyType, ?> mapKey;

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

    public Map<String, SubType> getSubTypeMap() {
        return subTypeMap;
    }

    public void setSubTypeMap(Map<String, SubType> subTypeMap) {
        this.subTypeMap = subTypeMap;
    }

    public Map<?, MapValueType> getMapValue() {
        return mapValue;
    }

    public void setMapValue(Map<?, MapValueType> mapValue) {
        this.mapValue = mapValue;
    }

    public Map<MapKeyType, ?> getMapKey() {
        return mapKey;
    }

    public void setMapKey(Map<MapKeyType, ?> mapKey) {
        this.mapKey = mapKey;
    }

    public List<SubType> getSubTypeList() {
        return subTypeList;
    }

    public void setSubTypeList(List<SubType> subTypeList) {
        this.subTypeList = subTypeList;
    }

    public SimpleEnum[] getSimpleArray() {
        return simpleArray;
    }

    public void setSimpleArray(SimpleEnum[] simpleArray) {
        this.simpleArray = simpleArray;
    }
}
