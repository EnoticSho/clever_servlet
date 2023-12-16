package clevertec.dao.impl;

import clevertec.config.dbConnection.DatabaseConnectionManager;
import clevertec.dao.ProductDao;
import clevertec.entity.Product;
import lombok.RequiredArgsConstructor;
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
 * Реализация DAO (Data Access Object) для работы с продуктами в базе данных.
 */
@Slf4j
@RequiredArgsConstructor
public class ProductDaoImpl implements ProductDao {

    private final DatabaseConnectionManager databaseConnectionManager;

    /**
     * Ищет продукт по его идентификатору.
     *
     * @param uuid Идентификатор продукта
     * @return Опциональный объект продукта, если найден
     */
    @Override
    public Optional<Product> findById(UUID uuid) {
        String query = """
                SELECT *
                FROM products
                WHERE id = ?
                """;
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, uuid);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(buildProduct(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("SQL exception in findById", e);
            throw new RuntimeException("SQL exception occurred while finding product by id", e);
        }
        return Optional.empty();
    }

    /**
     * Получает список всех продуктов.
     *
     * @return Список продуктов
     */
    @Override
    public List<Product> findALL() {
        List<Product> productList = new ArrayList<>();
        String query = """
                SELECT *
                FROM products
                """;
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Optional<Product> product = Optional.of(buildProduct(resultSet));
                    productList.add(product.get());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        String query = """
                INSERT INTO products (id, name, price, weight, creation_date)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, product.getId());
            preparedStatement.setString(2, product.getName());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setDouble(4, product.getWeight());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(product.getCreated()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save product", e);
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
        String query = """
                UPDATE products
                SET name = ?, price = ?, weight = ?, creation_date = ?
                WHERE id = ?;\
                """;

        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setDouble(3, product.getWeight());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(product.getCreated()));
            preparedStatement.setObject(5, product.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product", e);
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
        String query = """
                DELETE
                FROM products
                WHERE id = ?
                """;
        try (Connection connection = databaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setObject(1, uuid);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected < 1) {
                throw new SQLException("Product not found for id: " + uuid);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product with id: " + uuid, e);
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
