package auth.document;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtService {

    private static final long EXPIRATION_MS = 1000 * 60 * 60; // 1 hora

    private static String getSecret() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            // usado APENAS em testes locais
            secret = "ZHVtbXktc2VjcmV0LWtleS1mb3Itand0LWhzMjU2LXN1cGVyLXNlY3VyZQ==";
        }
        return secret;
    }

    private static Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(String documentNumber) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(documentNumber)
                .claim("documentNumber", documentNumber)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
