package clevertec.utils;

import clevertec.data.ProductTestData;
import clevertec.dto.InfoProductDto;
import clevertec.utils.xmlserializer.XmlSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlSerializerTest {

    private XmlSerializer xmlSerializer;

    @BeforeEach
    public void setUp() {
        xmlSerializer = new XmlSerializer();
    }

    @Test
    public void shouldReturnProductInXml() {
        // Given
        InfoProductDto infoProductDto = ProductTestData.builder()
                .build()
                .buildInfoProductDto();
        String expected = "<InfoProductDto>\n" +
                "    <id>c249fc5b-4a25-4212-83ca-2c6ec0d57d0b</id>\n" +
                "    <name>ProductName</name>\n" +
                "    <price>100.0</price>\n" +
                "    <weight>50.0</weight>\n" +
                "</InfoProductDto>";
        String normalizedExpected = expected.replaceAll("\\s+", "");

        // When
        String serialize = xmlSerializer.serialize(infoProductDto);
        String normalizedSerialize = serialize.replaceAll("\\s+", "");

        // Then
        assertEquals(normalizedExpected, normalizedSerialize);
    }
}
