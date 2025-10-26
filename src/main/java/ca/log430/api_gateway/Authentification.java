package ca.log430.api_gateway;

import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class Authentification extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("bypass auth");

        if (request.getRequestURI().startsWith("/users/auth") || request.getRequestURI().equals("/users")) {
            filterChain.doFilter(request, response);
            System.out.println("bypass auth");
            return;
        }
        System.out.println("doFilterInternal");
        // Get bearer token from Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String tokenValue = authorizationHeader.substring(7);

        // Usin io.json web token library to validate token now validate :
        // check if token is valid
        // make get http request :

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/users/auth/check?token=" + tokenValue;
        System.out.println("Token: " + tokenValue);
        ResponseEntity<HashMap> response1 = restTemplate.getForEntity(url, HashMap.class);
        if (response1.getStatusCode().is2xxSuccessful()) {
            // get paylaod
            if (response1.getBody().get("data") == null) {;
                System.out.println("test");
                ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                grantedAuthorities.add(new SimpleGrantedAuthority("SERVICE"));
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", null, grantedAuthorities);
                token.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(token);


                filterChain.doFilter(request, response);
                return;
            }

            ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("user", null, grantedAuthorities);
            token.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(token);


            filterChain.doFilter(request, response);
            return;
        }




        filterChain.doFilter(request, response);
    }
}
