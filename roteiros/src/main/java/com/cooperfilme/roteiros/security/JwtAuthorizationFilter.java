package com.cooperfilme.roteiros.security;

import com.cooperfilme.roteiros.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtConfig jwtConfig;

    public JwtAuthorizationFilter(AuthenticationManager authManager, JwtConfig jwtConfig) {
        super(authManager);
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            try {
                token = token.replace("Bearer ", "");

                Jws<Claims> claimsJws = Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                        .build()
                        .parseSignedClaims(token);

                String user = claimsJws.getPayload().getSubject();
                String role = (String) claimsJws.getPayload().get("role");

                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(role)));
                }
            } catch (JwtException e) {
                logger.error("Não foi possível fazer parse do token JWT", e);
            }
        }
        return null;
    }
}
