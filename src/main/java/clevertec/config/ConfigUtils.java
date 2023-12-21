package clevertec.config;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Класс ConfigUtils предоставляет утилитарные методы для работы с конфигурацией.
 */
@Slf4j
public class ConfigUtils {
    private ConfigUtils() {
    }

    /**
     * Безопасно приводит объект к типу Map<String, Object>.
     *
     * @param obj Объект для приведения типа.
     * @return Приведенный к типу Map<String, Object> объект.
     * @throws IllegalArgumentException если объект не является Map<String, Object>.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> safelyCastToMap(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        else {
            log.error("Invalid configuration format: expected Map, received {}", obj.getClass().getSimpleName());
            throw new IllegalArgumentException("Invalid configuration format");
        }
    }
}
