package clevertec;

import clevertec.config.dbConnection.DatabaseConnectionManager;
import clevertec.dao.impl.ProductDaoImpl;
import clevertec.dto.InfoProductDto;
import clevertec.mapper.ProductMapperImpl;
import clevertec.proxy.DaoProxyImpl;
import clevertec.service.ProductService;
import clevertec.service.impl.ProductServiceImpl;
import clevertec.utils.pdfserializer.PdfSerializer;

import java.io.IOException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        ProductService service = new ProductServiceImpl(new DaoProxyImpl(new ProductDaoImpl(new DatabaseConnectionManager())), new ProductMapperImpl());
        InfoProductDto infoProductDto = service.get(UUID.fromString("dcce95ba-46ea-4739-887b-1de051755ac7"));
        System.out.println(infoProductDto);
        PdfSerializer pdfSerializer = new PdfSerializer();
        pdfSerializer.serializeObjectToPdf(infoProductDto);
        service.getAllProducts().forEach(System.out::println);
    }
}
