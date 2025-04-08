package table.eat.now.user.presentation.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import table.eat.now.user.application.security.dto.CustomUserDetails;
import table.eat.now.user.presentation.security.LoginRequest;
import table.eat.now.user.presentation.security.jwt.TokenProvider;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final TokenProvider tokenProvider;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    setFilterProcessesUrl("/api/v1/users/login"); // 로그인 엔드포인트 지정
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {

    try {
      LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

      log.info("로그인 시도: {}", loginRequest.getUsername());

      return authenticationManager.authenticate(authToken);

    } catch (IOException e) {
      throw new RuntimeException("로그인 실패", e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult)
      throws IOException, ServletException {

    CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

    String token = tokenProvider.createAccessToken(
        userDetails.getMemberId().toString(),
        userDetails.getRole().toString(),
        userDetails.getUsername()
    );

    response.setHeader("Authorization", token);
    log.info("로그인 성공, 토큰 발급: {}", token);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed)
      throws IOException, ServletException {

    log.info("로그인 실패: {}", failed.getMessage());
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 실패");
  }
}
