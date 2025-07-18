package sarangbang.site.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.access-secret}")
    private String secretKeyStr;

    private Key secretKey;
    private final long accessTokenValidity = 60 * 60 * 1000L; // 1시간

    /**
     * DB에서 사용자 정보를 가져오는 UserDetailsService를 주입받습니다.
     * 이 서비스는 직접 구현해야 합니다. (예: UserDetailsServiceImpl)
     */
    private final UserDetailsService userDetailsService;

    @PostConstruct
    public void init() {
        // 환경변수에서 읽은 문자열을 Key 객체로 변환
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String userId, String email, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("id", userId);
        claims.put("roles", roles);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
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
        // 1. 토큰에서 사용자의 아이디(subject)를 추출합니다.
        String userIdentifier = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        // 2. 추출한 아이디를 기반으로 UserDetailsService를 통해 DB에서 사용자 정보를 조회합니다.
        //    (loadUserByUsername 메서드를 호출합니다)
        UserDetails userDetails = userDetailsService.loadUserByUsername(userIdentifier);

        // 3. 조회된 UserDetails 객체를 기반으로 Authentication 객체를 생성합니다.
        //    이때 사용되는 UsernamePasswordAuthenticationToken은 3개의 파라미터를 가집니다.
        //    - principal: 인증된 사용자 정보 (UserDetails 객체)
        //    - credentials: 자격 증명 (보통 비밀번호, 여기선 사용하지 않으므로 빈 문자열)
        //    - authorities: 사용자가 가진 권한 목록
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        return authentication;
    }
}