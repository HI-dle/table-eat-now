package table.eat.now.promotion.promotionrestaurant.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotionrestaurant.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionrestaurant.application.dto.excepton.PromotionRestaurantErrorCode;
import table.eat.now.promotion.promotionrestaurant.application.dto.request.CreatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionrestaurant.application.dto.request.SearchPromotionRestaurantCommand;
import table.eat.now.promotion.promotionrestaurant.application.dto.request.UpdatePromotionRestaurantCommand;
import table.eat.now.promotion.promotionrestaurant.application.dto.response.CreatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionrestaurant.application.dto.response.GetPromotionRestaurantInfo;
import table.eat.now.promotion.promotionrestaurant.application.dto.response.SearchPromotionRestaurantInfo;
import table.eat.now.promotion.promotionrestaurant.application.dto.response.UpdatePromotionRestaurantInfo;
import table.eat.now.promotion.promotionrestaurant.domain.entity.PromotionRestaurant;
import table.eat.now.promotion.promotionrestaurant.domain.repository.PromotionRestaurantRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionRestaurantServiceImpl implements PromotionRestaurantService{

  private final PromotionRestaurantRepository promotionRestaurantRepository;

  @Override
  @Transactional
  public CreatePromotionRestaurantInfo createPromotionRestaurant(
      CreatePromotionRestaurantCommand command) {
    return CreatePromotionRestaurantInfo
        .from(promotionRestaurantRepository.save(command.toEntity()));
  }

  @Override
  @Transactional
  public UpdatePromotionRestaurantInfo updatePromotionRestaurant(
      UpdatePromotionRestaurantCommand command, String promotionRestaurantUuid) {
    PromotionRestaurant promotionRestaurant = findByPromotionRestaurant(promotionRestaurantUuid);
    promotionRestaurant.modifyPromotionRestaurant(
        command.promotionUuid(),
        command.restaurantUuid());
    return UpdatePromotionRestaurantInfo.from(promotionRestaurant);
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedResultCommand<SearchPromotionRestaurantInfo> searchPromotionRestaurant(
      SearchPromotionRestaurantCommand command) {
    return PaginatedResultCommand.from(
        promotionRestaurantRepository.searchPromotionRestaurant(command.toCriteria()));
  }

  @Override
  @Transactional
  public void deletePromotionRestaurant(String restaurantUuid, CurrentUserInfoDto userInfoDto) {
    PromotionRestaurant promotionRestaurant =
        findByPromotionRestaurantFromRestaurantId(restaurantUuid);
    promotionRestaurant.delete(userInfoDto.userId());
    log.info("삭제가 완료 되었습니다. 삭제한 userId: {}", promotionRestaurant);
  }

  @Override
  @Transactional(readOnly = true)
  public GetPromotionRestaurantInfo findRestaurantsByPromotions(
      String restaurantUuid,
      String promotionUuid) {
    return GetPromotionRestaurantInfo.from(
        findByRestaurantUuidAndPromotionUuidAndDeletedAtIsNull(restaurantUuid,promotionUuid));
  }

  private PromotionRestaurant findByPromotionRestaurant(String promotionRestaurantUuid) {
    return promotionRestaurantRepository.findByPromotionRestaurantUuidAndDeletedAtIsNull(
        promotionRestaurantUuid).orElseThrow(() ->
        CustomException.from(PromotionRestaurantErrorCode.INVALID_PROMOTION_RESTAURANT_UUID));
  }
  private PromotionRestaurant findByPromotionRestaurantFromRestaurantId(String restaurantUuid) {
    return promotionRestaurantRepository.findByRestaurantUuidAndDeletedAtIsNull(restaurantUuid)
        .orElseThrow(() ->
            CustomException.from(PromotionRestaurantErrorCode.INVALID_PROMOTION_RESTAURANT_UUID));
  }
  private PromotionRestaurant findByRestaurantUuidAndPromotionUuidAndDeletedAtIsNull(
      String restaurantUuid, String promotionUuid) {
    return promotionRestaurantRepository.findByRestaurantUuidAndPromotionUuidAndDeletedAtIsNull(
        restaurantUuid, promotionUuid)
        .orElseThrow(() ->
            CustomException.from(PromotionRestaurantErrorCode.INVALID_PROMOTION_RESTAURANT_UUID));
  }



}
