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

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final DaoProxyImpl daoProxy;
    private final ProductMapper productMapper;

    @Override
    public InfoProductDto get(UUID uuid) {
        return daoProxy.getProductById(uuid)
                .map(productMapper::toInfoProductDto)
                .orElseThrow(() -> new ProductNotFoundException(uuid));
    }

    @Override
    public List<InfoProductDto> getAllProducts() {
        return daoProxy.getAllProducts().stream()
                .map(productMapper::toInfoProductDto)
                .toList();
    }

    @Override
    public UUID update(UUID uuid, @Valid ProductDto productDto) {
        Product product = daoProxy.getProductById(uuid)
                .orElseThrow(() -> new ProductNotFoundException(uuid));
        Product merge = productMapper.merge(product, productDto);
        return daoProxy.update(merge).getId();
    }

    @Override
    public UUID create(@Valid ProductDto productDto) {
        Product product = productMapper.toProduct(productDto);
        product.setId(UUID.randomUUID());
        product.setCreated(LocalDateTime.now());
        return daoProxy.saveProduct(product).getId();
    }

    @Override
    public void delete(UUID uuid) {
        daoProxy.deleteProductById(uuid);
    }
}
