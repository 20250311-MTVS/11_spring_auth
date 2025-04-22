package com.ohgiraffers.module01_securitysession.config;

import com.ohgiraffers.module01_securitysession.common.UserRole;
import com.ohgiraffers.module01_securitysession.config.handler.AuthFailHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private AuthFailHandler authFailHandler;

    /*
    * 비밀번호를 인코딩 하기 위한 bean
    * Bcrypt는 비밀번호 해싱에 가장 많이 사용되는 알고리즘 중 하나이다.
    * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain confiig(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/auth/login", "/user/signup", "/auth/fail","/").permitAll();
            auth.requestMatchers("/auth/*").hasAnyAuthority(UserRole.ADMIN.getRole());
            auth.requestMatchers("/user/*").hasAnyAuthority(UserRole.USER.getRole());
            auth.anyRequest().authenticated();
        }).formLogin( login ->{
            login.loginPage("/auth/login");
            login.usernameParameter("user");
            login.passwordParameter("pass");
            login.defaultSuccessUrl("/", true);
            login.failureUrl("/");
            login.failureHandler(authFailHandler);

        }).logout(logout ->{
            logout.logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"));
            logout.deleteCookies("JSESSIONID");
            logout.invalidateHttpSession(true);
            logout.logoutSuccessUrl("/");
        }).sessionManagement(session ->{
            session.maximumSessions(1);
            session.invalidSessionUrl("/");
        }).csrf(csrf -> csrf.disable());

        return http.build();
    }

}
