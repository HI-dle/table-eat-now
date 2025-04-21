package table.eat.now.promotion.promotion.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.promotion.promotion.application.client.PromotionClient;
import table.eat.now.promotion.promotion.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotion.application.dto.client.response.GetPromotionRestaurantInfo;
import table.eat.now.promotion.promotion.application.dto.request.CreatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.request.GetPromotionsFeignCommand;
import table.eat.now.promotion.promotion.application.dto.request.ParticipatePromotionUserInfo;
import table.eat.now.promotion.promotion.application.dto.request.SearchPromotionCommand;
import table.eat.now.promotion.promotion.application.dto.request.UpdatePromotionCommand;
import table.eat.now.promotion.promotion.application.dto.response.CreatePromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.GetPromotionsClientInfo;
import table.eat.now.promotion.promotion.application.dto.response.SearchPromotionInfo;
import table.eat.now.promotion.promotion.application.dto.response.UpdatePromotionInfo;
import table.eat.now.promotion.promotion.application.event.PromotionEventPublisher;
import table.eat.now.promotion.promotion.application.event.produce.PromotionUserCouponSaveEvent;
import table.eat.now.promotion.promotion.application.event.produce.PromotionUserSaveEvent;
import table.eat.now.promotion.promotion.application.event.produce.PromotionUserSavePayload;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.application.service.util.MaxParticipate;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;
import table.eat.now.promotion.promotion.domain.entity.PromotionType;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;
import table.eat.now.promotion.promotion.domain.entity.repository.event.ParticipateResult;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionServiceImpl implements PromotionService{

  private final PromotionRepository promotionRepository;
  private final PromotionClient promotionClient;
  private final PromotionEventPublisher promotionEventPublisher;

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
        command.couponUuid(),
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

    deleteCheckPromotionStatus(promotion.getPromotionStatus());

    promotion.delete(userInfoDto.userId());
    log.info("삭제가 완료 되었습니다. 삭제한 userId: {}", promotion);
  }

  @Override
  @Transactional(readOnly = true)
  public GetPromotionsClientInfo reservationGetPromotions(GetPromotionsFeignCommand command) {


    List<GetPromotionRestaurantInfo> promotionRestaurantResList = command.promotionUuids().stream()
        .map(promotionUuid ->
            promotionClient.findRestaurantsByPromotions(
                command.restaurantUuid(),
                promotionUuid
            )
        )
        .toList();

    List<Promotion> promotions = promotionRepository.
        findAllByPromotionUuidInAndDeletedByIsNull(command.promotionUuids());

    return GetPromotionsClientInfo.from(promotionRestaurantResList, promotions);
  }

  @Override
  @Transactional
  public boolean participate(ParticipatePromotionUserInfo info) {
    // Redis에 참여 시도
    ParticipateResult participateResult = promotionRepository.addUserToPromotion(
        info.toDomain(), MaxParticipate.PARTICIPATE_10000_MAX);

    return participateProcess(info, participateResult);
  }
  //테스트용
//
//  @Override
//  public void test(ParticipatePromotionUserInfo info) {
//    for (long userId = 1; userId <= 2001; userId++) {
//      ParticipatePromotionUserInfo loopInfo = ParticipatePromotionUserInfo.builder()
//          .userId(userId)
//          .promotionUuid(info.promotionUuid())
//          .promotionName(info.promotionName())
//          .build();
//
//      ParticipateResult participateResult = promotionRepository.addUserToPromotion(
//          loopInfo.toDomain(), MaxParticipate.PARTICIPATE_10000_MAX);
//
//      participateProcess(loopInfo, participateResult);
//    }
//  }

  private boolean participateProcess(ParticipatePromotionUserInfo info,
      ParticipateResult participateResult) {
    if (participateResult == ParticipateResult.FAIL) {
      return false;
    }

    if (participateResult == ParticipateResult.SUCCESS_SEND_BATCH) {
      List<PromotionUserSavePayload> savePayloadList = promotionRepository.getPromotionUsers(
              info.promotionName()).stream()
          .map(PromotionUserSavePayload::from)
          .toList();

      PromotionUserSaveEvent promotionUserSaveEvent = PromotionUserSaveEvent.of(
          savePayloadList, createCurrentUserInfoDto());

      promotionEventPublisher.publish(promotionUserSaveEvent);

      Promotion promotion = findByPromotion(info.promotionUuid());

      promotionEventPublisher.publish(PromotionUserCouponSaveEvent.of(
          promotionUserSaveEvent, promotion.getCouponUuid()));
    }
    return true;
  }


  //이 부분 너무 고민입니다...PromotionUser로 보낼 때 auditing에 사용될 CurrentUserInfoDto가
  //필요할 것 같은데 이미 프로모션 진행 중에 자동으로 저장되게끔 구성을 해서 CurrentUserInfoDto를
  // controller에서 받아오기엔 1000번째 유저의 아이디가 들어가 버리는 탓에 아래 메서드에서 관리자 값을 하나 두고
  // 관리자 값을 넣어줄까 싶어서 구성해 보았습니다. 좋은 방안 있으면 알려주세요!
  private CurrentUserInfoDto createCurrentUserInfoDto() {
    return CurrentUserInfoDto.of(1L, UserRole.MASTER);
  }

  private void deleteCheckPromotionStatus(PromotionStatus status) {
    if (status == PromotionStatus.RUNNING) {
      throw CustomException.from(PromotionErrorCode.CANNOT_DELETE_RUNNING_PROMOTION);
    }
  }

  private Promotion findByPromotion(String promotionUuid) {
    return promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid)
        .orElseThrow(() ->
            CustomException.from(PromotionErrorCode.INVALID_PROMOTION_UUID));
  }

}
