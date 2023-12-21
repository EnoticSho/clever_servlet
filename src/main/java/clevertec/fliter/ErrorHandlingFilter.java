package clevertec.fliter;

import clevertec.exception.ProductNotFoundException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorHandlingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException {
        try {
            chain.doFilter(request, response);
        } catch (ProductNotFoundException e) {
            handleException((HttpServletResponse) response, HttpServletResponse.SC_NOT_FOUND, "Product not found: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            handleException((HttpServletResponse) response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request: " + e.getMessage());
        } catch (ServletException e) {
            handleException((HttpServletResponse) response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            handleException((HttpServletResponse) response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private void handleException(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}
