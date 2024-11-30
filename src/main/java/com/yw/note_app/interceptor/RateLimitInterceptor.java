package com.yw.note_app.interceptor;

import com.yw.note_app.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    public RateLimitInterceptor(RateLimitService rateLimitService, ObjectMapper objectMapper) {
        this.rateLimitService = rateLimitService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String apiKey = getApiKey(request);
        if (apiKey == null) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing API key");
            return false;
        }

        Bucket bucket = rateLimitService.resolveBucket(apiKey);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        response.addHeader("X-Rate-Limit-Limit", "50"); // 50 per minute
        response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
        response.addHeader("X-Rate-Limit-Reset", String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            return true;
        }else {
            long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            writeErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded. Please try again in " + waitSeconds + " seconds");
            return false;
        }
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws Exception {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private String getApiKey(HttpServletRequest request) {
        // First try to get from header
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null) {
            return apiKey;
        }

        // If no API key, use IP address
        return request.getRemoteAddr();
    }
}
