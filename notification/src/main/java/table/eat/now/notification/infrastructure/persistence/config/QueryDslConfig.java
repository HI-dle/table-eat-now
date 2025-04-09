package table.eat.now.notification.infrastructure.persistence.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 09.
 */
@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {

  private final EntityManager em;

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(em);
  }

}
