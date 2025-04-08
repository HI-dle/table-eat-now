package table.eat.now.promotion.promotionUser.application.service;

import table.eat.now.promotion.promotionUser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionUserService {

  CreatePromotionUserInfo createPromotionUser(CreatePromotionUserCommand command);
}
