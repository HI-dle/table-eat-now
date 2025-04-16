package table.eat.now.promotion.promotionuser.application.service;

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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.promotion.promotionuser.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionuser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionuser.application.dto.request.SearchPromotionUserCommand;
import table.eat.now.promotion.promotionuser.application.dto.request.UpdatePromotionUserCommand;
import table.eat.now.promotion.promotionuser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionuser.application.dto.response.SearchPromotionUserInfo;
import table.eat.now.promotion.promotionuser.application.dto.response.UpdatePromotionUserInfo;
import table.eat.now.promotion.promotionuser.application.event.EventType;
import table.eat.now.promotion.promotionuser.application.event.dto.PromotionUserSaveEventInfo;
import table.eat.now.promotion.promotionuser.application.event.dto.PromotionUserSavePayloadInfo;
import table.eat.now.promotion.promotionuser.application.exception.PromotionUserErrorCode;
import table.eat.now.promotion.promotionuser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionuser.domain.repository.PromotionUserRepository;
import table.eat.now.promotion.promotionuser.domain.repository.search.PaginatedResult;
import table.eat.now.promotion.promotionuser.domain.repository.search.PromotionUserSearchCriteriaQuery;


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
  @DisplayName("userId로 PromotionUser를 삭제한다.")
  @Test
  void delete_promotion_user_success() {
    // given
    Long targetUserId = 999L;
    Long deleterUserId = 1L;
    CurrentUserInfoDto currentUserInfo = new CurrentUserInfoDto(deleterUserId, UserRole.MASTER);

    PromotionUser promotionUser = Mockito.mock(PromotionUser.class);

    when(promotionUserRepository.findByUserIdAndDeletedAtIsNull(targetUserId))
        .thenReturn(Optional.of(promotionUser));

    // when
    promotionUserService.deletePromotionUser(targetUserId, currentUserInfo);

    // then
    verify(promotionUserRepository).findByUserIdAndDeletedAtIsNull(targetUserId);
    verify(promotionUser).delete(deleterUserId);
  }

  @DisplayName("존재하지 않는 userId로 삭제 시 예외가 발생한다.")
  @Test
  void delete_promotion_user_invalid_userId_exception() {
    // given
    Long targetUserId = 999L;
    CurrentUserInfoDto currentUserInfo = new CurrentUserInfoDto(1L, UserRole.MASTER);

    when(promotionUserRepository.findByUserIdAndDeletedAtIsNull(targetUserId))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        promotionUserService.deletePromotionUser(targetUserId, currentUserInfo)
    ).isInstanceOf(CustomException.class)
        .hasMessage(PromotionUserErrorCode.INVALID_PROMOTION_USER_UUID.getMessage());

    verify(promotionUserRepository).findByUserIdAndDeletedAtIsNull(targetUserId);
  }


  @DisplayName("프로모션 유저를 배치 저장한다.")
  @Test
  void save_promotion_users_success() {
    // given
    CurrentUserInfoDto userInfo = new CurrentUserInfoDto(1L, UserRole.MASTER);
    List<PromotionUserSavePayloadInfo> payloads = List.of(
        new PromotionUserSavePayloadInfo(101L, "promo-uuid-1"),
        new PromotionUserSavePayloadInfo(102L, "promo-uuid-1")
    );

    PromotionUserSaveEventInfo eventInfo = new PromotionUserSaveEventInfo(
        EventType.SUCCEED,
        payloads,
        userInfo
    );

    //Mock 객체의 메서드가 어떤 인자를 받았는지 확인하고 싶을 때 사용
    ArgumentCaptor<List<PromotionUser>> captor = ArgumentCaptor.forClass(List.class);

    // when
    promotionUserService.savePromotionUsers(eventInfo);

    // then
    verify(promotionUserRepository).saveAllInBatch(captor.capture());
    List<PromotionUser> savedUsers = captor.getValue();


    assertThat(savedUsers).hasSize(2);
    assertThat(savedUsers).extracting("userId")
        .containsExactlyInAnyOrder(101L, 102L);
    assertThat(savedUsers).extracting("promotionUuid")
        .containsOnly("promo-uuid-1");
  }



}