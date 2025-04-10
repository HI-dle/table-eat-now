/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.restaurant.restaurant.application.service.dto.request.CreateRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.response.CreateRestaurantInfo;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantRepository;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

  private final RestaurantRepository restaurantRepository;

  @Override
  public CreateRestaurantInfo createRestaurant(CreateRestaurantCommand command) {
    return CreateRestaurantInfo.from(restaurantRepository.save(command.toEntity()));
  }
}
