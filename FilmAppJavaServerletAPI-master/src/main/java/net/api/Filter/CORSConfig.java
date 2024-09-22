package net.api.Filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CORSConfig implements Filter {
    private static final Map<String, String> CORS_HEADERS = new HashMap<>();

    static {
        CORS_HEADERS.put("Access-Control-Allow-Origin", "*");
        CORS_HEADERS.put("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        CORS_HEADERS.put("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        CORS_HEADERS.put("Access-Control-Max-Age", "3600");
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization required
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        handleCorsRequest(httpResponse, (HttpServletRequest) request);
        chain.doFilter(request, response);
    }

    private void handleCorsRequest(HttpServletResponse response, HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin != null && !origin.isEmpty() ? origin : "*");
        CORS_HEADERS.forEach(response::setHeader);
    }

    @Override
    public void destroy() {
        // No cleanup required
    }
}