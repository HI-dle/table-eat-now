package table.eat.now.promotion.promotionUser.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
import table.eat.now.promotion.promotionUser.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionUser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.request.SearchPromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.request.UpdatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.dto.response.SearchPromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.dto.response.UpdatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.exception.PromotionUserErrorCode;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionUser.domain.repository.PromotionUserRepository;
import table.eat.now.promotion.promotionUser.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionUser.domain.repository.search.PromotionUserSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@ExtendWith(MockitoExtension.class)
class PromotionUserServiceImplTest {

  @Mock
  private PromotionUserRepository promotionUserRepository;

  @InjectMocks
  private PromotionUserServiceImpl promotionUserService;

  @DisplayName("프로모션 유저 서비스 테스트")
  @Test
  void promotion_user_create_service_test() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    CreatePromotionUserCommand command = new CreatePromotionUserCommand(1L, promotionUuid);

    PromotionUser entity = command.toEntity();

    when(promotionUserRepository.save(any(PromotionUser.class)))
        .thenReturn(entity);

    // when
    CreatePromotionUserInfo result = promotionUserService.createPromotionUser(command);

    // then
    assertThat(result.userId()).isEqualTo(command.userId());

    verify(promotionUserRepository).save(any(PromotionUser.class));
  }

  @DisplayName("promotionUserUuid로 프로모션 유저 정보를 수정한다.")
  @Test
  void update_promotion_user_success() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    String promotionUserUuid = UUID.randomUUID().toString();
    Long userId = 1L;

    UpdatePromotionUserCommand command = new UpdatePromotionUserCommand(userId, promotionUuid);

    PromotionUser promotionUser = PromotionUser.of(2L, promotionUuid);
    ReflectionTestUtils.setField(promotionUser, "promotionUserUuid", promotionUserUuid);

    when(promotionUserRepository.findByPromotionUserUuidAndDeletedAtIsNull(promotionUserUuid))
        .thenReturn(Optional.of(promotionUser));

    // when
    UpdatePromotionUserInfo result = promotionUserService.updatePromotionUser(
        command, promotionUserUuid);

    // then
    assertThat(result.promotionUserUuid()).isEqualTo(promotionUserUuid);
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.promotionUuid()).isEqualTo(promotionUuid);

    verify(promotionUserRepository).findByPromotionUserUuidAndDeletedAtIsNull(promotionUserUuid);
  }

  @DisplayName("존재하지 않는 promotionUserUuid로 수정 시 예외가 발생한다.")
  @Test
  void update_promotion_user_invalid_uuid_exception() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    String promotionUserUuid = UUID.randomUUID().toString();
    UpdatePromotionUserCommand command = new UpdatePromotionUserCommand(1L,promotionUuid);

    when(promotionUserRepository.findByPromotionUserUuidAndDeletedAtIsNull(promotionUserUuid))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        promotionUserService.updatePromotionUser(command, promotionUserUuid)
    ).isInstanceOf(CustomException.class)
        .hasMessage(PromotionUserErrorCode.INVALID_PROMOTION_USER_UUID.getMessage());

    verify(promotionUserRepository).findByPromotionUserUuidAndDeletedAtIsNull(promotionUserUuid);
  }

  @DisplayName("유저 아이디와 프로모션 아이디로 프로모션에 참여한 유저 검색 서비스 테스트")
  @Test
  void promotion_user_search_service_test() {
    // given
    String promotionUuid = UUID.randomUUID().toString();

    SearchPromotionUserCommand command = new SearchPromotionUserCommand(
        1L,
        promotionUuid,
        true,
        "createdAt",
        0,
        10
    );

    PromotionUserSearchCriteriaQuery query1 = new PromotionUserSearchCriteriaQuery(
        "promotion-user-uuid-1",  promotionUuid, 1L);
    PromotionUserSearchCriteriaQuery query2 = new PromotionUserSearchCriteriaQuery(
        "promotion-user-uuid-2", promotionUuid,1L);

    PaginatedResult<PromotionUserSearchCriteriaQuery> paginatedResult =
        new PaginatedResult<>(
            List.of(query1, query2),
            0,
            10,
            2L,
            1
        );

    when(promotionUserRepository.searchPromotionUser(command.toCriteria()))
        .thenReturn(paginatedResult);

    // when
    PaginatedResultCommand<SearchPromotionUserInfo> result =
        promotionUserService.searchPromotionUser(command);

    // then
    assertThat(result.content()).hasSize(2);
    assertThat(result.content().get(0).promotionUserUuid()).isEqualTo("promotion-user-uuid-1");
    assertThat(result.page()).isEqualTo(0);
    assertThat(result.size()).isEqualTo(10);
    assertThat(result.totalElements()).isEqualTo(2L);
    assertThat(result.totalPages()).isEqualTo(1);

    verify(promotionUserRepository).searchPromotionUser(any());
  }


}