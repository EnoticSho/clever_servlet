package clevertec.service.impl;

import clevertec.data.ProductTestData;
import clevertec.dto.InfoProductDto;
import clevertec.exception.ProductNotFoundException;
import clevertec.service.ProductService;
import clevertec.utils.pdfserializer.PdfSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {
    @InjectMocks
    private PdfServiceImpl pdfService;

    @Mock
    private PdfSerializer pdfSerializer;

    @Mock
    private ProductService productService;


    @Test
    public void productToPdf_Success() {
        // Given
        InfoProductDto infoProductDto = ProductTestData.builder()
                .build()
                .buildInfoProductDto();
        UUID id = infoProductDto.getId();
        Path expectedPath = Paths.get("test.pdf");

        when(productService.get(id))
                .thenReturn(infoProductDto);
        when(pdfSerializer.serializeObjectToPdf(infoProductDto))
                .thenReturn(expectedPath);

        // When
        Path actualPath = pdfService.productToPdf(id);

        // Then
        verify(productService)
                .get(id);
        verify(pdfSerializer)
                .serializeObjectToPdf(infoProductDto);
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void productToPdf_ProductServiceThrowsException() {
        // Given
        UUID testUuid = UUID.fromString("ea211d6b-5648-4c6d-8d37-8b16303afdf5");
        when(productService.get(testUuid))
                .thenThrow(new ProductNotFoundException(testUuid));

        // When & Then
        Assertions.assertThrows(ProductNotFoundException.class,
                () -> pdfService.productToPdf(testUuid));
    }
}
