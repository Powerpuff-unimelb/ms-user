package com.powerpuff.demo.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Data
public class JwtUtils {
    private final Long accessTokenExpire = 1200L;
    private final Long refreshTokenExpire = 604800L;
    private final String secret = "powerpuffmikesecret";

    private Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

    public String generateAccessToken(String username, List<String> authorities, String issuer) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpire * 1000))
                .withIssuer(issuer)
                .withClaim("roles", authorities)
                .sign(algorithm);
    }

    public String generateRefreshToken(String username, String issuer) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpire * 1000))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public JWTVerifier buildVerifier() {
        return JWT.require(algorithm).build();
    }
}
