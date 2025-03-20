package org.example.javablog.util;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


import javax.crypto.SecretKey;
import java.util.Date;


public class JwtUtil {
    private final String SECRET_KEY = "dHJhbmR1Y2xvbmdfc2luaHZpZW5iYWNoa2hvYWRhbmFuZ19oZWhl";
    private final Long TOKEN_EXPIRES_IN = 1000*60*60L; // 1hour

    private final SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+TOKEN_EXPIRES_IN))
                .signWith(secretKey,SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
