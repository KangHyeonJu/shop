package com.shop.config;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
//위 어노테이션이 달린 클래스에 @Bean 어노테이션이 붙은 메서드를 등록하면 해당 메서드의 반환 값이 스프링 빈으로 등록됨
@EnableWebSecurity
public class SecurityConfig {

    //스프링 시큐리티 필터 체인 필수(시큐리티 커스텀을 위해)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //로그인 전 요청 url을 세션에 기억해 두었다가 로그인 후 원래 요청했던 url로 이동하게 하는 설정
        //HttpSessionRequestCache cache = new HttpSessionRequestCache();
        //cache.setMatchingRequestParameterName(null);  //특정 파라미터가 없어도 항상 기억하게 함

        //1. 로그인 설정
         http.formLogin((it) -> it
                .loginPage("/members/login")    //커스텀 로그인 페이지 url
                .defaultSuccessUrl("/")         //로그인 성공 시 이동할 경로
                .usernameParameter("email")     //로그인에 사용할 파라미터 이름 (기본: username)
                .failureUrl("/members/login/error") //로그인 실패 시 이동 경로
        );

        //2. 로그아웃 설정
        http.logout( (it) -> it
                .logoutUrl("/members/logout")   //로그아웃 URL (해당 url 접근 시 로그아웃 처리)
                .logoutSuccessUrl("/")          //로그아웃 성공 시 리다이렉트
        );

        //3. 인가(접근 권한) 설정
        http.authorizeHttpRequests((req)->{req
                //모두 허용(비로그인도 가능)
                .requestMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()

                //로그인 후 admin 역할을 가진 사용자만 접근 가능
                .requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")

                .anyRequest().authenticated();  //그 외 모든 요청 로그인한 사용자만 접근 가능
        });

//        http.requestCache((it) -> it.requestCache(cache));

        //http.csrf((csrf) -> csrf.disable());

        //인증이 필요한 url에 비로그인 사용자가 접근할 경우
        //spring security가 자동으로 /members/login 으로 이동시키도록 설정
        http.exceptionHandling((e) -> e
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );
        return http.build();
    }

    //암호화(=인코딩한다)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
