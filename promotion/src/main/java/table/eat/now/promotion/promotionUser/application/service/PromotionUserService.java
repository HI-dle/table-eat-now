package table.eat.now.promotion.promotionUser.application.service;

import table.eat.now.promotion.promotionUser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.request.UpdatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.dto.response.UpdatePromotionUserInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionUserService {

  CreatePromotionUserInfo createPromotionUser(CreatePromotionUserCommand command);

  UpdatePromotionUserInfo updatePromotionUser(UpdatePromotionUserCommand com, String promotionUserUuid);
}
