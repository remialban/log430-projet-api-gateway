package ca.log430.api_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private Authentification authentificaion;




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/swagger-ui.html").permitAll()
                                .requestMatchers("/v3/**").permitAll()
                                .requestMatchers("/v2/api-docs").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/status").permitAll()
                                .requestMatchers("/users/auth").permitAll()
                                .requestMatchers("/users/auth/check").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers("/users/**").fullyAuthenticated()
                                .anyRequest().fullyAuthenticated()
                                //.requestMatchers(HttpMethod.POST, "/users").permitAll()
                                //.requestMatchers("/users/**").fullyAuthenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(authentificaion, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
