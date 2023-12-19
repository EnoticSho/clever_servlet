package clevertec.servlet;

import clevertec.dto.InfoProductDto;
import clevertec.dto.ProductDto;
import clevertec.exception.ProductNotFoundException;
import clevertec.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

@Slf4j
@WebServlet(name = "product-servlet", value = "/products/*")
public class ProductServlet extends HttpServlet {

    private ProductService productService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ServletContext ctx = getServletContext();
        this.productService = (ProductService) ctx.getAttribute("productService");
        this.objectMapper = (ObjectMapper) ctx.getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            handleListProductsRequest(req, resp);
        }
        else {
            handleSingleProductRequest(req, resp, pathInfo);
        }
    }

    private void handleSingleProductRequest(HttpServletRequest req, HttpServletResponse resp, String pathInfo) throws IOException {
        try {
            UUID productId = UUID.fromString(pathInfo.substring(1));
            InfoProductDto product = productService.get(productId);
            String jsonResponse = objectMapper.writeValueAsString(product);
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(jsonResponse);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (ProductNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
        }
    }

    private void handleListProductsRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pageSizeParam = req.getParameter("pageSize");
            String pageNumberParam = req.getParameter("pageNumber");

            int pageSize = pageSizeParam != null ? Integer.parseInt(pageSizeParam) : 20;
            int pageNumber = pageNumberParam != null ? Integer.parseInt(pageNumberParam) : 1;

            List<InfoProductDto> products = productService.getAllProducts(pageSize, pageNumber);
            String jsonResponse = objectMapper.writeValueAsString(products);

            writeResponse(resp, jsonResponse, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("Error in listing products: ", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in listing products.");
        }
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ProductDto productDto = objectMapper.readValue(req.getReader(), ProductDto.class);
            UUID productId = productService.create(productDto);
            writeResponse(resp, productId, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID is required.");
            return;
        }

        try {
            UUID productId = UUID.fromString(pathInfo.substring(1));
            ProductDto productDto = objectMapper.readValue(req.getReader(), ProductDto.class);
            UUID update = productService.update(productId, productDto);
            writeResponse(resp, update.toString(), HttpServletResponse.SC_OK);
        } catch (ProductNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID is required.");
            return;
        }

        try {
            UUID productId = UUID.fromString(pathInfo.substring(1));
            productService.delete(productId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (ProductNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
        }
    }

    private void writeResponse(HttpServletResponse resp, Object object, int statusCode) throws IOException {
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(objectMapper.writeValueAsString(object));
            resp.setStatus(statusCode);
        }
    }
}
