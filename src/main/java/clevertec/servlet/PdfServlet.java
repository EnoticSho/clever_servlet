package clevertec.servlet;

import clevertec.dto.InfoProductDto;
import clevertec.exception.ProductNotFoundException;
import clevertec.service.PdfService;
import clevertec.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@WebServlet(name = "pdf-servlet", value = "/pdf/*")
public class PdfServlet extends HttpServlet {

    private PdfService pdfService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        ServletContext ctx = getServletContext();
        this.pdfService = (PdfService) ctx.getAttribute("pdf");
        this.objectMapper = (ObjectMapper) ctx.getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID is required.");
            return;
        }

        try {
            UUID productId = UUID.fromString(pathInfo.substring(1));
            Path pdfPath = pdfService.productToPdf(productId);

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + pdfPath.getFileName() + "\"");
            Files.copy(pdfPath, resp.getOutputStream());
        } catch (ProductNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
        }
    }
}
