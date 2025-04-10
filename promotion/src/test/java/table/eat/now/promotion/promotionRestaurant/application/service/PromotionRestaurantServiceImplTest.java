package table.eat.now.promotion.promotionRestaurant.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotionRestaurant.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.excepton.PromotionRestaurantErrorCode;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.CreatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.SearchPromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.UpdatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.SearchPromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.UpdatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotion.promotionRestaurant.domain.repository.PromotionRestaurantRepository;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionRestaurant.domain.repository.search.PromotionRestaurantSearchCriteriaQuery;

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
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString()
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

  @DisplayName("promotionRestaurantUuid로 프로모션-레스토랑 정보를 수정한다.")
  @Test
  void update_promotion_restaurant_service_test() {
    // given
    String promotionRestaurantUuid = UUID.randomUUID().toString();

    UpdatePromotionRestaurantCommand command = new UpdatePromotionRestaurantCommand(
        "promotion-uuid-after",
        "restaurant-uuid-after"
    );

    PromotionRestaurant existing = PromotionRestaurant.of(
        "promotion-uuid-before",
        "restaurant-uuid-before");

    ReflectionTestUtils.setField(existing,
        "promotionRestaurantUuid", promotionRestaurantUuid);

    when(promotionRestaurantRepository.findByPromotionRestaurantUuidAndDeletedAtIsNull(promotionRestaurantUuid))
        .thenReturn(Optional.of(existing));

    // when
    UpdatePromotionRestaurantInfo result = promotionRestaurantService
        .updatePromotionRestaurant(command, promotionRestaurantUuid);

    // then
    assertThat(result.promotionRestaurantUuid()).isEqualTo(promotionRestaurantUuid);
    assertThat(result.promotionUuid()).isEqualTo(command.promotionUuid());
    assertThat(result.restaurantUuid()).isEqualTo(command.restaurantUuid());

    verify(promotionRestaurantRepository).findByPromotionRestaurantUuidAndDeletedAtIsNull(promotionRestaurantUuid);
  }

  @DisplayName("존재하지 않는 promotionRestaurantUuid로 수정 시 예외가 발생한다.")
  @Test
  void update_promotion_restaurant_fail_not_found_test() {
    // given
    String invalidUuid = UUID.randomUUID().toString();

    UpdatePromotionRestaurantCommand command = new UpdatePromotionRestaurantCommand(
        "promotion-uuid-after",
        "restaurant-uuid-after"
    );

    when(promotionRestaurantRepository.
        findByPromotionRestaurantUuidAndDeletedAtIsNull(invalidUuid))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        promotionRestaurantService.updatePromotionRestaurant(command, invalidUuid)
    ).isInstanceOf(CustomException.class)
        .hasMessage(PromotionRestaurantErrorCode.INVALID_PROMOTION_RESTAURANT_UUID.getMessage());

    verify(promotionRestaurantRepository).
        findByPromotionRestaurantUuidAndDeletedAtIsNull(invalidUuid);
  }

  @DisplayName("레스토랑 아이디와 프로모션 아이디로 프로모션-레스토랑 정보를 검색한다.")
  @Test
  void search_promotion_restaurant_service_test() {
    // given
    SearchPromotionRestaurantCommand command = new SearchPromotionRestaurantCommand(
        "promotion-uuid",
        "restaurant-uuid",
        true,
        "createdAt",
        0,
        10
    );

    PromotionRestaurantSearchCriteriaQuery query1 = new PromotionRestaurantSearchCriteriaQuery(
        "promotion-restaurant-uuid-1",
        "promotion-uuid",
        "restaurant-uuid"
    );

    PromotionRestaurantSearchCriteriaQuery query2 = new PromotionRestaurantSearchCriteriaQuery(
        "promotion-restaurant-uuid-2",
        "promotion-uuid",
        "restaurant-uuid"
    );

    PaginatedResult<PromotionRestaurantSearchCriteriaQuery> paginatedResult =
        new PaginatedResult<>(
            List.of(query1, query2),
            0,
            10,
            2L,
            1
        );

    when(promotionRestaurantRepository.searchPromotionRestaurant(command.toCriteria()))
        .thenReturn(paginatedResult);

    // when
    PaginatedResultCommand<SearchPromotionRestaurantInfo> result =
        promotionRestaurantService.searchPromotionRestaurant(command);

    // then
    assertThat(result.content()).hasSize(2);
    assertThat(result.content().get(0).promotionRestaurantUuid()).isEqualTo("promotion-restaurant-uuid-1");
    assertThat(result.content().get(1).promotionRestaurantUuid()).isEqualTo("promotion-restaurant-uuid-2");
    assertThat(result.page()).isEqualTo(0);
    assertThat(result.size()).isEqualTo(10);
    assertThat(result.totalElements()).isEqualTo(2L);
    assertThat(result.totalPages()).isEqualTo(1);

    verify(promotionRestaurantRepository).searchPromotionRestaurant(command.toCriteria());
  }

}