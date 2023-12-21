package clevertec.service.impl;

import clevertec.dto.InfoProductDto;
import clevertec.service.PdfService;
import clevertec.service.ProductService;
import clevertec.utils.pdfserializer.PdfSerializer;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.UUID;

@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final PdfSerializer pdfSerializer;
    private final ProductService productService;

    @Override
    public Path productToPdf(UUID uuid) {
        InfoProductDto infoProductDto = productService.get(uuid);
        return pdfSerializer.serializeObjectToPdf(infoProductDto);
    }
}
