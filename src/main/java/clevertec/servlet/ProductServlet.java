package clevertec.servlet;

import clevertec.dao.impl.ProductDaoImpl;
import clevertec.dto.InfoProductDto;
import clevertec.dto.ProductDto;
import clevertec.exception.ProductNotFoundException;
import clevertec.mapper.ProductMapperImpl;
import clevertec.proxy.DaoProxyImpl;
import clevertec.service.impl.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
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

    private final ProductServiceImpl productService;
    private final ObjectMapper objectMapper;

    public ProductServlet() throws ClassNotFoundException {
        this.productService = new ProductServiceImpl(new DaoProxyImpl(new ProductDaoImpl()), new ProductMapperImpl());
        this.objectMapper = new ObjectMapper();
        Class.forName("org.postgresql.Driver");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            String pageSizeParam = req.getParameter("pageSize");
            String pageNumberParam = req.getParameter("pageNumber");
            int pageSize = pageSizeParam != null ? Integer.parseInt(pageSizeParam) : 20;
            int pageNumber = pageNumberParam != null ? Integer.parseInt(pageNumberParam) : 1;
            List<InfoProductDto> products = productService.getAllProducts(pageSize, pageNumber);
            String jsonResponse = objectMapper.writeValueAsString(products);
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(jsonResponse);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        }
        else {
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
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ProductDto productDto = objectMapper.readValue(req.getReader(), ProductDto.class);
            UUID productId = productService.create(productDto);
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(objectMapper.writeValueAsString(productId));
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID is required.");
            return;
        }

        try {
            UUID productId = UUID.fromString(pathInfo.substring(1));
            ProductDto productDto = objectMapper.readValue(req.getReader(), ProductDto.class);
            UUID update = productService.update(productId, productDto);
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(update.toString());
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (ProductNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
}
