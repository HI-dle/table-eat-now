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
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import table.eat.now.user.application.security.dto.CustomUserDetails;
import table.eat.now.user.presentation.security.LoginRequest;
import table.eat.now.user.presentation.security.jwt.TokenProvider;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenProvider tokenProvider;
  private final AuthenticationConfiguration authenticationConfiguration;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

    if (!request.getRequestURI().equals("/api/v1/users/login")) {
      chain.doFilter(request, response);
      return;
    }

    try {
      log.info("로그인 진행");
      LoginRequest loginRequest = new ObjectMapper().readValue(
          request.getInputStream(), LoginRequest.class);

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());


      AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

      Authentication authentication = authenticationManager.authenticate(authToken);

      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      String token = tokenProvider.createAccessToken(
          userDetails.getMemberId().toString(),
          userDetails.getRole().toString(),
          userDetails.getUsername());

      response.setHeader("Authorization", token);
      log.info("성공?");
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (AuthenticationException e) {
      log.error("로그인 실패: {}", e.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 실패");
      return;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return;
  }
}
