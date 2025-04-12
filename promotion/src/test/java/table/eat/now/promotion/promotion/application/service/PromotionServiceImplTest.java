package table.eat.now.promotion.promotion.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.promotion.promotion.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotion.application.dto.request.CreatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.request.SearchPromotionCommand;
import table.eat.now.promotion.promotion.application.dto.request.UpdatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.SearchPromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.UpdatePromotionInfo;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;
import table.eat.now.promotion.promotion.domain.entity.PromotionType;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PaginatedResult;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteria;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

  @Mock
  private PromotionRepository promotionRepository;

  @InjectMocks
  private PromotionServiceImpl promotionService;

  @DisplayName("프로모션 서비스 단위 테스트")
  @Test
  void promotion_crate_service_test() {
      // given
    CreatePromotionCommand command = new CreatePromotionCommand(
        "봄맞이 할인 프로모션",
        "전 메뉴 3000원 할인",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(10),
        BigDecimal.valueOf(3000),
        "READY",
        "COUPON"
    );
    Promotion promotion = command.toEntity();

    when(promotionRepository.save(any(Promotion.class)))
        .thenReturn(promotion);

    // when
    CreatePromotionInfo result = promotionService.createPromotion(command);

    // then
    assertThat(result.promotionName()).isEqualTo(command.promotionName());
    assertThat(result.discountAmount()).isEqualTo(command.discountAmount());

    verify(promotionRepository).save(any(Promotion.class));
  }
  @DisplayName("promotionUuid로 프로모션 내용을 수정한다.")
  @Test
  void promotion_uuid_update_service_test() {
      // given

    String promotionUuid = UUID.randomUUID().toString();

    UpdatePromotionCommand command = new UpdatePromotionCommand(
        "봄맞이 할인 프로모션 - 수정 후",
        "전 메뉴 5000원 할인",
        LocalDateTime.now().plusDays(2),
        LocalDateTime.now().plusDays(12),
        BigDecimal.valueOf(5000),
        "READY",
        "COUPON"
    );
    Promotion promotion = Promotion.of(
        "봄맞이 할인 프로모션 - 수정 전",
        "전 메뉴 5000원 할인",
        LocalDateTime.now().plusDays(2),
        LocalDateTime.now().plusDays(12),
        BigDecimal.valueOf(5000),
        PromotionStatus.valueOf("READY"),
        PromotionType.valueOf("COUPON")
    );

    promotion.modifyPromotion( command.promotionName(),
        command.description(),
        command.startTime(),
        command.endTime(),
        command.discountAmount(),
        PromotionStatus.valueOf(command.promotionStatus()),
        PromotionType.valueOf(command.promotionType()));
    ReflectionTestUtils.setField(promotion, "promotionUuid", promotionUuid);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(promotion));

    // when
    UpdatePromotionInfo result = promotionService.updatePromotion(command, promotionUuid);

    // then
    assertThat(result.promotionName()).isEqualTo("봄맞이 할인 프로모션 - 수정 후");
    assertThat(result.discountAmount()).isEqualTo(BigDecimal.valueOf(5000));

    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
  }
  @DisplayName("존재하지 않는 promotionUuid로 수정 시 예외가 발생한다.")
  @Test
  void promotion_uuid_update_fail_test() {
    // given
    String invalidUuid = UUID.randomUUID().toString();

    UpdatePromotionCommand command = new UpdatePromotionCommand(
        "잘못된 수정",
        "내용 없음",
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(1),
        BigDecimal.valueOf(1000),
        "READY",
        "COUPON"
    );

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(invalidUuid))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> promotionService.updatePromotion(command, invalidUuid))
        .isInstanceOf(CustomException.class);
  }

  @DisplayName("promotionUuid로 프로모션을 조회하면 성공한다.")
  @Test
  void promotion_uuid_find_success_test() {
    // given
    String promotionUuid = UUID.randomUUID().toString();

    Promotion promotion = Promotion.of(
        "봄맞이 할인",
        "전 메뉴 5000원 할인",
        LocalDateTime.now().plusDays(2),
        LocalDateTime.now().plusDays(12),
        BigDecimal.valueOf(5000),
        PromotionStatus.valueOf("READY"),
        PromotionType.valueOf("COUPON")
    );

    ReflectionTestUtils.setField(promotion, "promotionUuid", promotionUuid);
    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(promotion));

    // when
    GetPromotionInfo result = promotionService.findPromotion(promotionUuid);

    // then
    assertThat(result.promotionUuid()).isEqualTo(promotionUuid);
    assertThat(result.promotionName()).isEqualTo("봄맞이 할인");
    assertThat(result.discountAmount()).isEqualTo(BigDecimal.valueOf(5000));

    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
  }


  @DisplayName("존재하지 않는 promotionUuid로 조회 시 예외가 발생한다.")
  @Test
  void promotion_uuid_find_fail_test() {
    // given
    String invalidUuid = UUID.randomUUID().toString();

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(invalidUuid))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> promotionService.findPromotion(invalidUuid))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(PromotionErrorCode.INVALID_PROMOTION_UUID.getMessage());
  }

  @DisplayName("프로모션 검색 시 페이징된 결과를 반환한다.")
  @Test
  void search_promotion_success_test() {
    // given
    SearchPromotionCommand command = new SearchPromotionCommand(
        "할인",
        "한정",
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(3),
        BigDecimal.valueOf(1000),
        "READY",
        "COUPON",
        true,
        "startTime",
        0,
        10
    );

    PromotionSearchCriteria criteria = command.toCriteria();

    PromotionSearchCriteriaQuery result1 = new PromotionSearchCriteriaQuery(
        1L, UUID.randomUUID().toString(),
        "봄맞이 할인",
        "봄 시즌 한정 할인",
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(3),
        BigDecimal.valueOf(1000),
        "READY",
        "COUPON"
    );

    PromotionSearchCriteriaQuery result2 = new PromotionSearchCriteriaQuery(
        2L, UUID.randomUUID().toString(),
        "여름맞이 이벤트",
        "여름 시즌 한정 할인",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(4),
        BigDecimal.valueOf(2000),
        "READY",
        "RESTAURANT"
    );

    PaginatedResult<PromotionSearchCriteriaQuery> paginatedResult =
        new PaginatedResult<>(
            List.of(result1, result2),
            0, 10, 2L, 1
        );

    when(promotionRepository.searchPromotion(criteria)).thenReturn(paginatedResult);

    // when
    PaginatedResultCommand<SearchPromotionInfo> result = promotionService.searchPromotion(command);

    // then
    assertThat(result.page()).isEqualTo(0);
    assertThat(result.size()).isEqualTo(10);
    assertThat(result.totalElements()).isEqualTo(2L);
    assertThat(result.totalPages()).isEqualTo(1);
    assertThat(result.content()).hasSize(2);
    assertThat(result.content().get(0).promotionName()).isEqualTo("봄맞이 할인");

    verify(promotionRepository).searchPromotion(criteria);
  }

  @DisplayName("promotionUuid로 생성된 프로모션을 삭제한다.")
  @Test
  void delete_promotion_success() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    Long deleterUserId = 1L;
    CurrentUserInfoDto currentUserInfo = new CurrentUserInfoDto(deleterUserId, UserRole.MASTER);

    Promotion promotion = Mockito.mock(Promotion.class);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(promotion));

    // when
    promotionService.deletePromotion(promotionUuid, currentUserInfo);

    // then
    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
    verify(promotion).delete(deleterUserId);
  }

  @DisplayName("존재하지 않는 promotionUuid로 프로모션을 삭제 시 예외가 발생한다.")
  @Test
  void delete_promotion_invalid_uuid_exception() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    CurrentUserInfoDto currentUserInfo = new CurrentUserInfoDto(1L, UserRole.MASTER);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        promotionService.deletePromotion(promotionUuid, currentUserInfo)
    ).isInstanceOf(CustomException.class)
        .hasMessage(PromotionErrorCode.INVALID_PROMOTION_UUID.getMessage());

    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
  }
  @DisplayName("진행 중인 프로모션은 삭제할 수 없다.")
  @Test
  void delete_promotion_running_status_exception() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    CurrentUserInfoDto currentUserInfo = new CurrentUserInfoDto(1L, UserRole.MASTER);

    Promotion promotion = Mockito.mock(Promotion.class);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(promotion));
    // 진행 중인 상태
    when(promotion.getPromotionStatus()).thenReturn(PromotionStatus.RUNNING);

    // when & then
    assertThatThrownBy(() ->
        promotionService.deletePromotion(promotionUuid, currentUserInfo)
    ).isInstanceOf(CustomException.class)
        .hasMessage(PromotionErrorCode.CANNOT_DELETE_RUNNING_PROMOTION.getMessage());

    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
    verify(promotion).getPromotionStatus();
    verify(promotion, never()).delete(any());
  }



}