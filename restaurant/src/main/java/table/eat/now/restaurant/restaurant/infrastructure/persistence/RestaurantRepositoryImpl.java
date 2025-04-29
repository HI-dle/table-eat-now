/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantsCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.request.RestaurantRatingUpdatedCommand;
import table.eat.now.restaurant.restaurant.domain.dto.response.Paginated;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantRepository;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

  private final JpaRestaurantRepository jpaRestaurantRepository;
  private final JdbcRestaurantRepository jdbcRestaurantRepository;

  @Override
  public Restaurant save(Restaurant entity) {
    return jpaRestaurantRepository.save(entity);
  }

  @Override
  public Optional<Restaurant> findByRestaurantUuidWithMenusAndTimeslots(String restaurantUuid) {
    return jpaRestaurantRepository.findByRestaurantUuid(restaurantUuid);
  }

  @Override
  public List<Restaurant> findAll() {
    return jpaRestaurantRepository.findAll();
  }

  @Override
  public <S extends Restaurant> List<S> saveAll(Iterable<S> restaurants) {
    return jpaRestaurantRepository.saveAll(restaurants);
  }

  @Override
  public Optional<Restaurant> findByDynamicCondition(
      String restaurantUuid, boolean includeDeleted, boolean includeInactive) {
    return jpaRestaurantRepository.findByDynamicCondition(
        restaurantUuid, includeDeleted, includeInactive);
  }

  @Override
  public Optional<Restaurant> findByStaffIdOrOwnerId(Long id) {
    return jpaRestaurantRepository.findByStaffIdOrOwnerIdWithSingleParam(id);
  }

  @Override
  public Paginated<Restaurant> searchRestaurants(GetRestaurantsCriteria criteria) {
    return jpaRestaurantRepository.searchRestaurants(criteria);
  }

  @Override
  public boolean isOwner(Long userId, String restaurantUuid) {
    return jpaRestaurantRepository.isOwnerByUserIdAndRestaurantUuid(userId, restaurantUuid);
  }

  @Override
  public boolean isStaff(Long userId, String restaurantUuid) {
    return jpaRestaurantRepository.isStaffByUserIdAndRestaurantUuid(userId, restaurantUuid);
  }

  @Override
  public void batchModifyRestaurantRating(List<RestaurantRatingUpdatedCommand> commands) {
    jdbcRestaurantRepository.batchModifyRestaurantRating(commands);
  }

}
