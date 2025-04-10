package table.eat.now.promotion.promotion.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import table.eat.now.promotion.promotion.application.dto.request.CreatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.request.UpdatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.UpdatePromotionInfo;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;
import table.eat.now.promotion.promotion.domain.entity.PromotionType;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;

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


}