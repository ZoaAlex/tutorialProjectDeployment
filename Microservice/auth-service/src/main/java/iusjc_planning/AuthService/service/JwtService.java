package iusjc_planning.AuthService.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class JwtService {

    // Clé de 256 bits minimum
    private final String secretKey = "MY_SUPER_SECRET_KEY_FOR_JWT_256_BITS_XYZ123";

    public String generateToken(String email, String role) {

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }
}
