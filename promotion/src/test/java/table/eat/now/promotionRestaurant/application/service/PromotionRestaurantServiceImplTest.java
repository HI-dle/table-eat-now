package table.eat.now.promotionRestaurant.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import table.eat.now.promotionRestaurant.application.dto.request.CreatePromotionRestaurantCommand;
import table.eat.now.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;
import table.eat.now.promotionRestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotionRestaurant.domain.repository.PromotionRestaurantRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@ExtendWith(MockitoExtension.class)
class PromotionRestaurantServiceImplTest {

  @Mock
  private PromotionRestaurantRepository promotionRestaurantRepository;

  @InjectMocks
  private PromotionRestaurantServiceImpl promotionRestaurantService;

  @DisplayName("프로모션 레스토랑 서비스 테스트")
  @Test
  void promotion_restaurant_create_service_test() {
      // given
    CreatePromotionRestaurantCommand command = new CreatePromotionRestaurantCommand(
        UUID.randomUUID(),
        UUID.randomUUID()
    );
    PromotionRestaurant entity = command.toEntity();

    when(promotionRestaurantRepository.save(any(PromotionRestaurant.class)))
        .thenReturn(entity);

    // when
    CreatePromotionRestaurantInfo result = promotionRestaurantService.createPromotionRestaurant(
        command);

    // then
    assertThat(result.promotionUuid()).isEqualTo(command.promotionUuid());
    assertThat(result.restaurantUuid()).isEqualTo(command.restaurantUuid());

    verify(promotionRestaurantRepository).save(any(PromotionRestaurant.class));
  }
}