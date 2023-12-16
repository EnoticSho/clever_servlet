package clevertec.config.dbConnection;

import clevertec.config.ConfigurationLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * Менеджер соединений с базой данных, обеспечивающий создание и поддержку соединений.
 */
public class DatabaseConnectionManager {
    private Connection connection;

    /**
     * Получает соединение с базой данных. Если текущее соединение отсутствует или закрыто,
     * метод создает новое соединение.
     *
     * @return Активное соединение с базой данных
     * @throws SQLException если происходит ошибка SQL или соединение невозможно установить
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Map<String, Object> config = ConfigurationLoader.loadConfig();
                Map<String, Object> dbProperties = (Map<String, Object>) config.get("db");
                String url = (String) dbProperties.get("dbUrl");
                String username = (String) dbProperties.get("dbUsername");
                String password = (String) dbProperties.get("dbPassword");
                connection = DriverManager.getConnection(url, username, password);
            } catch (IOException e) {
                e.printStackTrace();
                throw new SQLException("Unable to read application.yml file.");
            }
        }
        return connection;
    }
}
