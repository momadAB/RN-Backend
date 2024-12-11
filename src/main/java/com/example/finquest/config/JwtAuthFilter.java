package com.example.finquest.config;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final JWTUtil jwtUtil;

    /**
     * Constructor for JwtAuthFilter.
     * @param jwtUtil - utility class for handling JWT tokens.
     */
    public JwtAuthFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Intercepts each incoming request and validates the JWT token.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Retrieve the Authorization header from the request
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        // Check if the Authorization header exists and starts with Bearer
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            String token = authorizationHeader.substring(7); // Extract the token after 'Bearer '

            try {
                // Validate the token
                if (jwtUtil.isTokenValid(token)) {

                    // Extract the username from the token
                    String username = jwtUtil.getUsernameFromToken(token);
                    if (username == null || username.isEmpty()) {
                        throw new RuntimeException("User not found in the token.");
                    }

                    // Extract roles from the token
                    String roles = jwtUtil.getRolesFromToken(token); // Example role format: "ROLE_USER,ROLE_ADMIN"
                    List<SimpleGrantedAuthority> authorities = Collections.emptyList();

                    if (roles != null && !roles.isEmpty()) {
                        authorities = List.of(roles.split(",")).stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                    }

                    // Create an authentication token for the user
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    // Attach additional details to the authentication object
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication in the SecurityContext for the current request
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired: " + e.getMessage());
            } catch (SignatureException e) {
                System.out.println("JWT signature does not match: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unable to authenticate user: " + e.getMessage());
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
