package clevertec.liquibase;

import clevertec.config.ConfigurationLoader;
import clevertec.dbConnection.DatabaseConnectionManager;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@WebListener
public class LiquibaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Map<String, Object> config = ConfigurationLoader.loadConfig();
            Map<String, Object> dbProperties = (Map<String, Object>) config.get("db");
            Boolean url = (Boolean) dbProperties.get("initialize-db");
            if (url) {
                Class.forName("org.postgresql.Driver");
                Connection connection = DatabaseConnectionManager.getConnection();
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));
                Liquibase liquibase = new Liquibase("liquibase/db-changelog.sql", new ClassLoaderResourceAccessor(), database);
                liquibase.update();
            }
        } catch (IOException | ClassNotFoundException | LiquibaseException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
