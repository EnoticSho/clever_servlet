package clevertec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoProductDto {

    private UUID id;
    private String name;
    private Double price;
    private Double weight;
}
