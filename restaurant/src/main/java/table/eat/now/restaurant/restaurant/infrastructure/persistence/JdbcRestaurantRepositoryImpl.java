/**
 * @author : jieun
 * @Date : 2025. 04. 29.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import table.eat.now.restaurant.restaurant.application.service.dto.request.RestaurantRatingUpdatedCommand;

@RequiredArgsConstructor
@Repository
public class JdbcRestaurantRepositoryImpl implements JdbcRestaurantRepository {

  private final JdbcTemplate jdbcTemplate;

  /**
   * data라는 가상의 테이블을 만들고 restaurant_uuid가 같은 걸 찾아서 review_rating_avg를 업데이트한다.
   * 예시
   * UPDATE p_restaurant r
   * SET review_rating_avg = data.average_rating
   * FROM (
   *   VALUES
   *     ('uuid1', 4.5),
   *     ('uuid2', 3.8),
   *     ('uuid3', 5.0)
   * ) AS data(restaurant_uuid, average_rating)
   * WHERE r.restaurant_uuid = data.restaurant_uuid;
   */
  @Override
  public void batchModifyRestaurantRating(List<RestaurantRatingUpdatedCommand> commands) {
    if (commands.isEmpty()) {
      return;
    }

    StringBuilder sql = new StringBuilder();
    List<Object> params = new ArrayList<>();

    sql.append("UPDATE p_restaurant r ")
        .append("SET review_rating_avg = data.average_rating ")
        .append("FROM (VALUES ");

    for (int i = 0; i < commands.size(); i++) {
      sql.append("(?, ?)");
      if (i < commands.size() - 1) {
        sql.append(", ");
      }
      params.add(commands.get(i).restaurantUuid());
      params.add(commands.get(i).averageRating());
    }

    sql.append(") AS data(restaurant_uuid, average_rating) ")
        .append("WHERE r.restaurant_uuid = data.restaurant_uuid");

    jdbcTemplate.update(sql.toString(), params.toArray());
  }
}
