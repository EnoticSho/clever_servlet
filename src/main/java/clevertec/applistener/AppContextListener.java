package clevertec.applistener;

import clevertec.config.ConfigUtils;
import clevertec.config.ConfigurationLoader;
import clevertec.dao.ProductDao;
import clevertec.dao.impl.ProductDaoImpl;
import clevertec.dbConnection.DatabaseConnectionManager;
import clevertec.fliter.EncodingFilter;
import clevertec.fliter.ErrorHandlingFilter;
import clevertec.mapper.ProductMapperImpl;
import clevertec.proxy.DaoProxyImpl;
import clevertec.service.PdfService;
import clevertec.service.ProductService;
import clevertec.service.impl.PdfServiceImpl;
import clevertec.service.impl.ProductServiceImpl;
import clevertec.utils.pdfserializer.PdfSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.command.CommandScope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Слушатель контекста приложения, используемый для инициализации и конфигурации компонентов веб-приложения.
 * Этот класс реализует интерфейс {@link
 * ServletContextListener} и предназначен для выполнения действий при запуске веб-приложения.
 */
@Slf4j
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final String DB_CONFIG_KEY = "db";
    private static final String INITIALIZE_DB_KEY = "initialize-db";
    private static final String SQL_PATH_KEY = "sql-path";

    /**
     * Вызывается при инициализации контекста сервлета. Этот метод отвечает за настройку и инициализацию компонентов веб-приложения.
     *
     * @param sce Событие контекста сервлета, предоставляющее доступ к объекту {@link ServletContext}.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        FilterRegistration.Dynamic encodingFilter = ctx.addFilter("EncodingFilter", new EncodingFilter());
        encodingFilter.addMappingForUrlPatterns(null, false, "/*");

        FilterRegistration.Dynamic errorHandlingFilter = ctx.addFilter("ErrorHandlingFilter", new ErrorHandlingFilter());
        errorHandlingFilter.addMappingForUrlPatterns(null, false, "/*");

        initServices(ctx);

        liquibaseInit();
    }

    /**
     * Инициализирует сервисы и компоненты приложения, такие как DAO, сервисы и мапперы.
     * Добавляет созданные компоненты в атрибуты контекста сервлета для дальнейшего использования.
     *
     * @param ctx Контекст сервлета, в который добавляются компоненты.
     */
    private void initServices(ServletContext ctx) {
        ProductDao productDao = new ProductDaoImpl();
        ProductService productService = new ProductServiceImpl(new DaoProxyImpl(productDao), new ProductMapperImpl());
        ObjectMapper objectMapper = new ObjectMapper();
        PdfService pdfService = new PdfServiceImpl(new PdfSerializer(), productService);

        ctx.setAttribute("productService", productService);
        ctx.setAttribute("objectMapper", objectMapper);
        ctx.setAttribute("pdf", pdfService);
    }

    /**
     * Инициализирует Liquibase для управления изменениями в базе данных.
     * Загружает конфигурацию из файла свойств и выполняет начальную настройку и обновление базы данных.
     */
    private void liquibaseInit() {
        try {
            Map<String, Object> config = ConfigurationLoader.loadConfig();
            Map<String, Object> dbProperties = ConfigUtils.safelyCastToMap(config.get(DB_CONFIG_KEY));
            Boolean initializeDb = (Boolean) dbProperties.get(INITIALIZE_DB_KEY);
            String sqlPath = (String) dbProperties.get(SQL_PATH_KEY);

            if (initializeDb) {
                runLiquibase(sqlPath);
            }
        } catch (IOException | LiquibaseException | SQLException e) {
            log.error("Error during initialization Liquibase", e);

        }
    }

    /**
     * Выполняет обновление схемы базы данных с использованием Liquibase, основываясь на заданном пути к файлу изменений.
     *
     * @param sqlPath Путь к файлу изменений Liquibase.
     * @throws LiquibaseException Если произошла ошибка при работе с Liquibase.
     * @throws SQLException       Если произошла ошибка SQL.
     */
    private void runLiquibase(String sqlPath) throws LiquibaseException, SQLException {
        Connection connection = DatabaseConnectionManager.getConnection();
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        CommandScope liquibaseCommandScope = new CommandScope("update")
                .addArgumentValue("changeLogFile", sqlPath)
                .addArgumentValue("resourceAccessor", new ClassLoaderResourceAccessor())
                .addArgumentValue("database", database);

        liquibaseCommandScope.execute();
    }
}
