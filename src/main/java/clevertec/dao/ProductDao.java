package clevertec.dao;

import clevertec.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductDao {
    Optional<Product> findById(UUID uuid);

    List<Product> findAll(int pageSize, int pageNumber);

    Product save(Product product);

    Product update(Product product);

    void delete(UUID uuid);
}
