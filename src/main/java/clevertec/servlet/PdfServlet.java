package clevertec.servlet;

import clevertec.service.PdfService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@WebServlet(name = "pdf-servlet", value = "/pdf/*")
public class PdfServlet extends HttpServlet {

    private PdfService pdfService;

    @Override
    public void init() {
        ServletContext ctx = getServletContext();
        this.pdfService = (PdfService) ctx.getAttribute("pdf");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new ServletException("Product ID is required.");
        }

        UUID productId = UUID.fromString(pathInfo.substring(1));
        Path pdfPath = pdfService.productToPdf(productId);

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + pdfPath.getFileName() + "\"");
        Files.copy(pdfPath, resp.getOutputStream());
    }
}
