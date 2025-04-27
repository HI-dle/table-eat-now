package table.eat.now.promotion.promotion.application.service;

import static table.eat.now.promotion.promotion.infrastructure.metric.PromotionMetricName.PROMOTION_PARTICIPATION_FAIL;
import static table.eat.now.promotion.promotion.infrastructure.metric.PromotionMetricName.PROMOTION_PARTICIPATION_SUCCESS;

import io.micrometer.core.instrument.MeterRegistry;
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
  private final MeterRegistry meterRegistry;

  @Override
  @Transactional
  public CreatePromotionInfo createPromotion(CreatePromotionCommand command) {
    Promotion savedPromotion = promotionRepository.save(command.toEntity());
    CreatePromotionInfo createPromotionInfo = CreatePromotionInfo.from(savedPromotion);

    schedulePromotion(savedPromotion);

    return createPromotionInfo;
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
        PromotionType.valueOf(command.promotionType()),
        command.maxParticipant()
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
    Promotion promotion = findByPromotion(info.promotionUuid());
    validPromotionStatus(promotion);

    ParticipateResult participateResult = promotionRepository.addUserToPromotion(
        info.toDomain(), promotion.getMaxParticipant().getMaxParticipantsValue());

    return participateProcess(info, participateResult);
  }

  private boolean participateProcess(ParticipatePromotionUserInfo info,
      ParticipateResult participateResult) {
    if (participateResult == ParticipateResult.FAIL) {

      meterRegistry.counter(PROMOTION_PARTICIPATION_FAIL).increment();

      log.info("참여 실패");
      return false;
    }

    if (participateResult == ParticipateResult.DUPLICATION) {

      meterRegistry.counter(PROMOTION_PARTICIPATION_FAIL).increment();

      log.info("중복 참여");
      return false;
    }

    if (participateResult == ParticipateResult.SUCCESS_SEND_BATCH) {
      List<PromotionUserSavePayload> savePayloadList = promotionRepository.getPromotionUsers(
              info.promotionName()).stream()
          .map(PromotionUserSavePayload::from)
          .toList();

      PromotionUserSaveEvent promotionUserSaveEvent = PromotionUserSaveEvent.of(
          savePayloadList, createCurrentUserInfoDto());

      log.info("배치 실행 {}", savePayloadList.size());

      promotionEventPublisher.publish(promotionUserSaveEvent);
    }

    Promotion promotion = findByPromotion(info.promotionUuid());

    promotionEventPublisher.publish(PromotionUserCouponSaveEvent.of(
        info, promotion, createCurrentUserInfoDto()));

    meterRegistry.counter(PROMOTION_PARTICIPATION_SUCCESS).increment();


    log.info("참여 성공");
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

  private void schedulePromotion(Promotion promotion) {
    promotionRepository.addScheduleQueue(promotion);
  }

  private void validPromotionStatus(Promotion promotion) {
    if (!PromotionStatus.RUNNING.equals(promotion.getPromotionStatus())) {
      throw CustomException.from(PromotionErrorCode.NOT_RUNNING_PROMOTION);
    }
  }

}
