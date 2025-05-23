package table.eat.now.promotion.promotion.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static table.eat.now.promotion.promotion.infrastructure.metric.PromotionMetricName.PROMOTION_PARTICIPATION_FAIL;
import static table.eat.now.promotion.promotion.infrastructure.metric.PromotionMetricName.PROMOTION_PARTICIPATION_SUCCESS;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
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
import table.eat.now.promotion.promotion.application.event.PromotionEvent;
import table.eat.now.promotion.promotion.application.event.PromotionEventPublisher;
import table.eat.now.promotion.promotion.application.event.produce.PromotionUserSaveEvent;
import table.eat.now.promotion.promotion.application.exception.PromotionErrorCode;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.PromotionStatus;
import table.eat.now.promotion.promotion.domain.entity.PromotionType;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;
import table.eat.now.promotion.promotion.domain.entity.repository.event.ParticipateResult;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipant;
import table.eat.now.promotion.promotion.domain.entity.repository.event.PromotionParticipantDto;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PaginatedResult;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteria;
import table.eat.now.promotion.promotion.domain.entity.repository.search.PromotionSearchCriteriaQuery;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

  @Mock
  private PromotionRepository promotionRepository;

  @Mock
  private PromotionClient promotionClient;

  @Mock
  private MeterRegistry meterRegistry;

  @Mock
  private Counter counter;
  @Mock
  private PromotionEventPublisher promotionEventPublisher;

  @InjectMocks
  private PromotionServiceImpl promotionService;

  @DisplayName("프로모션 서비스 단위 테스트")
  @Test
  void promotion_crate_service_test() {
    String couponUuid = UUID.randomUUID().toString();
    // given
    CreatePromotionCommand command = new CreatePromotionCommand(
        couponUuid,
        "봄맞이 할인 프로모션",
        "전 메뉴 3000원 할인",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(10),
        BigDecimal.valueOf(3000),
        "READY",
        "COUPON",
        1000
    );
    Promotion promotion = command.toEntity();

    when(promotionRepository.save(any(Promotion.class)))
        .thenReturn(promotion);

    // when
    CreatePromotionInfo result = promotionService.createPromotion(command);

    // then
    assertThat(result.promotionName()).isEqualTo(command.promotionName());
    assertThat(result.discountAmount()).isEqualTo(command.discountAmount());

    verify(promotionRepository).save(any(Promotion.class));
  }

  @DisplayName("promotionUuid로 프로모션 내용을 수정한다.")
  @Test
  void promotion_uuid_update_service_test() {
    // given

    String promotionUuid = UUID.randomUUID().toString();
    String couponUuid = UUID.randomUUID().toString();

    UpdatePromotionCommand command = new UpdatePromotionCommand(
        couponUuid,
        "봄맞이 할인 프로모션 - 수정 후",
        "전 메뉴 5000원 할인",
        LocalDateTime.now().plusDays(2),
        LocalDateTime.now().plusDays(12),
        BigDecimal.valueOf(5000),
        "READY",
        "COUPON",
        1000
    );
    Promotion promotion = Promotion.of(
        couponUuid,
        "봄맞이 할인 프로모션 - 수정 전",
        "전 메뉴 5000원 할인",
        LocalDateTime.now().plusDays(2),
        LocalDateTime.now().plusDays(12),
        BigDecimal.valueOf(5000),
        PromotionStatus.valueOf("READY"),
        PromotionType.valueOf("COUPON"),
        1000
    );

    promotion.modifyPromotion(
        command.couponUuid(),
        command.promotionName(),
        command.description(),
        command.startTime(),
        command.endTime(),
        command.discountAmount(),
        PromotionStatus.valueOf(command.promotionStatus()),
        PromotionType.valueOf(command.promotionType()),
        1000);
    ReflectionTestUtils.setField(promotion, "promotionUuid", promotionUuid);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(promotion));

    // when
    UpdatePromotionInfo result = promotionService.updatePromotion(command, promotionUuid);

    // then
    assertThat(result.promotionName()).isEqualTo("봄맞이 할인 프로모션 - 수정 후");
    assertThat(result.discountAmount()).isEqualTo(BigDecimal.valueOf(5000));

    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
  }

  @DisplayName("존재하지 않는 promotionUuid로 수정 시 예외가 발생한다.")
  @Test
  void promotion_uuid_update_fail_test() {
    // given
    String invalidUuid = UUID.randomUUID().toString();
    String couponUuid = UUID.randomUUID().toString();

    UpdatePromotionCommand command = new UpdatePromotionCommand(
        couponUuid,
        "잘못된 수정",
        "내용 없음",
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(1),
        BigDecimal.valueOf(1000),
        "READY",
        "COUPON",
        1000
    );

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(invalidUuid))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> promotionService.updatePromotion(command, invalidUuid))
        .isInstanceOf(CustomException.class);
  }

  @DisplayName("promotionUuid로 프로모션을 조회하면 성공한다.")
  @Test
  void promotion_uuid_find_success_test() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    String couponUuid = UUID.randomUUID().toString();

    Promotion promotion = Promotion.of(
        couponUuid,
        "봄맞이 할인",
        "전 메뉴 5000원 할인",
        LocalDateTime.now().plusDays(2),
        LocalDateTime.now().plusDays(12),
        BigDecimal.valueOf(5000),
        PromotionStatus.valueOf("READY"),
        PromotionType.valueOf("COUPON"),
        1000
    );

    ReflectionTestUtils.setField(promotion, "promotionUuid", promotionUuid);
    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(promotion));

    // when
    GetPromotionInfo result = promotionService.findPromotion(promotionUuid);

    // then
    assertThat(result.promotionUuid()).isEqualTo(promotionUuid);
    assertThat(result.promotionName()).isEqualTo("봄맞이 할인");
    assertThat(result.discountAmount()).isEqualTo(BigDecimal.valueOf(5000));

    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
  }


  @DisplayName("존재하지 않는 promotionUuid로 조회 시 예외가 발생한다.")
  @Test
  void promotion_uuid_find_fail_test() {
    // given
    String invalidUuid = UUID.randomUUID().toString();

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(invalidUuid))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> promotionService.findPromotion(invalidUuid))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(PromotionErrorCode.INVALID_PROMOTION_UUID.getMessage());
  }

  @DisplayName("프로모션 검색 시 페이징된 결과를 반환한다.")
  @Test
  void search_promotion_success_test() {
    String couponUuid = UUID.randomUUID().toString();
    // given
    SearchPromotionCommand command = new SearchPromotionCommand(
        "할인",
        "한정",
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(3),
        BigDecimal.valueOf(1000),
        "READY",
        "COUPON",
        true,
        "startTime",
        0,
        10
    );

    PromotionSearchCriteria criteria = command.toCriteria();

    PromotionSearchCriteriaQuery result1 = new PromotionSearchCriteriaQuery(

        1L, UUID.randomUUID().toString(),
        couponUuid,
        "봄맞이 할인",
        "봄 시즌 한정 할인",
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(3),
        BigDecimal.valueOf(1000),
        "READY",
        "COUPON",
        1000
    );

    PromotionSearchCriteriaQuery result2 = new PromotionSearchCriteriaQuery(
        2L, UUID.randomUUID().toString(),
        couponUuid,
        "여름맞이 이벤트",
        "여름 시즌 한정 할인",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(4),
        BigDecimal.valueOf(2000),
        "READY",
        "RESTAURANT",
        1000
    );

    PaginatedResult<PromotionSearchCriteriaQuery> paginatedResult =
        new PaginatedResult<>(
            List.of(result1, result2),
            0, 10, 2L, 1
        );

    when(promotionRepository.searchPromotion(criteria)).thenReturn(paginatedResult);

    // when
    PaginatedResultCommand<SearchPromotionInfo> result = promotionService.searchPromotion(command);

    // then
    assertThat(result.page()).isEqualTo(0);
    assertThat(result.size()).isEqualTo(10);
    assertThat(result.totalElements()).isEqualTo(2L);
    assertThat(result.totalPages()).isEqualTo(1);
    assertThat(result.content()).hasSize(2);
    assertThat(result.content().get(0).promotionName()).isEqualTo("봄맞이 할인");

    verify(promotionRepository).searchPromotion(criteria);
  }

  @DisplayName("promotionUuid로 생성된 프로모션을 삭제한다.")
  @Test
  void delete_promotion_success() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    Long deleterUserId = 1L;
    CurrentUserInfoDto currentUserInfo = new CurrentUserInfoDto(deleterUserId, UserRole.MASTER);

    Promotion promotion = mock(Promotion.class);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(promotion));

    // when
    promotionService.deletePromotion(promotionUuid, currentUserInfo);

    // then
    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
    verify(promotion).delete(deleterUserId);
  }

  @DisplayName("존재하지 않는 promotionUuid로 프로모션을 삭제 시 예외가 발생한다.")
  @Test
  void delete_promotion_invalid_uuid_exception() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    CurrentUserInfoDto currentUserInfo = new CurrentUserInfoDto(1L, UserRole.MASTER);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        promotionService.deletePromotion(promotionUuid, currentUserInfo)
    ).isInstanceOf(CustomException.class)
        .hasMessage(PromotionErrorCode.INVALID_PROMOTION_UUID.getMessage());

    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
  }

  @DisplayName("진행 중인 프로모션은 삭제할 수 없다.")
  @Test
  void delete_promotion_running_status_exception() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    CurrentUserInfoDto currentUserInfo = new CurrentUserInfoDto(1L, UserRole.MASTER);

    Promotion promotion = mock(Promotion.class);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(promotion));
    // 진행 중인 상태
    when(promotion.getPromotionStatus()).thenReturn(PromotionStatus.RUNNING);

    // when & then
    assertThatThrownBy(() ->
        promotionService.deletePromotion(promotionUuid, currentUserInfo)
    ).isInstanceOf(CustomException.class)
        .hasMessage(PromotionErrorCode.CANNOT_DELETE_RUNNING_PROMOTION.getMessage());

    verify(promotionRepository).findByPromotionUuidAndDeletedByIsNull(promotionUuid);
    verify(promotion).getPromotionStatus();
    verify(promotion, never()).delete(any());
  }

  @DisplayName("레스토랑 UUID와 프로모션 UUID 리스트로 프로모션 정보를 조회한다.")
  @Test
  void reservation_get_promotions_success() {
    String restaurantUuid = "restaurant-uuid-123";
    String couponUuid = UUID.randomUUID().toString();
    Set<String> promotionUuids = Set.of("promo-uuid-1", "promo-uuid-2");

    GetPromotionsFeignCommand command = new GetPromotionsFeignCommand(promotionUuids, restaurantUuid);

    GetPromotionRestaurantInfo promotionRestaurantInfo1 = new GetPromotionRestaurantInfo(
        "promo-uuid-1", "promotion-restaurant-uuid-1", restaurantUuid);

    GetPromotionRestaurantInfo promotionRestaurantInfo2 = new GetPromotionRestaurantInfo(
        "promo-uuid-2", "promotion-restaurant-uuid-2", restaurantUuid);

    when(promotionClient.findRestaurantsByPromotions(restaurantUuid, "promo-uuid-1"))
        .thenReturn(promotionRestaurantInfo1);

    when(promotionClient.findRestaurantsByPromotions(restaurantUuid, "promo-uuid-2"))
        .thenReturn(promotionRestaurantInfo2);

    Promotion promo1 = Promotion.of(
        couponUuid,
        "봄 프로모션", "할인 설명입니다",
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(10),
        BigDecimal.valueOf(1000),
        PromotionStatus.RUNNING,
        PromotionType.RESTAURANT,
        1000
    );

    Promotion promo2 = Promotion.of(
        couponUuid,
        "여름 프로모션", "여름 한정 할인",
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(5),
        BigDecimal.valueOf(2000),
        PromotionStatus.RUNNING,
        PromotionType.RESTAURANT,
        1000
    );

    ReflectionTestUtils.setField(promo1, "promotionUuid", "promo-uuid-1");
    ReflectionTestUtils.setField(promo2, "promotionUuid", "promo-uuid-2");

    when(promotionRepository.findAllByPromotionUuidInAndDeletedByIsNull(promotionUuids))
        .thenReturn(List.of(promo1, promo2));

    GetPromotionsClientInfo result = promotionService.reservationGetPromotions(command);

    assertThat(result).isNotNull();
    assertThat(result.reservationRequests()).hasSize(2);

    verify(promotionClient).findRestaurantsByPromotions(restaurantUuid, "promo-uuid-1");
    verify(promotionClient).findRestaurantsByPromotions(restaurantUuid, "promo-uuid-2");
    verify(promotionRepository).findAllByPromotionUuidInAndDeletedByIsNull(promotionUuids);
  }

  @Test
  @DisplayName("프로모션 참여 실패 시 false를 반환한다.")
  void participate_promotion_fail() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    ParticipatePromotionUserInfo info = new ParticipatePromotionUserInfo(
        1L,
        promotionUuid,
        "실패 프로모션"
    );

    Promotion fakePromotion = Promotion.of(
        "test",
        "실패 프로모션",
        "test",
        LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3),
            BigDecimal.valueOf(3000),
        PromotionStatus.RUNNING,
        PromotionType.COUPON,
        1000
        );

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(fakePromotion));

    when(promotionRepository.addUserToPromotion(any(PromotionParticipant.class),
        eq(fakePromotion.getMaxParticipant().getMaxParticipantsValue())))
        .thenReturn(ParticipateResult.FAIL);

    when(meterRegistry.counter(PROMOTION_PARTICIPATION_FAIL)).thenReturn(counter);

    // when
    boolean result = promotionService.participate(info);

    // then
    assertThat(result).isFalse();
    verify(promotionRepository).addUserToPromotion(any(), eq(fakePromotion.getMaxParticipant().getMaxParticipantsValue()));
    verifyNoInteractions(promotionEventPublisher);
    verify(meterRegistry).counter(PROMOTION_PARTICIPATION_FAIL);
    verify(counter).increment();
  }



  @DisplayName("참여 후 배치 전송 대상이면 true를 반환하고 이벤트를 발행한다.")
  @Test
  void participate_success_send_batch_event() {
    // given
    Long userId = 2L;
    String promotionUuid = "test-promotion-uuid";
    String promotionName = "이벤트 대상 프로모션";
    String couponUuid = "test-coupon-uuid";
    Integer maxParticipant = 1000;

    ParticipatePromotionUserInfo info = new ParticipatePromotionUserInfo(
        userId,
        promotionUuid,
        promotionName
    );

    when(promotionRepository.addUserToPromotion(argThat(participant ->
            participant.userId().equals(userId) &&
                participant.promotionUuid().equals(promotionUuid)),
        eq(maxParticipant)))
        .thenReturn(ParticipateResult.SUCCESS_SEND_BATCH);

    List<PromotionParticipantDto> participantList = List.of(
        new PromotionParticipantDto(userId, promotionUuid)
    );

    when(promotionRepository.getPromotionUsers(promotionName))
        .thenReturn(participantList);

    Promotion promotion = Promotion.of(
        couponUuid,
        promotionName,
        "전 메뉴 5000원 할인",
        LocalDateTime.now().plusDays(2),
        LocalDateTime.now().plusDays(12),
        BigDecimal.valueOf(5000),
        PromotionStatus.valueOf("RUNNING"),
        PromotionType.valueOf("COUPON"),
        maxParticipant);

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(promotion));
    when(meterRegistry.counter(PROMOTION_PARTICIPATION_SUCCESS)).thenReturn(counter);


    ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

    // when
    boolean result = promotionService.participate(info);


    // then
    assertThat(result).isTrue();

    verify(promotionRepository).addUserToPromotion(any(), eq(maxParticipant));
    verify(promotionRepository).getPromotionUsers(promotionName);
    verify(promotionRepository, times(2)).findByPromotionUuidAndDeletedByIsNull(promotionUuid);

    verify(meterRegistry).counter(PROMOTION_PARTICIPATION_SUCCESS);
    verify(counter).increment();

    verify(promotionEventPublisher, times(1)).publish((PromotionEvent) eventCaptor.capture());

    List<Object> publishedEvents = eventCaptor.getAllValues();

    boolean hasSaveEvent = publishedEvents.stream()
        .anyMatch(e -> e instanceof PromotionUserSaveEvent);


    assertThat(hasSaveEvent).isTrue();

  }




  @DisplayName("성공적으로 참여했지만 배치 대상이 아니면 true를 반환하고 이벤트 발행은 하지 않는다.")
  @Test
  void participate_success_without_batch() {
    // given
    Integer maxParticipant = 1000;

    Promotion savedPromotion = Promotion.of(
        "sample-coupon-uuid",
        "취침 프로모션",
        "자고 싶다...",
        LocalDateTime.now(),
        LocalDateTime.now().plusDays(1),
        BigDecimal.valueOf(1000),
        PromotionStatus.RUNNING,
        PromotionType.COUPON,
        maxParticipant
    );

    lenient().when(promotionRepository.save(any(Promotion.class))).thenReturn(savedPromotion);
    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(savedPromotion.getPromotionUuid()))
        .thenReturn(Optional.of(savedPromotion));
    when(meterRegistry.counter(PROMOTION_PARTICIPATION_SUCCESS)).thenReturn(counter);



    ParticipatePromotionUserInfo info = new ParticipatePromotionUserInfo(
        3L,
        savedPromotion.getPromotionUuid(),
        savedPromotion.getDetails().getPromotionName()
    );


    when(promotionRepository.addUserToPromotion(any(PromotionParticipant.class)
        , eq(maxParticipant)))
        .thenReturn(ParticipateResult.SUCCESS);

    // when
    boolean result = promotionService.participate(info);



    // then
    assertThat(result).isTrue();
    verify(promotionRepository).addUserToPromotion(any(), eq(maxParticipant));
    verify(promotionRepository, never()).getPromotionUsers(anyString());


    verify(promotionEventPublisher, never()).publish(
        (PromotionEvent) argThat((ArgumentMatcher<Object>) event -> event instanceof PromotionUserSaveEvent)
    );
    verify(meterRegistry).counter(PROMOTION_PARTICIPATION_SUCCESS);
    verify(counter).increment();


  }


  @DisplayName("프로모션 참여시 프로모션의 상태가 running 상태가 아닐시 예외가 발생한다.")
  @Test
  void participate_fail_promotion_not_running() {
    // given
    String promotionUuid = UUID.randomUUID().toString();
    ParticipatePromotionUserInfo info = new ParticipatePromotionUserInfo(
        1L,
        promotionUuid,
        "종료된 프로모션"
    );

    Promotion closePromotion = Promotion.of(
        "test",
        "종료된 프로모션",
        "test",
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(3),
        BigDecimal.valueOf(3000),
        PromotionStatus.CLOSED,
        PromotionType.COUPON,
        1000
    );

    when(promotionRepository.findByPromotionUuidAndDeletedByIsNull(promotionUuid))
        .thenReturn(Optional.of(closePromotion));

    // when & then
    CustomException exception = assertThrows(CustomException.class,
        () -> promotionService.participate(info));

    assertThat(exception.getStatus()).isEqualTo(PromotionErrorCode.NOT_RUNNING_PROMOTION.getStatus());

  }



}