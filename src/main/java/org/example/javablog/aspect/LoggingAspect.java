package org.example.javablog.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.javablog.dto.ApiResponse;
import org.example.javablog.model.UserLog;
import org.example.javablog.repository.UserLogRepository;
import org.example.javablog.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String httpMethod = request.getMethod();
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/auth/login") || uri.startsWith("/api/auth/register")) {
            return joinPoint.proceed(); // bỏ qua logging
        }

        String queryParams = request.getQueryString();
        Long userId = extractUserIdFromToken();

        UserLog logEntry = new UserLog();
        logEntry.setUserId(userId);
        logEntry.setMethod(httpMethod);
        logEntry.setEndpoint(uri);
        logEntry.setQueryParams(queryParams);

        int statusCode = 200; // Mặc định là 200 OK

        Object result;
        try {
            result = joinPoint.proceed();
            // Nếu controller trả về ResponseEntity thì lấy status từ đó
            if (result instanceof ResponseEntity<?> responseEntity) {
                statusCode = responseEntity.getStatusCode().value();
                Object body = responseEntity.getBody();

                // Nếu body là APIResponse, có thể log thêm thông tin
                if (body instanceof ApiResponse apiResponse) {
                    logEntry.setMessage(apiResponse.getMessage());
                }
            }

        } catch (Throwable ex) {
            throw ex;
        } finally {
            logEntry.setDurationMs(System.currentTimeMillis() - start);
            logEntry.setStatusCode(statusCode);
            log.info("API Call: {} {} - User ID: {}, Duration: {} ms, Status: {}",
                    httpMethod, uri, userId, logEntry.getDurationMs(), logEntry.getStatusCode());
            userLogRepository.save(logEntry);
        }

        return result;
    }

    private Long extractUserIdFromToken() {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.extractUserId(token);
        }
        return null;
    }
}
