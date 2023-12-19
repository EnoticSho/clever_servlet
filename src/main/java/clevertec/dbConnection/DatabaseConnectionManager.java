package clevertec.dbConnection;

import clevertec.config.ConfigUtils;
import clevertec.config.ConfigurationLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Менеджер соединений с базой данных, использующий пул соединений HikariCP.
 */
@Slf4j
public class DatabaseConnectionManager {
    private static final HikariDataSource DATA_SOURCE;
    private static final String DB_CONFIG_KEY = "db";
    private static final String DB_PASSWORD_KEY = "dbPassword";
    private static final String DB_USERNAME_KEY = "dbUsername";
    private static final String DB_URL_KEY = "dbUrl";
    private static final String DB_DRIVER = "org.postgresql.Driver";

    static {
        try {
            Map<String, Object> config = ConfigurationLoader.loadConfig();
            if (config == null || !config.containsKey("db")) {
                throw new IllegalStateException("Database configuration is missing.");
            }

            Map<String, Object> dbProperties = ConfigUtils.safelyCastToMap(config.get(DB_CONFIG_KEY));
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl((String) dbProperties.get(DB_URL_KEY));
            hikariConfig.setUsername((String) dbProperties.get(DB_USERNAME_KEY));
            hikariConfig.setPassword((String) dbProperties.get(DB_PASSWORD_KEY));
            hikariConfig.setDriverClassName(DB_DRIVER);

            DATA_SOURCE = new HikariDataSource(hikariConfig);
        } catch (Exception e) {
            log.error("Failed to initialize the database connection pool.", e);
            throw new RuntimeException("Failed to initialize the database connection pool.", e);
        }
    }

    /**
     * Получает соединение из пула соединений HikariCP.
     *
     * @return Активное соединение с базой данных
     * @throws SQLException если происходит ошибка SQL или соединение невозможно установить
     */
    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}
