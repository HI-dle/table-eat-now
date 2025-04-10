package table.eat.now.promotion.promotion.application.service;

import table.eat.now.promotion.promotion.application.dto.request.CreatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.request.UpdatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.UpdatePromotionInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionService {

  CreatePromotionInfo createPromotion(CreatePromotionCommand command);
  UpdatePromotionInfo updatePromotion(UpdatePromotionCommand command, String promotionUuid);
}
