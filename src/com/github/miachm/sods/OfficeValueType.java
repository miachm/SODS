package com.github.miachm.sods;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * see
 * http://docs.oasis-open.org/office/v1.2/part1/cd04/OpenDocument-v1.2-part1-cd04.html#a_19_387_office_value-type
 */
enum OfficeValueType {

    BOOLEAN("boolean", Boolean.class) {
        @Override
        public Object read(XmlReaderInstance reader) {
            String raw = reader.getAttribValue("office:boolean-value");
            if (raw == null) {
                return null;
            }
            switch (raw) {
                case "true":
                    return true;
                case "false":
                    return false;
                default:
                    return null;
            }
        }

        @Override
        public void write(Object value, XMLStreamWriter writer) throws XMLStreamException {
            if (value instanceof Boolean) {
                writer.writeAttribute("office:value-type", getId());
                writer.writeAttribute("office:boolean-value", value.toString());
            }
        }
    },
    CURRENCY("currency") {
        @Override
        public Object read(XmlReaderInstance reader) {
            return null; // TODO
        }

        @Override
        public void write(Object value, XMLStreamWriter writer) throws XMLStreamException {
            // TODO
        }
    },
    DATE("date", LocalDateTime.class, LocalDate.class) {
        @Override
        public Object read(XmlReaderInstance reader) {
            String raw = reader.getAttribValue("office:date-value");
            if (raw == null) {
                return null;
            }
            try {
                return LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException dateTimeEx) {
                try {
                    return LocalDate.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException dateEx) {
                    return null;
                }
            }
        }

        @Override
        public void write(Object value, XMLStreamWriter writer) throws XMLStreamException {
            if (value instanceof LocalDateTime) {
                writer.writeAttribute("office:value-type", getId());
                writer.writeAttribute("office:date-value", ((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else if (value instanceof LocalDate) {
                writer.writeAttribute("office:value-type", getId());
                writer.writeAttribute("office:date-value", ((LocalDate) value).format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
        }
    },
    FLOAT("float", Number.class) {
        @Override
        public Object read(XmlReaderInstance reader) {
            String raw = reader.getAttribValue("office:value");
            if (raw == null) {
                return null;
            }
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        @Override
        public void write(Object value, XMLStreamWriter writer) throws XMLStreamException {
            if (value instanceof Number) {
                writer.writeAttribute("office:value-type", getId());
                writer.writeAttribute("office:value", value.toString());
            }
        }
    },
    PERCENTAGE("percentage") {
        @Override
        public Object read(XmlReaderInstance reader) {
//            String raw = reader.getAttribValue("office:value");
            return null; // TODO
        }

        @Override
        public void write(Object value, XMLStreamWriter writer) throws XMLStreamException {
            // TODO
        }
    },
    STRING("string", String.class) {
        @Override
        public Object read(XmlReaderInstance reader) {
            return reader.getAttribValue("office:string-value");
        }

        @Override
        public void write(Object value, XMLStreamWriter writer) throws XMLStreamException {
            // write as text instead of attribute
        }
    },
    TIME("time", Duration.class) {
        @Override
        public Object read(XmlReaderInstance reader) {
            String raw = reader.getAttribValue("office:time-value");
            if (raw == null) {
                return null;
            }
            try {
                return Duration.parse(raw);
            } catch (DateTimeParseException dateEx) {
                return null;
            }
        }

        @Override
        public void write(Object value, XMLStreamWriter writer) throws XMLStreamException {
            if (value instanceof Duration) {
                writer.writeAttribute("office:value-type", getId());
                writer.writeAttribute("office:time-value", value.toString());
            }
        }
    },
    VOID("void") {
        @Override
        public Object read(XmlReaderInstance reader) {
            return null;
        }

        @Override
        public void write(Object value, XMLStreamWriter writer) throws XMLStreamException {
            // nothing to do
        }
    };

    private final String id;
    private final Class<?>[] javaTypes;

    private OfficeValueType(String id, Class<?>... javaTypes) {
        this.id = id;
        this.javaTypes = javaTypes;
    }

    public String getId() {
        return id;
    }

    public boolean canHandle(Class<?> type) {
        for (Class<?> javaType : javaTypes) {
            if (javaType.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads the current cell value.
     *
     * @param reader input
     * @return a value if possible, null otherwise
     */
    abstract public Object read(XmlReaderInstance reader);

    /**
     * Writes the current cell value
     *
     * @param value a non-null value
     * @param writer output
     * @throws XMLStreamException
     */
    abstract public void write(Object value, XMLStreamWriter writer) throws XMLStreamException;

    public static OfficeValueType ofReader(XmlReaderInstance reader) {
        String raw = reader.getAttribValue("office:value-type");
        return raw != null ? ofId(raw) : DEFAULT_VALUE;
    }

    public static OfficeValueType ofId(String id) {
        for (OfficeValueType valueType : values()) {
            if (valueType.getId().equals(id)) {
                return valueType;
            }
        }
        return DEFAULT_VALUE;
    }

    public static OfficeValueType ofJavaType(Class<?> type) {
        for (OfficeValueType valueType : values()) {
            if (valueType.canHandle(type)) {
                return valueType;
            }
        }
        return DEFAULT_VALUE;
    }

    // FIXME: should it be VOID instead?
    private static final OfficeValueType DEFAULT_VALUE = OfficeValueType.STRING;
}
