package table.eat.now.gateway.filter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import table.eat.now.gateway.util.JwtResolver;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

	private final JwtResolver resolver;
	private static final List<String> EXCLUDED_PATHS = List.of(
			"/api/v1/users/signup",
			"/api/v1/users/login",
			"/springdoc/"
	);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();

		if (isExcludedPath(path)) {
			return chain.filter(exchange);
		}

		return validateToken(request)
				.map(tokenInfo -> addUserContext(exchange, tokenInfo))
				.map(chain::filter)
				.orElseGet(() -> rejectRequest(exchange));
	}

	private boolean isExcludedPath(String path) {
		return EXCLUDED_PATHS.stream()
				.anyMatch(path::startsWith);
	}

	private Optional<TokenInfo> validateToken(ServerHttpRequest request) {
		return Optional.ofNullable(request.getHeaders().getFirst("Authorization"))
				.filter(resolver::isValidHeader).map(resolver::removePrefix)
				.filter(resolver::isValidToken).map(this::createTokenInfo);
	}

	private Mono<Void> rejectRequest(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

	private record TokenInfo(String userId, String role) {

	}

	private TokenInfo createTokenInfo(String token) {
		return new TokenInfo(resolver.getUserId(token), resolver.getUserRole(token));
	}

	private ServerWebExchange addUserContext(ServerWebExchange exchange, TokenInfo tokenInfo) {
		ServerHttpRequest request = exchange.getRequest().mutate()
				.header("X-User-Id", tokenInfo.userId())
				.header("X-User-Role", tokenInfo.role())
				.build();

		return exchange.mutate().request(request).build();
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 10;
	}
}