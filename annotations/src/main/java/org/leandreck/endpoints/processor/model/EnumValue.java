package org.leandreck.endpoints.processor.model;

/**
 * Created by kowalzik on 03.11.2016.
 */
public class EnumValue {

    private final String name;
    private final int ordinal;

    public EnumValue(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
