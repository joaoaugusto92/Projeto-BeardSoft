package com.api.beard_soft.infra.security;

import com.api.beard_soft.domain.user.UserEntity;
import com.api.beard_soft.repository.UserRepository;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository, CustomUserDetailsService userDetailsService){
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;

    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);

        if (token != null) {
            try {
                DecodedJWT decodedJWT = tokenService.validateToken(token);
                String email = decodedJWT.getSubject();
                /*String role = decodedJWT.getClaim("role").asString(); // Ex: "ADMIN"
                String authorityString = "ROLE_" + role;*/

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                System.out.println("SecurityFilter - Token Válido. Email: " + email);


                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                System.err.println("SecurityFilter - Erro na validação do token: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7); // "Bearer ".length() é 7
    }

}
