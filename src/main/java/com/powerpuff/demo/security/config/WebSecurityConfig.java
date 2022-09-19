package com.powerpuff.demo.security.config;

import com.powerpuff.demo.security.handler.*;
import com.powerpuff.demo.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    AuthenticationEntry authenticationEntry;

    AuthenticationSuccess authenticationSuccess;

    AuthenticationFailure authenticationFailure;

    AuthenticationLogout authenticationLogout;

    AccessDeny accessDeny;

    SessionInformationExpired sessionInformationExpiredStrategy;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
//                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                    .ignoringAntMatchers("/sign-up/**").and()
                .cors().and()

                .authenticationProvider(authenticationProvider())
                .authorizeRequests()
                .antMatchers("/sign-up/**").permitAll()
                .anyRequest()
                .authenticated().and()

                .formLogin()
                .permitAll()
                .successHandler(authenticationSuccess)
                .failureHandler(authenticationFailure).and()

//                .rememberMe()
//                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(14))
//                .key("thisisaverysrcuredkey").and()

                .logout().permitAll()
                .logoutSuccessHandler(authenticationLogout)
                .deleteCookies().and()

                .exceptionHandling()
                .accessDeniedHandler(accessDeny)
                .authenticationEntryPoint(authenticationEntry).and()

                .sessionManagement()
                .maximumSessions(1)
                .expiredSessionStrategy(sessionInformationExpiredStrategy);

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);

        return authProvider;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
