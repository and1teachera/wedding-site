package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.security.JwtTokenProvider;
import com.zlatenov.wedding_backend.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Debug controller to help diagnose authentication issues.
 * This controller should be removed in production.
 */
@Slf4j
@RestController
@RequestMapping("/debug")
public class DebugController {
    
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;
    
    @Value("${spring.security.jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;
    
    @GetMapping("/env")
    public ResponseEntity<Map<String, String>> getEnvInfo() {
        Map<String, String> envInfo = new HashMap<>();
        envInfo.put("jwtSecretLength", String.valueOf(jwtSecret.length()));
        envInfo.put("jwtSecretFirstChar", jwtSecret.substring(0, 1));
        envInfo.put("jwtExpiration", jwtExpiration.toString());
        envInfo.put("activeProfiles", String.join(",", environment.getActiveProfiles()));
        envInfo.put("springJacksonAutoConfig", String.valueOf(applicationContext.containsBeanDefinition("jacksonObjectMapper")));
        
        return ResponseEntity.ok(envInfo);
    }
    
    @GetMapping("/test-jwt")
    public ResponseEntity<Map<String, String>> testJwt() {
        try {
            // Create a simple test token
            Map<String, String> result = new HashMap<>();
            Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
            String token = Jwts.builder()
                .setSubject("test-subject")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
            
            result.put("success", "true");
            result.put("token", token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> result = new HashMap<>();
            result.put("success", "false");
            result.put("error", e.getClass().getName());
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/auth-flow")
    public ResponseEntity<Map<String, Object>> testAuthFlow() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Check database connection
            boolean dbConnected = true; // Simplified for example
            result.put("dbConnected", dbConnected);
            
            // Try to fetch a test user (use the first one found)
            try {
                User testUser = userService.getUserById(1L);
                if (testUser != null) {
                    result.put("userFound", true);
                    result.put("userId", testUser.getId());
                    result.put("userName", testUser.getFirstName() + " " + testUser.getLastName());
                    
                    // Try to generate a token
                    String token = jwtTokenProvider.generateToken(testUser);
                    result.put("tokenGenerated", token != null);
                    result.put("tokenLength", token.length());
                    
                    // Check token parsing
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    result.put("tokenParsed", username != null);
                    result.put("tokenUsername", username);
                } else {
                    result.put("userFound", false);
                    result.put("userError", "No users found in the database");
                }
            } catch (Exception e) {
                result.put("userFound", false);
                result.put("userError", e.getMessage());
                result.put("exception", e.getClass().getName());
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("stackTrace", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(500).body(result);
        }
    }
    
    @GetMapping("/simple-response")
    public ResponseEntity<Map<String, String>> simpleResponse() {
        log.info("Simple response test endpoint called");
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        log.info("Returning simple map response: {}", response);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/direct-jwt")
    public ResponseEntity<String> directJwt() {
        log.info("Direct JWT test endpoint called");
        try {
            User user = userService.getUserById(1L);
            log.info("Found user: {} {}", user.getFirstName(), user.getLastName());
            
            String token = jwtTokenProvider.generateToken(user);
            log.info("Generated token with length: {}", token.length());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(token);
        } catch (Exception e) {
            log.error("Error generating direct JWT: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error: " + e.getMessage());
        }
    }
}