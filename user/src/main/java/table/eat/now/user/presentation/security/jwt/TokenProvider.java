package table.eat.now.user.presentation.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@Component
@Slf4j
public class TokenProvider {

  public static final String BEARER_PREFIX = "Bearer ";
  private static final String CLAIM_ROLE_KEY = "role";
  private static final String CLAIM_USERNAME_KEY = "username";
  private final SecretKey key;
  private final long token_time;

  public TokenProvider(
      @Value("${jwt.secret}") String secretKey,
      @Value("${jwt.access-expiration}") long token_time) {
    this.token_time = token_time;
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  public String createAccessToken(String userId, String role, String username) {
    String token = createToken(userId, role, username, token_time);
    return addBearerPrefix(token);
  }

  private String createToken(String userId, String role, String username, long expiration) {
    return Jwts.builder()
        .subject(userId)
        .claim(CLAIM_ROLE_KEY, role)
        .claim(CLAIM_USERNAME_KEY, username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(key)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(removeBearerPrefix(token));
      return true;
    } catch (ExpiredJwtException e) {
      log.error("에러");
      return false;
    } catch (JwtException e) {
      log.error("에러");
      return false;
    }
  }

  public String getUserIdFromToken(String token) {
    return getClaimFromToken(removeBearerPrefix(token), Claims::getSubject);
  }

  public String getUserRoleFromToken(String token) {
    return getClaimFromToken(removeBearerPrefix(token),
        claims -> claims.get(CLAIM_ROLE_KEY, String.class));
  }
  public String getUserNameFromToken(String token) {
    return getClaimFromToken(removeBearerPrefix(token),
        claims -> claims.get(CLAIM_USERNAME_KEY, String.class));
  }

  private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    Claims claims = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(removeBearerPrefix(token))
        .getPayload();
    return claimsResolver.apply(claims);
  }

  private String addBearerPrefix(String token) {
    return BEARER_PREFIX + token;
  }

  public String removeBearerPrefix(String token) {
    if (token != null && token.startsWith(BEARER_PREFIX)) {
      return token.substring(BEARER_PREFIX.length());
    }
    return token;
  }
}
