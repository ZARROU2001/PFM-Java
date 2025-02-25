package com.perso.ecomm.security;

import com.perso.ecomm.JWT.JWTAuthenticationFilter;
import com.perso.ecomm.JWT.OAuth2LoginSuccessHandler;
import com.perso.ecomm.oauth.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;


    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationEntryPoint authenticationEntryPoint, CustomOAuth2UserService customOAuth2UserService, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        (auth) ->
                                auth
                                        .anyRequest().permitAll()
                )
                .oauth2Login(
                        oauth -> oauth
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                                .successHandler(oAuth2LoginSuccessHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }


}
