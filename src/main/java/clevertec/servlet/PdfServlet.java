package clevertec.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebServlet;

@Slf4j
@WebServlet(name = "pdf-servlet", value = "/pdf")
public class PdfServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String productID = request.getParameter("productID");
//
//        if (productExists(productID)) {
//            try {
//                // Создаем объект класса, содержащего информацию о продукте
//                Product product = getProductDetails(productID);
//
//                // Используем PdfSerializer для генерации PDF
//                PdfSerializer serializer = new PdfSerializer();
//                serializer.serializeObjectToPdf(product);
//
//                // Здесь можно добавить код для отправки файла PDF клиенту
//                // ...
//
//            } catch (Exception e) {
//                log.error("Ошибка при генерации PDF: ", e);
//                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при генерации PDF");
//            }
//        } else {
//            log.info("Товар с ID {} не найден", productID);
//            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Товар не найден");
//        }
//    }
}
