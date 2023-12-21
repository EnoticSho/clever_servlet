package clevertec.exception;

/**
 * Исключение, выбрасываемое при возникновении ошибки доступа к базе данных.
 * Это исключение обычно используется, когда не удалось выполнить какую-либо операцию с базой данных
 */
public class DatabaseAccessException extends RuntimeException{

    /**
     * Создает новое исключение с заданным сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
