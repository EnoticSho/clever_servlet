package clevertec.utils.xmlserializer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class XmlSerializer {

    public String serialize(Object object) {
        return serializeInternal(object, 0, true);
    }

    private String serializeInternal(Object object, int depth, boolean isRootObject) {
        if (object == null) {
            return indent(depth) + "<null/>";
        }

        Field[] declaredFields = object.getClass().getDeclaredFields();
        String name = isRootObject ? object.getClass().getSimpleName() : "";
        Map<String, Object> stringObjectMap = new LinkedHashMap<>();
        for (final Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                stringObjectMap.put(declaredField.getName(), declaredField.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        String xmlContent = toXml(stringObjectMap, depth + (isRootObject ? 1 : 0));
        return isRootObject ? startField(name, depth) + "\n" + xmlContent + "\n" + endField(name) + "\n" : xmlContent;
    }

    private String toXml(Map<String, Object> stringObjectMap, int depth) {
        return stringObjectMap.entrySet().stream()
                .map(entry -> startField(entry.getKey(), depth) + ObjectToString(entry.getValue(), depth + 1) + endField(entry.getKey()))
                .collect(Collectors.joining("\n"));
    }

    private String ObjectToString(Object value, int depth) {
        if (value == null) {
            return "<null/>";
        }
        else if (value instanceof String ||
                value instanceof UUID ||
                value instanceof OffsetDateTime ||
                value instanceof LocalDate) {
            return value.toString();
        }
        else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        }
        else if (isPrimitiveOrWrapper(value.getClass())) {
            return String.valueOf(value);
        }
        else if (value.getClass().isArray()) {
            return arrayToXml(value, depth);
        }
        else if (value instanceof Collection<?>) {
            return collectionsToXml((Collection<?>) value, depth);
        }
        else if (value instanceof Map<?, ?>) {
            return mapToXml((Map<?, ?>) value, depth);
        }
        else {
            return serializeInternal(value, depth, false);
        }
    }

    private String arrayToXml(Object array, int depth) {
        StringBuilder builder = new StringBuilder();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            builder.append("\n");
            builder.append(startField("item", depth)).append(ObjectToString(Array.get(array, i), depth + 1))
                    .append(endField("item"));
        }
        return builder.append("\n").toString();
    }

    private String collectionsToXml(Collection<?> collection, int depth) {
        return "\n" + collection.stream()
                .map(item -> startField("item", depth) + ObjectToString(item, depth + 1) + endField("item"))
                .collect(Collectors.joining("\n")) + "\n";
    }

    private String mapToXml(Map<?, ?> map, int depth) {
        return map.entrySet().stream()
                .map(entry -> startField("entry", depth) + startField("key", depth + 1) + ObjectToString(entry.getKey(), depth + 2) + endField("key") + startField("value", depth + 1) + ObjectToString(entry.getValue(), depth + 2) + endField("value") + endField("entry"))
                .collect(Collectors.joining("\n"));
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type.equals(Integer.class) || type.equals(Double.class) ||
                type.equals(Float.class) || type.equals(Boolean.class) ||
                type.equals(Character.class) || type.equals(Byte.class) ||
                type.equals(Short.class) || type.equals(Long.class);
    }

    private String indent(int depth) {
        return "    ".repeat(depth);
    }

    private String endField(String key) {
        return "</" + key + ">";
    }

    private String startField(String key, int depth) {
        return indent(depth) + "<" + key + ">";
    }
}
