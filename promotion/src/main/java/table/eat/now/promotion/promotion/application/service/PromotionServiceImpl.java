package table.eat.now.promotion.promotion.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.promotion.promotion.application.dto.request.CreatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService{

  private final PromotionRepository promotionRepository;

  @Override
  @Transactional
  public CreatePromotionInfo createPromotion(CreatePromotionCommand application) {
    return CreatePromotionInfo.from(promotionRepository.save(application.toEntity()));
  }
}
