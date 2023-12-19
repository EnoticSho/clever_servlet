package clevertec.data;

import clevertec.dto.InfoProductDto;
import clevertec.dto.ProductDto;
import clevertec.entity.Product;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
@Builder(setterPrefix = "with")
public class ProductTestData {

    @Builder.Default
    private UUID id = UUID.fromString("c249fc5b-4a25-4212-83ca-2c6ec0d57d0b");

    @Builder.Default
    private String name = "ProductName";

    @Builder.Default
    private Double price = 100.00;

    @Builder.Default
    private Double weight = 50.00;

    @Builder.Default
    private LocalDateTime created = LocalDateTime.of(2023, 10, 15, 12, 34);

    public Product buildProduct() {
        return new Product(id, name, price, weight, created);
    }

    public ProductDto buildProductDto() {
        return new ProductDto(name, price, weight);
    }

    public InfoProductDto buildInfoProductDto() {
        return new InfoProductDto(id, name, price, weight);
    }
}
