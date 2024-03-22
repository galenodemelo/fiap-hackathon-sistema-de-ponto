package br.com.fiap.util;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;

public class HourSerializer extends ToStringSerializerBase {

    public HourSerializer() {
        this(Double.class);
    }

    public HourSerializer(Class<?> handledType) {
        super(handledType);
    }

    @Override
    public String valueToString(Object milliseconds) {
        long hours = ((long) milliseconds / (1000 * 60 * 60)) % 24;
        long minutes = ((long) milliseconds / (1000 * 60)) % 60;

        String formattedHours = String.format("%02d:%02d", hours, minutes);

        return formattedHours;
    }
}