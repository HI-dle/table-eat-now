package table.eat.now.promotion.promotion.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotion.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotion.application.dto.request.CreatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.request.SearchPromotionCommand;
import table.eat.now.promotion.promotion.application.dto.request.UpdatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.SearchPromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.UpdatePromotionInfo;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;
import table.eat.now.promotion.promotion.domain.entity.PromotionType;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionServiceImpl implements PromotionService{

  private final PromotionRepository promotionRepository;

  @Override
  @Transactional
  public CreatePromotionInfo createPromotion(CreatePromotionCommand command) {
    return CreatePromotionInfo.from(promotionRepository.save(command.toEntity()));
  }

  @Override
  @Transactional
  public UpdatePromotionInfo updatePromotion(UpdatePromotionCommand command, String promotionUuid) {
    Promotion promotion = findByPromotion(promotionUuid);
    promotion.modifyPromotion(
        command.promotionName(),
        command.description(),
        command.startTime(),
        command.endTime(),
        command.discountAmount(),
        PromotionStatus.valueOf(command.promotionStatus()),
        PromotionType.valueOf(command.promotionType())
    );
    return UpdatePromotionInfo.from(promotion);
  }

  @Override
  @Transactional(readOnly = true)
  public GetPromotionInfo findPromotion(String promotionUuid) {
    return GetPromotionInfo.from(findByPromotion(promotionUuid));
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedResultCommand<SearchPromotionInfo> searchPromotion(
      SearchPromotionCommand command) {
    return PaginatedResultCommand.from(
        promotionRepository.searchPromotion(command.toCriteria()));
  }

  @Override
  @Transactional
  public void deletePromotion(String promotionUuid, CurrentUserInfoDto userInfoDto) {
    Promotion promotion = findByPromotion(promotionUuid);
    promotion.delete(userInfoDto.userId());
    log.info("삭제가 완료 되었습니다. 삭제한 userId: {}", promotion);
  }

  private Promotion findByPromotion(String promotionUuid) {
    return promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid)
        .orElseThrow(() ->
            CustomException.from(PromotionErrorCode.INVALID_PROMOTION_UUID));
  }

}
