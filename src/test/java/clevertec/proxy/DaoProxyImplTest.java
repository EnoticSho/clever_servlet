package clevertec.proxy;

import clevertec.cache.Cache;
import clevertec.dao.ProductDao;
import clevertec.data.ProductTestData;
import clevertec.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DaoProxyImplTest {
    @Mock
    private ProductDao productDao;
    @Mock
    private Cache<UUID, Product> cache;
    @InjectMocks
    private DaoProxyImpl daoProxy;

    @Test
    public void getProductById_ProductNotInCacheButInDatabase() {
        // Given
        Product expectedProduct = ProductTestData.builder()
                .build()
                .buildProduct();
        UUID id = expectedProduct.getId();

        when(cache.get(id))
                .thenReturn(Optional.empty());
        when(productDao.findById(id))
                .thenReturn(Optional.of(expectedProduct));

        // when
        Optional<Product> actualProduct = daoProxy.getProductById(id);

        // then
        assertAll(() -> assertTrue(actualProduct.isPresent()),
                () -> assertEquals(expectedProduct, actualProduct.get()));
        verify(cache)
                .put(id, expectedProduct);
    }

    @Test
    public void getProductById_ProductInCache() {
        // Given
        Product expectedProduct = ProductTestData.builder()
                .build()
                .buildProduct();
        UUID id = expectedProduct.getId();

        when(cache.get(id))
                .thenReturn(Optional.of(expectedProduct));

        // when
        Optional<Product> actualProduct = daoProxy.getProductById(id);

        // then
        assertAll(() -> assertTrue(actualProduct.isPresent()),
                () -> assertEquals(expectedProduct, actualProduct.get()));
        verify(productDao, never())
                .findById(id);
    }

    @Test
    public void ShouldReturnListOfProductsWhenProductsAreAvailable() {
        // Given
        List<Product> expectedProducts = Arrays.asList(ProductTestData.builder()
                        .build()
                        .buildProduct(),
                ProductTestData.builder()
                        .withId(UUID.fromString("c244567b-4a25-4212-83ca-2c6ec0d57d0b"))
                        .build()
                        .buildProduct()
        );
        when(productDao.findAll(10, 1))
                .thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = daoProxy.getAllProducts(10, 1);

        // Then
        assertEquals(expectedProducts, actualProducts);
    }

    @Test
    void ShouldReturnEmptyListWhenNoProductsAreAvailable() {
        // Given
        when(productDao.findAll(10, 1))
                .thenReturn(Collections.emptyList());

        // When
        List<Product> actualProducts = daoProxy.getAllProducts(10, 1);

        // Then
        assertTrue(actualProducts.isEmpty());
    }

    @Test
    void ShouldSaveAndCacheProductWhenProductIsNew() {
        // Given
        Product product = ProductTestData.builder()
                .build()
                .buildProduct();
        when(productDao.save(product))
                .thenReturn(product);

        // When
        Product savedProduct = daoProxy.save(product);

        // Then
        verify(cache)
                .put(product.getId(), savedProduct);
        verify(productDao)
                .save(product);
        assertEquals(product, savedProduct);
    }

    @Test
    void ShouldUpdateAndCacheProductWhenProductExists() {
        // Given
        Product product = ProductTestData.builder()
                .build()
                .buildProduct();
        when(productDao.update(product))
                .thenReturn(product);

        // When
        Product updatedProduct = daoProxy.update(product);

        // Then
        verify(cache)
                .put(product.getId(), updatedProduct);
        verify(productDao)
                .update(product);
        assertEquals(product, updatedProduct);
    }

    @Test
    void ShouldRemoveProductFromDaoAndCacheWhenProductExists() {
        // Given
        UUID productId = UUID.fromString("c244567b-4a25-4212-83ca-2c6ec0d57d0b");

        // When
        daoProxy.deleteProductById(productId);

        // Then
        verify(productDao)
                .delete(productId);
        verify(cache)
                .delete(productId);
    }
}
