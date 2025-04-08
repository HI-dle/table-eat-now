package table.eat.now.promotion.promotionUser.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import table.eat.now.promotion.promotionUser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionUser.domain.repository.PromotionUserRepository;

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
    CreatePromotionUserCommand command = new CreatePromotionUserCommand(1L);

    PromotionUser entity = command.toEntity();

    when(promotionUserRepository.save(any(PromotionUser.class)))
        .thenReturn(entity);

    // when
    CreatePromotionUserInfo result = promotionUserService.createPromotionUser(command);

    // then
    assertThat(result.userId()).isEqualTo(command.userId());

    verify(promotionUserRepository).save(any(PromotionUser.class));
  }

}