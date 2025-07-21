package sarangbang.site.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.access-secret}")
    private String accessSecretStr;
    private Key accessSecretKey;
    private final long accessTokenValidity = 60 * 60 * 1000L; // 1시간

    @Value("${jwt.refresh-secret}")
    private String  refreshSecretStr;
    private Key refreshSecretKey;
    private final long refreshTokenValidity = 14 * 24 * 60 * 60 * 1000L;



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
}