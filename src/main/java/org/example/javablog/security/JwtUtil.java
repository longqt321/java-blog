package org.example.javablog.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    private String refreshToken;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.accessTokenExpirationMs}")
    private Long ACCESS_TOKEN_EXPIRES_IN_MILISECONDS;

    @Value("${jwt.refreshTokenExpirationMs}")
    private Long REFRESH_TOKEN_EXPIRES_IN_MILISECONDS;
    private SecretKey secretKey;
    @jakarta.annotation.PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String extractUsername(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Boolean isTokenExpired(String token){
        final Date expiredDate = getExpirationDateFromToken(token);
        return expiredDate.before(new Date());
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String,Object> claims = new HashMap<>();

        return doGenerateToken(claims,userDetails.getUsername(),ACCESS_TOKEN_EXPIRES_IN_MILISECONDS);
    }
    public String generateRefreshToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        if (!validateToken(this.refreshToken)){
            this.refreshToken = doGenerateToken(claims,userDetails.getUsername(),REFRESH_TOKEN_EXPIRES_IN_MILISECONDS);
        }

        return this.refreshToken;
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e){
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T getClaimFromToken(String token, Function<Claims,T> claimsSolver){
        final Claims claims = getAllClaimsFromToken(token);
        return claimsSolver.apply(claims);
    }

    private String doGenerateToken(Map<String,Object> claims,String subject,Long expirationInMilisecond){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationInMilisecond))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    private Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }

}
