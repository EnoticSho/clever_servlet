package clevertec.service.impl;

import clevertec.dto.InfoProductDto;
import clevertec.dto.ProductDto;
import clevertec.entity.Product;
import clevertec.exception.ProductNotFoundException;
import clevertec.mapper.ProductMapper;
import clevertec.proxy.DaoProxyImpl;
import clevertec.service.ProductService;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса для работы с продуктами.
 * Предоставляет методы для получения, создания, обновления и удаления продуктов.
 */
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final DaoProxyImpl daoProxy;
    private final ProductMapper productMapper;

    /**
     * Получить информацию о продукте по UUID.
     *
     * @param uuid Уникальный идентификатор продукта.
     * @return DTO информации о продукте.
     * @throws ProductNotFoundException если продукт не найден.
     */
    @Override
    public InfoProductDto get(UUID uuid) {
        return daoProxy.getProductById(uuid)
                .map(productMapper::toInfoProductDto)
                .orElseThrow(() -> new ProductNotFoundException(uuid));
    }

    /**
     * Получить список всех продуктов с пагинацией.
     *
     * @param pageSize    Размер страницы.
     * @param pageNumber  Номер страницы.
     * @return Список DTO информации о продуктах.
     */
    @Override
    public List<InfoProductDto> getAllProducts(int pageSize, int pageNumber) {
        return daoProxy.getAllProducts(pageSize, pageNumber).stream()
                .map(productMapper::toInfoProductDto)
                .toList();
    }

    /**
     * Обновить информацию о продукте.
     *
     * @param uuid       Уникальный идентификатор продукта.
     * @param productDto DTO продукта для обновления.
     * @return UUID обновленного продукта.
     * @throws ProductNotFoundException если продукт не найден.
     */
    @Override
    public UUID update(UUID uuid, @Valid ProductDto productDto) {
        Product product = daoProxy.getProductById(uuid)
                .orElseThrow(() -> new ProductNotFoundException(uuid));
        Product merge = productMapper.merge(product, productDto);
        return daoProxy.update(merge).getId();
    }

    /**
     * Создать новый продукт.
     *
     * @param productDto DTO продукта для создания.
     * @return UUID созданного продукта.
     */
    @Override
    public UUID create(@Valid ProductDto productDto) {
        Product product = productMapper.toProduct(productDto);
        product.setId(UUID.randomUUID());
        product.setCreated(LocalDateTime.now());
        return daoProxy.save(product).getId();
    }

    /**
     * Удалить продукт по UUID.
     *
     * @param uuid Уникальный идентификатор продукта для удаления.
     */
    @Override
    public void delete(UUID uuid) {
        daoProxy.deleteProductById(uuid);
    }
}
