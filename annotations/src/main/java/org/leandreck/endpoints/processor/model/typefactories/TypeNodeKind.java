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

import java.util.HashMap;
import java.util.Map;

/**
 */
public enum TypeNodeKind {

    SIMPLE(SimpleTypeNodeFactory.class),
    ARRAY(ArrayTypeNodeFactory.class),
    COLLECTION(CollectionTypeNodeFactory.class),
    MAP(MapTypeNodeFactory.class),
    ENUM(EnumTypeNodeFactory.class),
    MAPPED(MappedTypeNodeFactory.class),
    OPTIONAL(OptionalTypeNodeFactory.class),
    TYPEVAR(TypeVarTypeNodeFactory.class);

    private static final String NUMBER_TYPE = "number";
    private static final String STRING_TYPE = "string";
    private static final String BOOLEAN_TYPE = "boolean";
    private static final Map<String, String> mappings = new HashMap<>(20);

    static {
        //Void
        mappings.put("VOID", "void");

        //Number
        mappings.put("BYTE", NUMBER_TYPE);
        mappings.put("Byte", NUMBER_TYPE);
        mappings.put("SHORT", NUMBER_TYPE);
        mappings.put("Short", NUMBER_TYPE);
        mappings.put("INT", NUMBER_TYPE);
        mappings.put("Integer", NUMBER_TYPE);
        mappings.put("LONG", NUMBER_TYPE);
        mappings.put("Long", NUMBER_TYPE);
        mappings.put("FLOAT", NUMBER_TYPE);
        mappings.put("Float", NUMBER_TYPE);
        mappings.put("DOUBLE", NUMBER_TYPE);
        mappings.put("Double", NUMBER_TYPE);
        mappings.put("BigDecimal", NUMBER_TYPE);
        mappings.put("BigInteger", NUMBER_TYPE);

        //String
        mappings.put("CHAR", STRING_TYPE);
        mappings.put("Character", STRING_TYPE);
        mappings.put("String", STRING_TYPE);

        //Boolean
        mappings.put("BOOLEAN", BOOLEAN_TYPE);
        mappings.put("Boolean", BOOLEAN_TYPE);

        //Date
        mappings.put("Date", "Date");
        mappings.put("LocalDate", "Date");

        //any
        mappings.put("Object", "any");
    }


    private final ConcreteTypeNodeFactory typeNodeFactory;

    TypeNodeKind(final Class<? extends ConcreteTypeNodeFactory> typeNodeFactory) {
        ConcreteTypeNodeFactory tmpFactory = null;
        try {
            tmpFactory = typeNodeFactory.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.typeNodeFactory = tmpFactory;
    }

    public ConcreteTypeNodeFactory getTypeNodeFactory() {
        return typeNodeFactory;
    }

    public static boolean containsMapping(final String key) {
        return mappings.containsKey(key);
    }

    public static String getMapping(final String key) {
        return mappings.get(key);
    }
}

