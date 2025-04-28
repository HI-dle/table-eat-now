/**
 * @author : jieun
 * @Date : 2025. 04. 29.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import table.eat.now.restaurant.restaurant.application.service.dto.request.RestaurantRatingUpdatedCommand;

@RequiredArgsConstructor
@Repository
public class JdbcRestaurantRepositoryImpl implements JdbcRestaurantRepository {

  private final JdbcTemplate jdbcTemplate;

  /**
   * 예시
   * UPDATE p_restaurant
   * SET review_rating_avg = CASE restaurant_uuid
   * WHEN 'uuid1' THEN 4.5
   * WHEN 'uuid2' THEN 3.8
   * WHEN 'uuid3' THEN 5.0
   * END
   * WHERE restaurant_uuid IN ('uuid1', 'uuid2', 'uuid3');
   */
  @Override
  public void batchModifyRestaurantRating(List<RestaurantRatingUpdatedCommand> commands) {
    if (commands.isEmpty()) {
      return;
    }

    StringBuilder sql = new StringBuilder();
    sql.append("UPDATE p_restaurant SET review_rating_avg = CASE restaurant_uuid ");

    for (RestaurantRatingUpdatedCommand command : commands) {
      sql.append("WHEN '")
          .append(command.restaurantUuid())
          .append("' THEN ")
          .append(command.averageRating())
          .append(" ");
    }

    sql.append("END WHERE restaurant_uuid IN (");
    sql.append(
        commands.stream()
            .map(command -> "'" + command.restaurantUuid() + "'")
            .collect(Collectors.joining(", "))
    );
    sql.append(")");

    jdbcTemplate.update(sql.toString());
  }
}
