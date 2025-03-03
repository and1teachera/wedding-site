package com.zlatenov.wedding_backend.security;

import com.zlatenov.wedding_backend.exception.InvalidTokenSignatureException;
import com.zlatenov.wedding_backend.exception.MalformedTokenException;
import com.zlatenov.wedding_backend.exception.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // List of paths that don't require authentication
    private final List<String> publicPaths = Arrays.asList("/auth/login", "/auth/login-by-names");


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip token validation for public endpoints
        if (isPublicPath(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No JWT token found in request to {}", request.getServletPath());
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(7);
            if (jwt.isEmpty()) {
                log.warn("Empty JWT token in request to {}", request.getServletPath());
                filterChain.doFilter(request, response);
                return;
            }
            
            try {
                MDC.put("jwtValidation", "PROCESSING");
                
                // Extract username for logging before validation
                String usernameForLogging = null;
                try {
                    usernameForLogging = jwtTokenProvider.getUsernameFromToken(jwt);
                    MDC.put("tokenUsername", usernameForLogging);
                } catch (Exception e) {
                    log.debug("Could not extract username from token for logging: {}", e.getMessage());
                }
                
                boolean valid = jwtTokenProvider.validateToken(jwt);
                
                if (valid) {
                    MDC.put("jwtValidation", "VALID");
                    log.debug("Valid JWT token for user: {}", usernameForLogging);
                    
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                jwtTokenProvider.getRolesFromToken(jwt)
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        log.info("User authenticated: {}", username);
                        MDC.put("userId", username);
                    }
                } else {
                    MDC.put("jwtValidation", "INVALID");
                    log.warn("Invalid JWT token");
                }
            } catch (TokenExpiredException e) {
                MDC.put("jwtValidation", "EXPIRED");
                log.warn("JWT token expired: {}", e.getMessage());
                throw e;
            } catch (MalformedTokenException e) {
                MDC.put("jwtValidation", "MALFORMED");
                log.warn("JWT token malformed: {}", e.getMessage());
                throw e;
            } catch (InvalidTokenSignatureException e) {
                MDC.put("jwtValidation", "INVALID_SIGNATURE");
                log.warn("JWT token has invalid signature: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                MDC.put("jwtValidation", "ERROR");
                log.error("JWT validation error: {}", e.getMessage(), e);
                throw e;
            } finally {
                MDC.remove("jwtValidation");
                MDC.remove("tokenUsername");
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isPublicPath(String servletPath) {
        return publicPaths.stream().anyMatch(servletPath::startsWith);
    }
}