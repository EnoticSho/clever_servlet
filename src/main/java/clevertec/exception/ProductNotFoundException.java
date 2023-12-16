package clevertec.exception;

import java.util.UUID;

/**
 * Исключение, выбрасываемое в случае отсутствия продукта с заданным идентификатором UUID.
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Конструктор исключения ProductNotFoundException.
     * Создает исключение с сообщением, указывающим на отсутствие продукта с конкретным UUID.
     *
     * @param uuid Идентификатор продукта, который не удалось найти
     */
    public ProductNotFoundException(UUID uuid) {
        super(String.format("Product with uuid: %s not found", uuid));
    }
}
