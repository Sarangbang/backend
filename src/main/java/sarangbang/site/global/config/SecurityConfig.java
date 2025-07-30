package sarangbang.site.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sarangbang.site.security.filter.JwtAuthenticationFilter;
import sarangbang.site.security.handler.JwtAccessDeniedHandler;
import sarangbang.site.security.handler.JwtAuthenticationEntryPoint;
import sarangbang.site.security.oauth.OAuth2LoginFailureHandler;
import sarangbang.site.security.oauth.OAuth2LoginSuccessHandler;
import sarangbang.site.user.service.CustomUserDetailsService;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://*.ilsim.site",
                "https://ilsim.site",
                "https://www.ilsim.site",
                "https://dev.ilsim.site"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // filterChain 메소드에 Environment 파라미터 추가
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, Environment env) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic 비활성화
                .authorizeHttpRequests(authorize -> {
                    // 기본적으로 허용할 POST 경로들
                    authorize.requestMatchers(HttpMethod.POST, "/api/users/signin",  "/api/users/signup", "/api/users/refresh", "/api/users/logout").permitAll();

                    // 이미지 업로드 경로 허용 (컨트롤러에서 용도별 인증 체크)
                    authorize.requestMatchers(HttpMethod.POST, "/api/upload/**").permitAll();

                    // OAuth2 로그인 관련 경로 허용 (경로 변경)
                    authorize.requestMatchers("/api/oauth2/**", "/api/login/oauth2/**").permitAll();

                    // 기본적으로 허용할 GET 경로들
                    authorize.requestMatchers(HttpMethod.GET,
                            "/api/challenge/categories",
                            "/api/challenges/**",
                            "/api/categories/**",
                            "/api/regions/**",
                            "/api/files/**",  // 파일 다운로드 허용
                            "/error",
                            "/ws/chat/**",
                            "/api/notifications/subscribe"
                    ).permitAll();

                    // "dev" 프로필이 활성화되었는지 확인
                    if (env.acceptsProfiles(Profiles.of("dev"))) {
                        // "dev" 프로필일 때만 Swagger UI 경로를 허용
                        authorize.requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll();
                    }

                    // 위에서 정의한 경로 외 모든 요청은 인증 필요
                    authorize.anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization ->
                                authorization.baseUri("/api/oauth2/authorization")   // 기존 /oauth2/authorization
                        )
                        .redirectionEndpoint(redirection ->
                                // 기본은 /login/oauth2/code/*
                                redirection.baseUri("/api/login/oauth2/code/*")
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
