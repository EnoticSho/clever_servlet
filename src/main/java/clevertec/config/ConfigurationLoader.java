package clevertec.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Класс ConfigurationLoader выполняет загрузку конфигурации из файла 'application.yml' и предоставляет ее в виде Map<String, Object>.
 */
public class ConfigurationLoader {

    private static final String CONFIG_FILE = "application.yml";

    /**
     * Метод для загрузки конфигурации из файла 'application.yml'.
     *
     * @return Карта (Map), содержащая конфигурацию в виде пар ключ-значение.
     * @throws IOException           Если произошла ошибка ввода/вывода при чтении файла.
     * @throws FileNotFoundException Если файл 'application.yml' не найден в класспасе.
     */
    public static Map<String, Object> loadConfig() throws IOException {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new FileNotFoundException("Configuration file 'application.yml' not found on classpath");
            }
            Yaml yaml = new Yaml();
            return yaml.load(input);
        }
    }
}
