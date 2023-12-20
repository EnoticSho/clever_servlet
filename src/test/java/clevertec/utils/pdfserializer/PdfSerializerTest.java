package clevertec.utils.pdfserializer;

import clevertec.data.ProductTestData;
import clevertec.dto.InfoProductDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfSerializerTest {

    private PdfSerializer pdfSerializer;
    private String testFilePath;

    @BeforeEach
    void setUp() {
        pdfSerializer = new PdfSerializer();
        String simpleName = "InfoProductDto";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        String fileName = simpleName + "_" + formattedDateTime + ".pdf";
        testFilePath = "pdf" + File.separator + fileName;
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(new File(testFilePath).toPath());
    }

    @Test
    void testSerializeObjectToPdf_CreatesFile() {
        // Given
        InfoProductDto infoProductDto = ProductTestData.builder()
                .build()
                .buildInfoProductDto();
        Path expected = Path.of(testFilePath);

        // When
        Path actual = pdfSerializer.serializeObjectToPdf(infoProductDto);

        // Then
        assertEquals(expected, actual);
    }
}
