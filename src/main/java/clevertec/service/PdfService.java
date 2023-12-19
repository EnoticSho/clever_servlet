package clevertec.service;

import java.nio.file.Path;
import java.util.UUID;

public interface PdfService {
    Path productToPdf(UUID uuid);
}
