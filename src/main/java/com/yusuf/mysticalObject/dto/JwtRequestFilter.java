package com.yusuf.mysticalObject.dto;


import com.yusuf.mysticalObject.service.UserService;
import com.yusuf.mysticalObject.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private JwtUtil jwtUtil;
    private UserService userService;

    public JwtRequestFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        log.info("Processing request to: {}", request.getRequestURI());
        log.info("Auth header: {}", authHeader);

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                log.info("Successfully extracted username: {}", username);
                
                if (username != null) {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    log.info("User details loaded: {}", userDetails.getUsername());
                    
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        log.info("Token validated successfully");
                        
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        log.warn("Token validation failed");
                    }
                }
            } catch (Exception e) {
                log.error("JWT processing error: {}", e.getMessage(), e);
            }
        } else {
            log.warn("No Bearer token found in request");
        }

        filterChain.doFilter(request, response);
    }
}
