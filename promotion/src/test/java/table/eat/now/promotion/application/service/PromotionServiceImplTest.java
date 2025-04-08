package table.eat.now.promotion.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import table.eat.now.promotion.application.dto.request.CreatePromotionCommand;
import table.eat.now.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.domain.entity.Promotion;
import table.eat.now.promotion.domain.entity.repository.PromotionRepository;

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

}