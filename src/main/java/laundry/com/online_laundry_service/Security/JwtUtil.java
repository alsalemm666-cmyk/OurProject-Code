package laundry.com.online_laundry_service.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import laundry.com.online_laundry_service.Entities.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key secretKey;
    private final long expiration = 1000 * 60 * 60 * 10; // 10 ساعات

    // نقرأ السر من application.properties
public JwtUtil(@Value("${app.jwt.secret:DEFAULT_SECRET_KEY_1234567890}") String secret) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
}

public String generateToken(User user) {
    long now = System.currentTimeMillis();

    return Jwts.builder()
            .setSubject(user.getEmail())
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expiration))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
}


    public String getEmailFromToken(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        String tokenEmail = getEmailFromToken(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = parseToken(token).getBody().getExpiration();
        return expirationDate.before(new Date());
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}
