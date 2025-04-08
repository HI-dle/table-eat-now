package table.eat.now.promotion.application.service;

import table.eat.now.promotion.application.dto.request.CreatePromotionCommand;
import table.eat.now.promotion.application.dto.response.CreatePromotionInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionService {

  CreatePromotionInfo createPromotion(CreatePromotionCommand application);
}
