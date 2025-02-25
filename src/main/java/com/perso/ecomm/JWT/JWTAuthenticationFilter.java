package com.perso.ecomm.JWT;

import com.perso.ecomm.CustomUser.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(7);
        String subject = jwtUtil.getSubject(jwt);
        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            String ab = userDetails.getUsername();
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                logger.debug("test");
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                logger.debug("test2");
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.debug("test3");
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                Authentication a = SecurityContextHolder.getContext().getAuthentication();

            }
        }
        filterChain.doFilter(request, response);


    }
}
