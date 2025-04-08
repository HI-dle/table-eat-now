package table.eat.now.promotion.promotionUser.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.promotion.promotionUser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.domain.repository.PromotionUserRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
public class PromotionUserServiceImpl implements PromotionUserService{

  private final PromotionUserRepository promotionUserRepository;


  @Override
  public CreatePromotionUserInfo createPromotionUser(CreatePromotionUserCommand command) {
    return CreatePromotionUserInfo.from(promotionUserRepository.save(command.toEntity()));
  }
}
