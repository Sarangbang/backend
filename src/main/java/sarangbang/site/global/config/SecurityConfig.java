package sarangbang.site.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sarangbang.site.security.filter.JwtAuthenticationFilter;
import sarangbang.site.security.handler.JwtAccessDeniedHandler;
import sarangbang.site.security.handler.JwtAuthenticationEntryPoint;
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
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 공개적으로 접근 가능한 경로에 대한 보안 설정을 정의합니다.
     * Order(1)을  통해 다른 보안 필터 체인보다 먼저 실행됩니다.
     * 이 필터 체인은 JWT 토큰 검증 없이 특정 GET 요청을 허용하기 위해 사용됩니다.
     * securityMatcher에 정의된 경로 외의 요청은 이 필터 체인에서 처리되지 않습니다.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/api/regions/**", HttpMethod.GET.name()),
                        new AntPathRequestMatcher("/api/challenge/categories", HttpMethod.GET.name()),
                        new AntPathRequestMatcher("/api/challenges/**", HttpMethod.GET.name()),
                        new AntPathRequestMatcher("/api/categories/**", HttpMethod.GET.name()),
                        new AntPathRequestMatcher("/error")
                ))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }

    /**
     * JWT 인증이 필요한 경로에 대한 보안 설정을 정의합니다.
     * Order(2)를  통해 publicFilterChain 이후에 실행됩니다.
     * publicFilterChain에서 처리되지 않은 모든 요청은 이 필터 체인을 통과하며,
     * JwtAuthenticationFilter를 통해 토큰 기반 인증을 수행합니다.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http, Environment env) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    // 기본적으로 허용할 POST 경로들
                    authorize.requestMatchers(HttpMethod.POST, "/api/users/signin",  "/api/users/signup").permitAll();

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
