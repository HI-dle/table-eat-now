package table.eat.now.promotion.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.promotion.application.dto.request.CreatePromotionCommand;
import table.eat.now.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.domain.entity.repository.PromotionRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService{

  private final PromotionRepository promotionRepository;

  @Override
  public CreatePromotionInfo createPromotion(CreatePromotionCommand application) {
    return CreatePromotionInfo.from(promotionRepository.save(application.toEntity()));
  }
}
