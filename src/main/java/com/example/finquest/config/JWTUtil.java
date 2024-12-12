package com.example.finquest.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JWTUtil {

    private final String jwtSignKey = "secret";

    /**
     * Generates a JWT token using the provided username, roles, and additional claims.
     *
     * @param username The username for which the token is being generated.
     * @param roles The roles assigned to the user (PARENT_USER or CHILD_USER).
//     * @param userType The type of the user (ParentUser or ChildUser).
     * @param claims Optional additional claims to be included in the token.
     * @return The generated JWT token.
     */
    public String generateToken(String username, String roles, Map<String, Object> claims) {
        claims.put("roles", roles);
//        claims.put("userType", userType); // Helps distinguish between Parent and Child users
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24))) // Token valid for 24 hours
                .signWith(SignatureAlgorithm.HS256, jwtSignKey)
                .compact();
    }

    /**
     * Extracts specific claim from the token using a custom claims resolver.
     *
     * @param token The JWT token.
     * @param claimsResolver A function to extract the desired claim from the token.
     * @param <T> The type of the claim to extract.
     * @return The extracted claim.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token.
     * @return All claims present in the token.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSignKey).parseClaimsJws(token).getBody();
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Checks if the token has expired.
     *
     * @param token The JWT token.
     * @return True if the token has expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        Date tokenExpirationDate = getExpirationDateFromToken(token);
        return tokenExpirationDate.before(new Date());
    }

    /**
     * Validates the JWT token to ensure it is not expired and is properly signed.
     *
     * @param token The JWT token.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts roles (if any) from the JWT token.
     *
     * @param token The JWT token.
     * @return The roles present in the token as a string.
     */
    public String getRolesFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("roles", String.class));
    }

    /**
     * Extracts userType (if any) from the JWT token to distinguish if it's a ParentUser or ChildUser.
     *
     * @param token The JWT token.
     * @return The userType present in the token as a string (e.g., "PARENT_USER" or "CHILD_USER").
     */
//    public String getUserTypeFromToken(String token) {
//        return getClaimFromToken(token, claims -> claims.get("userType", String.class));
//    }
}
