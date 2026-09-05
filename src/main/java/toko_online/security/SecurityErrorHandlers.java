package toko_online.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import toko_online.model.dto.response.ApiResponse;

@Component
public class SecurityErrorHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityErrorHandlers.class);
    private final ObjectMapper objectMapper;

    public SecurityErrorHandlers(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException authException) throws IOException, ServletException {
        log.warn("Unauthorized access ke {} {} - {}",
                request.getMethod(), request.getRequestURI(), authException.getMessage());
        writeResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Autentikasi diperlukan.");
    }

    @Override
    public void handle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("Forbidden access ke {} {} - {}",
                request.getMethod(), request.getRequestURI(), accessDeniedException.getMessage());
        writeResponse(response, HttpServletResponse.SC_FORBIDDEN, "Anda tidak memiliki akses ke resource ini.");
    }

    private void writeResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> body = ApiResponse.error(message);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
