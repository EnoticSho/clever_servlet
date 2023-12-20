package clevertec.dao.impl;

import clevertec.dao.ProductDao;
import clevertec.dbConnection.DatabaseConnectionManager;
import clevertec.entity.Product;
import clevertec.exception.DatabaseAccessException;
import clevertec.exception.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация интерфейса {@link ProductDao} для взаимодействия с продуктами в базе данных.
 * Предоставляет методы для поиска, сохранения, обновления и удаления продуктов.
 */
@Slf4j
public class ProductDaoImpl implements ProductDao {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM products WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM products LIMIT ? OFFSET ?";
    private static final String SAVE_QUERY = "INSERT INTO products (id, name, price, weight, creation_date) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE products SET name = ?, price = ?, weight = ?, creation_date = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM products WHERE id = ?";

    /**
     * Ищет продукт в базе данных по его уникальному идентификатору.
     *
     * @param uuid Уникальный идентификатор продукта.
     * @return Опциональный объект продукта. Если продукт не найден, возвращает пустой Optional.
     */
    @Override
    public Optional<Product> findById(UUID uuid) {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_QUERY)) {
            preparedStatement.setObject(1, uuid);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(buildProduct(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("SQL exception in findById", e);
            throw new DatabaseAccessException("SQL exception occurred while finding product by id", e);
        }
        return Optional.empty();
    }

    /**
     * Получает страницу списка продуктов из базы данных.
     *
     * @param pageSize Размер страницы (количество продуктов на странице).
     * @param pageNumber Номер страницы (начиная с 1).
     * @return Список продуктов, соответствующий указанной странице.
     */
    @Override
    public List<Product> findAll(int pageSize, int pageNumber) {
        List<Product> productList = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_QUERY)) {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, (pageNumber - 1) * pageSize);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    productList.add(buildProduct(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error retrieving products", e);
        }
        return productList;
    }

    /**
     * Сохраняет продукт в базе данных.
     *
     * @param product Продукт для сохранения
     * @return Сохраненный продукт
     */
    @Override
    public Product save(Product product) {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_QUERY)) {
            preparedStatement.setObject(1, product.getId());
            preparedStatement.setString(2, product.getName());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setDouble(4, product.getWeight());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(product.getCreated()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to save product", e);
        }
        return product;
    }

    /**
     * Обновляет существующий продукт в базе данных.
     *
     * @param product Продукт для обновления
     * @return Обновленный продукт
     */
    @Override
    public Product update(Product product) {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setDouble(3, product.getWeight());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(product.getCreated()));
            preparedStatement.setObject(5, product.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseAccessException("Failed to update product", e);
        }
        return product;
    }

    /**
     * Удаляет продукт из базы данных по его идентификатору.
     *
     * @param uuid Идентификатор продукта для удаления
     */
    @Override
    public void delete(UUID uuid) {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)) {
            preparedStatement.setObject(1, uuid);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected < 1) {
                throw new ProductNotFoundException(uuid);
            }
        } catch (SQLException e) {
            throw new DatabaseAccessException("Error deleting product with id: " + uuid, e);
        }
    }

    private Product buildProduct(ResultSet resultSet) throws SQLException {
        return Product.builder()
                .id((UUID) resultSet.getObject("id"))
                .name(resultSet.getString("name"))
                .price(resultSet.getDouble("price"))
                .weight(resultSet.getDouble("weight"))
                .created(resultSet.getTimestamp("creation_date").toLocalDateTime())
                .build();
    }
}
