package com.elearning.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility component for creating and validating JSON Web Tokens (JWT).
 * Handles token generation, claim extraction, and expiration checks.
 */
@Component
public class JwtUtil {

    private final Key signingKey;
    private final long expirationMs;

    /**
     * Constructs a {@code JwtUtil} with a signing secret and expiration duration.
     *
     * @param secret       the HMAC secret key (minimum 32 bytes)
     * @param expirationMs token time-to-live in milliseconds
     */
    public JwtUtil(
            @Value("${jwt.secret:defaultSecretKeyThatIsAtLeast32BytesLong!!}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT containing the username, user ID, and role.
     *
     * @param username the subject of the token
     * @param userId   the user's unique identifier
     * @param role     the user's role
     * @return a compact JWT string
     */
    public String generateToken(String username, String userId, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (subject) from a token.
     *
     * @param token the JWT string
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the user ID claim from a token.
     *
     * @param token the JWT string
     * @return the user ID
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extracts the role claim from a token.
     *
     * @param token the JWT string
     * @return the role
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extracts a specific claim from a token using the provided resolver function.
     *
     * @param <T>            the claim type
     * @param token          the JWT string
     * @param claimsResolver function that extracts the desired claim
     * @return the resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    /**
     * Validates a token against the given user details.
     *
     * @param token       the JWT string
     * @param userDetails the Spring Security user details
     * @return {@code true} if the token is valid and not expired
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks whether a token has expired.
     *
     * @param token the JWT string
     * @return {@code true} if the token expiration date is in the past
     */
    public boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
