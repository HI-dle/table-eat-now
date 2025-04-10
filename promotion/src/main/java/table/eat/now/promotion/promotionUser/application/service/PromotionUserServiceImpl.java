package table.eat.now.promotion.promotionUser.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.common.exception.CustomException;
import table.eat.now.promotion.promotionUser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.request.UpdatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.dto.response.UpdatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.exception.PromotionUserErrorCode;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;
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

  @Override
  public UpdatePromotionUserInfo updatePromotionUser(UpdatePromotionUserCommand command,
      String promotionUserUuid) {
    PromotionUser promotionUser = findByPromotionUser(promotionUserUuid);
    promotionUser.modifyPromotionUser(command.userId());
    return UpdatePromotionUserInfo.from(promotionUser);
  }

  public PromotionUser findByPromotionUser(String promotionUserUuid) {
    return promotionUserRepository.findByPromotionUserUuidAndDeletedAtIsNull(
        promotionUserUuid).orElseThrow(() ->
        CustomException.from(PromotionUserErrorCode.INVALID_PROMOTION_USER_UUID));
  }
}
