package sarangbang.site.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import sarangbang.site.security.details.CustomUserDetails;
import sarangbang.site.user.service.CustomUserDetailsService;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.access-secret}")
    private String accessSecretStr;
    private Key accessSecretKey;
    private final long accessTokenValidity = 60 * 60 * 1000L; // 1시간
//    private final long accessTokenValidity = 60 * 60 * 1000L; // 1시간

    @Value("${jwt.refresh-secret}")
    private String  refreshSecretStr;
    private Key refreshSecretKey;
    private final long refreshTokenValidity = 14 * 24 * 60 * 60 * 1000L;

    private final CustomUserDetailsService userDetailsService;

    @PostConstruct
    public void init() {
        // 환경변수에서 읽은 문자열을 Key 객체로 변환
        this.accessSecretKey = Keys.hmacShaKeyFor(accessSecretStr.getBytes(StandardCharsets.UTF_8));
        this.refreshSecretKey = Keys.hmacShaKeyFor(refreshSecretStr.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String userId, String email, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("email", email);
        claims.put("roles", roles);

        return createToken(claims, accessTokenValidity, accessSecretKey);
    }

    public String createRefreshToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        return createToken(claims, refreshTokenValidity, refreshSecretKey);
    }

    private String createToken(Claims claims, long validity, Key secretKey) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessSecretKey);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshSecretKey);
    }

    private boolean validateToken(String token, Key secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않은 경우 (서명 오류, 만료, 형식 오류 등)
            log.error("Invalid JWT token trace: {}", e.getMessage());
            return false;
        }
    }

    public String getUserIdFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getEmailFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public String getUserIdFromRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드입니다.
     * @param token 복호화할 JWT 토큰
     * @return Authentication Spring Security의 인증 정보 객체
     */
    public Authentication getAuthentication(String token) {
        String email = getEmailFromAccessToken(token);

        // 2. 추출한 아이디를 기반으로 UserDetailsService를 통해 DB에서 사용자 정보를 조회합니다.
        //    (loadUserByUsername 메서드를 호출합니다)
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        // 3. 조회된 UserDetails 객체를 기반으로 Authentication 객체를 생성합니다.
        //    이때 사용되는 UsernamePasswordAuthenticationToken은 3개의 파라미터를 가집니다.
        //    - principal: 인증된 사용자 정보 (UserDetails 객체)
        //    - credentials: 자격 증명 (보통 비밀번호, 여기선 사용하지 않으므로 빈 문자열)
        //    - authorities: 사용자가 가진 권한 목록;
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        return authentication;
    }
}