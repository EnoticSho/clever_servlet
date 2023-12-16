package clevertec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    @NotEmpty(message = "Имя не может быть пустым")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Неверный формат имени")
    private String name;

    @NotNull(message = "Цена не может быть null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private Double price;

    @NotNull(message = "Цена не может быть null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private Double weight;
}
