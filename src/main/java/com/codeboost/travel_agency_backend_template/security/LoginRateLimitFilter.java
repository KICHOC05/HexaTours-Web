package com.codeboost.travel_agency_backend_template.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String RATE_LIMITED_PARAM = "rate-limited";

    private final int maxAttempts;
    private final long windowMillis;
    private final long blockMillis;

    private final Map<String, AttemptRecord> attemptsByIp = new ConcurrentHashMap<>();

    private static class AttemptRecord {
        int failures;
        long windowStart;
        long blockedUntil;
    }

    public LoginRateLimitFilter(
            int maxAttempts,
            long windowSeconds,
            long blockSeconds) {
        this.maxAttempts = Math.max(1, maxAttempts);
        this.windowMillis = windowSeconds * 1000L;
        this.blockMillis = blockSeconds * 1000L;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod())
                || !request.getServletPath().equals("/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = clientIp(request);
        long now = System.currentTimeMillis();
        AttemptRecord record = attemptsByIp.computeIfAbsent(ip, k -> new AttemptRecord());

        synchronized (record) {
            if (record.blockedUntil > now) {
                response.sendRedirect("/login?" + RATE_LIMITED_PARAM);
                return;
            }
        }

        filterChain.doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            attemptsByIp.remove(ip);
            return;
        }

        synchronized (record) {
            if (now - record.windowStart > windowMillis) {
                record.windowStart = now;
                record.failures = 0;
            }

            record.failures++;

            if (record.failures >= maxAttempts) {
                record.blockedUntil = now + blockMillis;
            }
        }
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
