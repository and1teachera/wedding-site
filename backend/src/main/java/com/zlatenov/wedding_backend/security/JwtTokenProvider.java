package com.zlatenov.wedding_backend.security;

import com.zlatenov.wedding_backend.exception.InvalidTokenSignatureException;
import com.zlatenov.wedding_backend.exception.MalformedTokenException;
import com.zlatenov.wedding_backend.exception.TokenExpiredException;
import com.zlatenov.wedding_backend.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.security.jwt.expiration}")
    private Long jwtExpirationInMs;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", user.isAdmin() ? "ADMIN" : "GUEST");

        return createToken(claims, user.getFirstName() + " " + user.getLastName());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            throw new TokenExpiredException("JWT token has expired");
        } catch (MalformedJwtException e) {
            log.warn("JWT malformed: {}", e.getMessage());
            throw new MalformedTokenException("JWT token is malformed");
        } catch (SignatureException e) {
            log.warn("JWT signature invalid: {}", e.getMessage());
            throw new InvalidTokenSignatureException("JWT token has invalid signature");
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("JWT validation error: {}", e.getMessage());
            throw new MalformedTokenException("JWT token validation failed");
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    @SuppressWarnings("unchecked")
    public Collection<SimpleGrantedAuthority> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String userType = claims.get("userType", String.class);
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType));
    }
}