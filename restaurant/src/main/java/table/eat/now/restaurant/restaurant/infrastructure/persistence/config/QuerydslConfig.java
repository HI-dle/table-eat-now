/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

  @Bean
  JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
    return new JPAQueryFactory(entityManager);
  }
}
