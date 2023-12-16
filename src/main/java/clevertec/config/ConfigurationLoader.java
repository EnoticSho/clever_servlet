package clevertec.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ConfigurationLoader {
    private static final String CONFIG_FILE_PATH = "src/main/resources/application.yml";

    public static Map<String, Object> loadConfig() throws IOException {
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            Yaml yaml = new Yaml();
            return yaml.load(input);
        }
    }
}
