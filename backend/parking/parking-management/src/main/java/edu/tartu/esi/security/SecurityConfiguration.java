//package edu.tartu.esi.security;
//
////import edu.tartu.esi.config.JwtAuthenticationFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import static edu.tartu.esi.security.Role.ADMIN;
//import static edu.tartu.esi.security.Role.LANDLORD;
//import static org.springframework.http.HttpMethod.*;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@EnableMethodSecurity
//public class SecurityConfiguration {
//
//    private final JwtAuthenticationFilter jwtAuthFilter;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf()
//                .disable()
//                .authorizeHttpRequests()
//                .requestMatchers(
//                        "/api/v1/auth/**",
//                        "/v2/api-docs",
//                        "/v3/api-docs",
//                        "/v3/api-docs/**",
//                        "/swagger-resources",
//                        "/swagger-resources/**",
//                        "/configuration/ui",
//                        "/configuration/security",
//                        "/swagger-ui/**",
//                        "/webjars/**",
//                        "/swagger-ui.html"
//                )
//                .permitAll()
//
//                .requestMatchers(GET, "/api/v1/parking-slots/by-id/**").hasAnyRole(ADMIN.name(), LANDLORD.name())
//                .requestMatchers(GET, "/api/v1/parking-slots/by-status/**").permitAll()
//                .requestMatchers(GET, "/api/v1/parking-slots/by-location/**").permitAll()
//                .requestMatchers(GET, "/api/v1/parking-slots/by-landlord/**").hasAnyRole(ADMIN.name(), LANDLORD.name())
//                .requestMatchers(POST, "/api/v1/parking-slots").hasAnyRole(ADMIN.name(), LANDLORD.name())
//                .requestMatchers(PUT, "/api/v1/parking-slots/**").hasAnyRole(ADMIN.name(), LANDLORD.name())
//                .requestMatchers(DELETE, "/api/v1/parking-slots/**").hasAnyRole(ADMIN.name(), LANDLORD.name())
//
////                .requestMatchers(GET, "/api/v1/parking-slots/by-id/**").hasRole("LANDLORD")
////                .requestMatchers(GET, "/api/v1/parking-slots/by-landlord/**").hasAnyRole("ADMIN", "LANDLORD")
////                .requestMatchers(POST, "/api/v1/parking-slots").hasAnyRole("ADMIN", "LANDLORD")
////                .requestMatchers(PUT, "/api/v1/parking-slots/**").hasAnyRole("ADMIN", "LANDLORD")
////                .requestMatchers(DELETE, "/api/v1/parking-slots/**").hasAnyRole("ADMIN", "LANDLORD")
//
//                //.requestMatchers(DELETE, "/api/v1/admin/**").hasAuthority(ADMIN_DELETE.name())*/
//
//                .anyRequest()
//                .authenticated()
//
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//}