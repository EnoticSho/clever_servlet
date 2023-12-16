package clevertec.mapper;

import clevertec.dto.InfoProductDto;
import clevertec.dto.ProductDto;
import clevertec.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Product toProduct(ProductDto productDto);

    InfoProductDto toInfoProductDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Product merge(@MappingTarget Product product, ProductDto productDto);
}
