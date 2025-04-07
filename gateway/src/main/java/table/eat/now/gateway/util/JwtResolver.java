package table.eat.now.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtResolver {

	private static final String BEARER_PREFIX = "Bearer ";
	private static final String CLAIM_ROLE_KEY = "role";
	private final SecretKey key;

	public JwtResolver(@Value("${jwt.secret}") String secretKey) {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String getUserId(String token) {
		return getClaims(token, Claims::getSubject);
	}

	public String getUserRole(String token) {
		return getClaims(token, claims ->
				claims.get(CLAIM_ROLE_KEY, String.class));
	}

	private <T> T getClaims(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(removePrefix(token))
				.getPayload();
		return claimsResolver.apply(claims);
	}

	public String removePrefix(String header) {
		if (isValidHeader(header)) {
			return header.substring(BEARER_PREFIX.length());
		}
		return header;
	}

	public boolean isValidHeader(String header) {
		return header != null && header.startsWith(BEARER_PREFIX);
	}

	public boolean isValidToken(String token) {
		try {
			Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
