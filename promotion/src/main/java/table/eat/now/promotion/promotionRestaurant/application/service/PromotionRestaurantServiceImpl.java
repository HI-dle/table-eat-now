package table.eat.now.promotion.promotionRestaurant.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.promotion.promotionRestaurant.application.dto.request.CreatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionRestaurant.application.dto.response.CreatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionRestaurant.domain.repository.PromotionRestaurantRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
public class PromotionRestaurantServiceImpl implements PromotionRestaurantService{

  private final PromotionRestaurantRepository promotionRestaurantRepository;

  @Override
  @Transactional
  public CreatePromotionRestaurantInfo createPromotionRestaurant(
      CreatePromotionRestaurantCommand command) {
    return CreatePromotionRestaurantInfo
        .from(promotionRestaurantRepository.save(command.toEntity()));
  }
}
