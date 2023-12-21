package clevertec.service;

import clevertec.dto.InfoProductDto;
import clevertec.dto.ProductDto;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    InfoProductDto get(UUID uuid);

    List<InfoProductDto> getAllProducts(int pageSize, int pageNumber);

    UUID update(UUID uuid, ProductDto productDto);

    UUID create(ProductDto productDto);

    void delete(UUID uuid);
}
