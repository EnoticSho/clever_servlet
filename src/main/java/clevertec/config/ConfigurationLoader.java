package clevertec.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ConfigurationLoader {
    private static final String CONFIG_FILE = "application.yml";

    public static Map<String, Object> loadConfig() throws IOException {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new FileNotFoundException("Файл конфигурации 'application.yml' не найден в classpath");
            }
            Yaml yaml = new Yaml();
            return yaml.load(input);
        }
    }
}
