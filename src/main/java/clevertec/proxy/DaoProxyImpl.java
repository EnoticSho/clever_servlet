package clevertec.proxy;

import clevertec.cache.Cache;
import clevertec.cache.impl.LfuCache;
import clevertec.cache.impl.LruCache;
import clevertec.config.ConfigUtils;
import clevertec.config.ConfigurationLoader;
import clevertec.dao.ProductDao;
import clevertec.entity.Product;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Прокси-класс для доступа к данным продуктов, инкапсулирующий логику кэширования.
 */
@Slf4j
public class DaoProxyImpl {
    private static final String CACHE_KEY = "cache";
    private static final String CACHE_CAPACITY = "capacity";
    private static final String CACHE_TYPE = "type";
    private final ProductDao productDao;
    private final Cache<UUID, Product> cache;

    /**
     * Конструктор DaoProxy.
     *
     * @param productDao DAO для работы с продуктами
     */
    public DaoProxyImpl(ProductDao productDao) {
        this.productDao = productDao;
        this.cache = cacheInit();
    }

    /**
     * Конструктор DaoProxy.
     *
     * @param productDao DAO для работы с продуктами
     * @param cache      кэш для работы с продуктами
     */
    public DaoProxyImpl(ProductDao productDao, Cache<UUID, Product> cache) {
        this.productDao = productDao;
        this.cache = cache;
    }

    /**
     * Инициализирует кэш на основе конфигурации.
     *
     * @return Инстанс кэша
     */
    private Cache<UUID, Product> cacheInit() {
        try {
            Map<String, Object> objectMap = ConfigurationLoader.loadConfig();
            Map<String, Object> cacheConfig = ConfigUtils.safelyCastToMap(objectMap.get(CACHE_KEY));
            int capacity = (Integer) cacheConfig.get(CACHE_CAPACITY);
            String cacheType = (String) cacheConfig.get(CACHE_TYPE);
            return createCache(cacheType, capacity);
        } catch (IOException e) {
            log.error("Error initializing cache", e);
            throw new RuntimeException("Failed to initialize cache", e);
        }
    }

    private Cache<UUID, Product> createCache(String cacheType, int capacity) {
        return switch (cacheType) {
            case "lru" -> new LruCache<>(capacity);
            case "lfu" -> new LfuCache<>(capacity);
            default -> throw new IllegalArgumentException("Unsupported cache type: " + cacheType);
        };
    }

    /**
     * Получает продукт по его идентификатору. Сначала проверяет наличие продукта в кэше.
     * Если продукт не найден в кэше, загружает его из DAO и помещает в кэш.
     * Возвращает Optional<Product>.
     *
     * @param id Идентификатор продукта
     * @return Optional<Product>, содержащий продукт, если он найден, иначе пустой Optional
     */
    public Optional<Product> getProductById(UUID id) {
        Optional<Product> product = cache.get(id);
        if (product.isEmpty()) {
            product = productDao.findById(id);
            product.ifPresent(p -> cache.put(id, p));
        }
        return product;
    }

    /**
     * Получает список всех продуктов.
     *
     * @return Список продуктов
     */
    public List<Product> getAllProducts(int pageSize, int pageNumber) {
        return productDao.findAll(pageSize, pageNumber);
    }

    /**
     * Сохраняет продукт, используя DAO, и добавляет его в кэш.
     *
     * @param product Продукт для сохранения
     * @return Сохраненный продукт
     */
    public Product save(Product product) {
        Product save = productDao.save(product);
        cache.put(product.getId(), save);
        return save;
    }

    /**
     * Обновляет продукт с помощью DAO и обновляет его в кэше.
     *
     * @param product Продукт для обновления
     * @return Обновленный продукт
     */
    public Product update(Product product) {
        Product update = productDao.update(product);
        cache.put(product.getId(), update);
        return update;
    }

    /**
     * Удаляет продукт по его идентификатору с помощью DAO и удаляет его из кэша.
     *
     * @param id Идентификатор продукта для удаления
     */
    public void deleteProductById(UUID id) {
        productDao.delete(id);
        cache.delete(id);
    }
}
