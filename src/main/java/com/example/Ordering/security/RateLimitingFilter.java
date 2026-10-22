package com.example.Ordering.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_GENERAL_REQUESTS_PER_MINUTE = 150;
    private static final int MAX_AUTH_REQUESTS_PER_MINUTE = 25;

    private static class RequestCounter {
        long timestamp;
        AtomicInteger count;

        RequestCounter(long timestamp) {
            this.timestamp = timestamp;
            this.count = new AtomicInteger(1);
        }
    }

    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Skip rate limiting for static assets and swagger
        if (path.startsWith("/static/") || path.startsWith("/css/") || path.startsWith("/js/")
                || path.startsWith("/images/") || path.endsWith(".html") || path.endsWith(".ico")
                || path.equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        boolean isAuth = path.startsWith("/api/auth/");
        String key = clientIp + (isAuth ? "_auth" : "_gen");
        int maxAllowed = isAuth ? MAX_AUTH_REQUESTS_PER_MINUTE : MAX_GENERAL_REQUESTS_PER_MINUTE;

        long now = System.currentTimeMillis();
        RequestCounter counter = requestCounts.compute(key, (k, existing) -> {
            if (existing == null || now - existing.timestamp > 60_000) {
                return new RequestCounter(now);
            }
            existing.count.incrementAndGet();
            return existing;
        });

        if (counter.count.get() > maxAllowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", "60");
            response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Please wait a minute before making more requests.\"}");
            return;
        }

        // Periodic cleanup if map grows
        if (requestCounts.size() > 5000) {
            requestCounts.entrySet().removeIf(entry -> now - entry.getValue().timestamp > 120_000);
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
