package table.eat.now.review.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static table.eat.now.common.resolver.dto.UserRole.CUSTOMER;
import static table.eat.now.common.resolver.dto.UserRole.MASTER;
import static table.eat.now.common.resolver.dto.UserRole.OWNER;
import static table.eat.now.common.resolver.dto.UserRole.STAFF;
import static table.eat.now.review.application.exception.ReviewErrorCode.REVIEW_ALREADY_EXISTS;
import static table.eat.now.review.application.exception.ReviewErrorCode.SERVICE_USER_MISMATCH;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.review.application.client.ReservationClient;
import table.eat.now.review.application.client.RestaurantClient;
import table.eat.now.review.application.client.WaitingClient;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;
import table.eat.now.review.application.service.dto.request.SearchAdminReviewQuery;
import table.eat.now.review.application.service.dto.request.SearchReviewQuery;
import table.eat.now.review.application.service.dto.request.UpdateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.application.client.dto.GetRestaurantInfo;
import table.eat.now.review.application.client.dto.GetRestaurantStaffInfo;
import table.eat.now.review.application.service.dto.response.GetReviewInfo;
import table.eat.now.review.application.client.dto.GetServiceInfo;
import table.eat.now.review.application.service.dto.response.PaginatedInfo;
import table.eat.now.review.application.service.dto.response.SearchAdminReviewInfo;
import table.eat.now.review.application.service.dto.response.SearchReviewInfo;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.repository.ReviewRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReviewServiceImplTest {

  @Autowired
  private ReviewService reviewService;

  @Autowired
  private ReviewRepository reviewRepository;

  @MockitoBean
  private WaitingClient waitingClient;

  @MockitoBean
  private ReservationClient reservationClient;

  @MockitoBean
  private RestaurantClient restaurantClient;

  @Nested
  class createReview_는 {

    private String restaurantId;
    private String serviceId;
    private Long customerId;
    private CreateReviewCommand command;
    private GetServiceInfo serviceInfo;

    @BeforeEach
    void setUp() {
      restaurantId = UUID.randomUUID().toString();
      serviceId = UUID.randomUUID().toString();
      customerId = 123L;

      command = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "RESERVATION",
          "맛있는 식당이었습니다.", 4, true, CUSTOMER);

      serviceInfo = new GetServiceInfo(restaurantId, customerId);
    }

    @Test
    void 유효한_요청으로_리뷰를_생성하면_저장된_리뷰_정보를_반환한다() {
      // given
      when(reservationClient.getReservation(serviceId)).thenReturn(serviceInfo);

      // when
      CreateReviewInfo result = reviewService.createReview(command);

      // then
      assertThat(result.createdAt()).isNotNull();
      verify(reservationClient).getReservation(serviceId);
    }

    @Test
    void WAITING_타입의_리뷰를_생성하면_웨이팅_클라이언트를_호출한다() {
      // given
      CreateReviewCommand waitingCommand = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "WAITING",
          "맛있는 식당이었습니다.", 4, true, CUSTOMER);
      when(waitingClient.getWaiting(serviceId)).thenReturn(serviceInfo);

      // when
      CreateReviewInfo result = reviewService.createReview(waitingCommand);

      // then
      assertThat(result).isNotNull();
      assertThat(result.serviceType()).isEqualTo("WAITING");
    }

    @Test
    void 요청한_사용자와_서비스의_사용자가_다르면_예외를_발생시킨다() {
      // given
      Long differentCustomerId = 456L;
      GetServiceInfo differentServiceInfo = new GetServiceInfo(restaurantId, differentCustomerId);

      when(reservationClient.getReservation(serviceId)).thenReturn(
          differentServiceInfo);

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.createReview(command));

      assertThat(exception.getMessage()).isEqualTo(SERVICE_USER_MISMATCH.getMessage());
    }

    @Test
    void 동일한_참조_정보로_리뷰를_작성시_예외를_발생시킨다() {
      // given
      when(reservationClient.getReservation(serviceId)).thenReturn(serviceInfo);
      reviewRepository.save(command.toEntity());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.createReview(command));

      assertThat(exception.getMessage()).isEqualTo(REVIEW_ALREADY_EXISTS.getMessage());
    }
  }

  @Nested
  class getReview_는 {

    private String reviewId;
    private String restaurantId;
    private String serviceId;
    private Long customerId;
    private Long otherUserId;
    private Long staffId;
    private Long ownerId;
    private GetServiceInfo serviceInfo;
    private CurrentUserInfoDto customerInfo;
    private CurrentUserInfoDto otherUserInfo;
    private CurrentUserInfoDto staffInfo;
    private CurrentUserInfoDto ownerInfo;

    @BeforeEach
    void setUp() {
      reviewId = UUID.randomUUID().toString();
      restaurantId = UUID.randomUUID().toString();
      serviceId = UUID.randomUUID().toString();
      customerId = 123L;
      otherUserId = 456L;
      staffId = 789L;
      ownerId = 999L;

      customerInfo = new CurrentUserInfoDto(customerId, CUSTOMER);
      otherUserInfo = new CurrentUserInfoDto(otherUserId, CUSTOMER);
      staffInfo = new CurrentUserInfoDto(staffId, STAFF);
      ownerInfo = new CurrentUserInfoDto(ownerId, OWNER);
      serviceInfo = new GetServiceInfo(restaurantId, customerId);

      CreateReviewCommand command = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "RESERVATION",
          "맛있는 식당이었습니다.", 4,
          false,
          CUSTOMER
      );

      when(reservationClient.getReservation(serviceId)).thenReturn(serviceInfo);
      Review review = reviewRepository.save(command.toEntity());
      reviewId = review.getReviewId();
    }

    @Test
    void 리뷰_작성자는_자신의_리뷰에_접근할_시_리뷰정보를_반환한다() {
      // when
      GetReviewInfo result = reviewService.getReview(reviewId, customerInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
    }

    @Test
    void 존재하지_않는_리뷰에_접근할_시_예외를_발생시킨다() {
      // given
      String nonExistentReviewId = UUID.randomUUID().toString();

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.getReview(nonExistentReviewId, customerInfo));

      assertThat(exception.getMessage()).isEqualTo("해당 리뷰를 찾을 수 없습니다.");
    }

    @Test
    void 다른_일반_사용자가_비공개_리뷰에_접근할_시_예외를_발생시킨다() {
      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.getReview(reviewId, otherUserInfo));

      assertThat(exception.getMessage()).contains("비공개 처리된 리뷰입니다");
    }

    @Test
    void 레스토랑_직원이_비공개_리뷰에_접근할_시_리뷰_정보를_반환한다() {
      // given
      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when
      GetReviewInfo result = reviewService.getReview(reviewId, staffInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      verify(restaurantClient).getRestaurantStaffs(restaurantId);
    }

    @Test
    void 레스토랑_주인이_비공개_리뷰에_접근할_시_리뷰_정보를_반환한다() {
      // given
      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when
      GetReviewInfo result = reviewService.getReview(reviewId, ownerInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      verify(restaurantClient).getRestaurantStaffs(restaurantId);
    }

    @Test
    void 다른_레스토랑_직원이_비공개_리뷰에_접근할_시_예외를_발생시킨다() {
      // given
      Long differentStaffId = 555L;
      CurrentUserInfoDto differentStaffInfo = new CurrentUserInfoDto(differentStaffId, STAFF);

      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.getReview(reviewId, differentStaffInfo));

      assertThat(exception.getMessage()).contains("비공개 처리된 리뷰입니다");
      verify(restaurantClient).getRestaurantStaffs(restaurantId);
    }

    @Test
    void 공개_리뷰는_모든_사용자가_접근_가능하게_한다() {
      // given
      // 공개 리뷰 생성 및 저장
      CreateReviewCommand publicCommand = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "RESERVATION",
          "맛있는 식당이었습니다.", 4,
          true, // 공개 리뷰
          CUSTOMER
      );

      when(reservationClient.getReservation(serviceId)).thenReturn(serviceInfo);
      Review publicReview = reviewRepository.save(publicCommand.toEntity());
      String publicReviewId = publicReview.getReviewId();

      // when
      GetReviewInfo result = reviewService.getReview(publicReviewId, otherUserInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(publicReviewId);
    }

    @Test
    void MASTER_역할은_모든_리뷰에_접근_가능하게_한다() {
      // given
      CurrentUserInfoDto masterInfo = new CurrentUserInfoDto(otherUserId, MASTER);

      // when
      GetReviewInfo result = reviewService.getReview(reviewId, masterInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
    }
  }

  @Nested
  class hideReview_는 {

    private String reviewId;
    private String restaurantId;
    private Long staffId;
    private Long ownerId;
    private CurrentUserInfoDto customerInfo;
    private CurrentUserInfoDto otherUserInfo;
    private CurrentUserInfoDto staffInfo;
    private CurrentUserInfoDto ownerInfo;
    private CurrentUserInfoDto masterInfo;

    @BeforeEach
    void setUp() {
      String serviceId = UUID.randomUUID().toString();
      Long customerId = 123L;
      Long otherUserId = 456L;
      reviewId = UUID.randomUUID().toString();
      restaurantId = UUID.randomUUID().toString();
      staffId = 789L;
      ownerId = 999L;

      customerInfo = new CurrentUserInfoDto(customerId, CUSTOMER);
      otherUserInfo = new CurrentUserInfoDto(otherUserId, CUSTOMER);
      staffInfo = new CurrentUserInfoDto(staffId, STAFF);
      ownerInfo = new CurrentUserInfoDto(ownerId, OWNER);
      masterInfo = new CurrentUserInfoDto(otherUserId, MASTER);

      CreateReviewCommand command = new CreateReviewCommand(
          restaurantId, serviceId, customerInfo.userId(), "RESERVATION",
          "맛있는 식당이었습니다.", 4,
          true, customerInfo.role()
      );

      Review review = reviewRepository.save(command.toEntity());
      reviewId = review.getReviewId();
    }

    @Test
    void 권한을_가진_사용자로_요청시_리뷰를_숨길_수_있다() {
      // when
      GetReviewInfo result = reviewService.hideReview(reviewId, customerInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.isVisible()).isFalse();
    }

    @Test
    void 작성자가_아닌_일반_사용자로_요청시_예외를_발생시킨다() {
      // when & then
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          reviewService.hideReview(reviewId, otherUserInfo));

      assertThat(exception.getMessage()).contains("이 작업에 대한 권한은 작성자에게만 있습니다.");
    }

    @Test
    void 레스토랑_직원이_요청시_리뷰를_숨길_수_있다() {
      // given
      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when
      GetReviewInfo result = reviewService.hideReview(reviewId, staffInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.isVisible()).isFalse();
      verify(restaurantClient).getRestaurantStaffs(restaurantId);
    }

    @Test
    void 레스토랑_주인이_요청시_리뷰를_숨길_수_있다() {
      // given
      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when
      GetReviewInfo result = reviewService.hideReview(reviewId, ownerInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.isVisible()).isFalse();
      verify(restaurantClient).getRestaurantStaffs(restaurantId);
    }

    @Test
    void 다른_레스토랑_직원이_요청시_예외를_발생시킨다() {
      // given
      Long differentStaffId = 555L;
      CurrentUserInfoDto differentStaffInfo = new CurrentUserInfoDto(differentStaffId, STAFF);

      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.hideReview(reviewId, differentStaffInfo));

      assertThat(exception.getMessage()).contains("수정 요청에 대한 권한이 없습니다.");
      verify(restaurantClient).getRestaurantStaffs(restaurantId);
    }

    @Test
    void 마스터_역할은_리뷰를_숨길_수_있다() {
      // when
      GetReviewInfo result = reviewService.hideReview(reviewId, masterInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.isVisible()).isFalse();
    }

    @Test
    void 존재하지_않는_리뷰를_숨기려고_하면_예외를_발생시킨다() {
      // given
      String nonExistentReviewId = UUID.randomUUID().toString();

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.hideReview(nonExistentReviewId, customerInfo));

      assertThat(exception.getMessage()).isEqualTo("해당 리뷰를 찾을 수 없습니다.");
    }
  }

  @Nested
  class showReview_는 {

    private String reviewId;
    private String restaurantId;
    private Long staffId;
    private Long ownerId;
    private CurrentUserInfoDto customerInfo;
    private CurrentUserInfoDto otherUserInfo;
    private CurrentUserInfoDto staffInfo;
    private CurrentUserInfoDto ownerInfo;
    private CurrentUserInfoDto masterInfo;

    @BeforeEach
    void setUp() {
      String serviceId = UUID.randomUUID().toString();
      Long customerId = 123L;
      Long otherUserId = 456L;
      reviewId = UUID.randomUUID().toString();
      restaurantId = UUID.randomUUID().toString();
      staffId = 789L;
      ownerId = 999L;

      customerInfo = new CurrentUserInfoDto(customerId, CUSTOMER);
      otherUserInfo = new CurrentUserInfoDto(otherUserId, CUSTOMER);
      staffInfo = new CurrentUserInfoDto(staffId, STAFF);
      ownerInfo = new CurrentUserInfoDto(ownerId, OWNER);
      masterInfo = new CurrentUserInfoDto(otherUserId, MASTER);

      CreateReviewCommand command = new CreateReviewCommand(
          restaurantId, serviceId, customerInfo.userId(), "RESERVATION",
          "맛있는 식당이었습니다.", 4,
          false, customerInfo.role()
      );

      Review review = reviewRepository.save(command.toEntity());
      reviewId = review.getReviewId();
    }

    @Test
    void 권한을_가진_사용자로_요청시_자신의_리뷰를_공개할_수_있다() {
      // when
      GetReviewInfo result = reviewService.showReview(reviewId, customerInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.isVisible()).isTrue();
    }

    @Test
    void 작성자가_아닌_일반_사용자로_요청시_예외를_발생시킨다() {
      // when & then
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          reviewService.showReview(reviewId, otherUserInfo));

      assertThat(exception.getMessage()).contains("이 작업에 대한 권한은 작성자에게만 있습니다.");
    }

    @Test
    void 레스토랑_직원이_요청시_리뷰를_공개할_수_있다() {
      // given
      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when
      GetReviewInfo result = reviewService.showReview(reviewId, staffInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.isVisible()).isTrue();
      verify(restaurantClient).getRestaurantStaffs(restaurantId);
    }

    @Test
    void 레스토랑_주인이_요청시_리뷰를_공개할_수_있다() {
      // given
      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when
      GetReviewInfo result = reviewService.showReview(reviewId, ownerInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.isVisible()).isTrue();
      verify(restaurantClient).getRestaurantStaffs(restaurantId);
    }

    @Test
    void 다른_레스토랑_직원이_요청시_예외를_발생시킨다() {
      // given
      Long differentStaffId = 555L;
      CurrentUserInfoDto differentStaffInfo = new CurrentUserInfoDto(differentStaffId, STAFF);

      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.showReview(reviewId, differentStaffInfo));

      assertThat(exception.getMessage()).contains("수정 요청에 대한 권한이 없습니다.");
      verify(restaurantClient).getRestaurantStaffs(restaurantId);
    }

    @Test
    void 마스터_역할은_리뷰를_공개할_수_있다() {
      // when
      GetReviewInfo result = reviewService.showReview(reviewId, masterInfo);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.isVisible()).isTrue();
    }

    @Test
    void 존재하지_않는_리뷰를_공개하려고_하면_예외를_발생시킨다() {
      // given
      String nonExistentReviewId = UUID.randomUUID().toString();

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.showReview(nonExistentReviewId, customerInfo));

      assertThat(exception.getMessage()).isEqualTo("해당 리뷰를 찾을 수 없습니다.");
    }

    @Test
    void 관리자가_숨긴_리뷰는_일반_사용자가_공개할_수_없다() {
      // given
      reviewService.showReview(reviewId, customerInfo); // 숨김 상태는 변경이 없어서 공개상태로 변경
      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);
      reviewService.hideReview(reviewId, staffInfo);

      // when & then
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          reviewService.showReview(reviewId, customerInfo));

      assertThat(exception.getMessage()).contains("관리자가 숨긴 리뷰는 일반 사용자가 공개할 수 없습니다");
    }
  }

  @Nested
  class updateReview_는 {

    private String reviewId;
    private String restaurantId;
    private String serviceId;
    private Long customerId;
    private Long otherUserId;
    private Long staffId;
    private Long ownerId;
    private CurrentUserInfoDto customerInfo;
    private CurrentUserInfoDto otherUserInfo;
    private CurrentUserInfoDto staffInfo;
    private CurrentUserInfoDto ownerInfo;
    private CurrentUserInfoDto masterInfo;
    private UpdateReviewCommand updateCommand;

    @BeforeEach
    void setUp() {
      serviceId = UUID.randomUUID().toString();
      customerId = 123L;
      otherUserId = 456L;
      reviewId = UUID.randomUUID().toString();
      restaurantId = UUID.randomUUID().toString();
      staffId = 789L;
      ownerId = 999L;

      customerInfo = new CurrentUserInfoDto(customerId, CUSTOMER);
      otherUserInfo = new CurrentUserInfoDto(otherUserId, CUSTOMER);
      staffInfo = new CurrentUserInfoDto(staffId, STAFF);
      ownerInfo = new CurrentUserInfoDto(ownerId, OWNER);
      masterInfo = new CurrentUserInfoDto(otherUserId, MASTER);

      updateCommand = new UpdateReviewCommand("수정된 리뷰 내용입니다.", 5, customerInfo);

      CreateReviewCommand command = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "RESERVATION",
          "맛있는 식당이었습니다.", 4, true, CUSTOMER
      );

      Review review = reviewRepository.save(command.toEntity());
      reviewId = review.getReviewId();
    }

    @Test
    void 작성자가_요청시_자신의_리뷰를_수정할_수_있다() {
      // when
      GetReviewInfo result = reviewService.updateReview(reviewId, updateCommand);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.content()).isEqualTo("수정된 리뷰 내용입니다.");
      assertThat(result.rating()).isEqualTo(5);
    }

    @Test
    void 다른_일반_사용자가_요청시_예외를_발생시킨다() {
      // given
      UpdateReviewCommand otherUserCommand = new UpdateReviewCommand(
          "다른 사용자가 수정한 내용", 3, otherUserInfo);

      // when & then
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          reviewService.updateReview(reviewId, otherUserCommand));

      assertThat(exception.getMessage()).contains("이 작업에 대한 권한은 작성자에게만 있습니다.");
    }

    @Test
    void 레스토랑_직원이_요청시_리뷰를_수정할_수_있다() {
      // given
      UpdateReviewCommand staffCommand = new UpdateReviewCommand(
          "직원이 수정한 내용", 3, staffInfo);
      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when
      GetReviewInfo result = reviewService.updateReview(reviewId, staffCommand);
      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.content()).isEqualTo("직원이 수정한 내용");
      assertThat(result.rating()).isEqualTo(3);
    }

    @Test
    void 레스토랑_주인이_요청시_리뷰를_수정할_수_있다() {
      // given
      UpdateReviewCommand staffCommand = new UpdateReviewCommand(
          "주인이 수정한 내용", 3, ownerInfo);
      GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
      when(restaurantClient.getRestaurantStaffs(restaurantId)).thenReturn(staffInfoResponse);

      // when
      GetReviewInfo result = reviewService.updateReview(reviewId, staffCommand);
      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.content()).isEqualTo("주인이 수정한 내용");
      assertThat(result.rating()).isEqualTo(3);
    }

    @Test
    void 관리자는_리뷰를_수정할_수_있다() {
      // given
      UpdateReviewCommand masterCommand = new UpdateReviewCommand(
          "관리자가 수정한 내용", 2, masterInfo);

      // when
      GetReviewInfo result = reviewService.updateReview(reviewId, masterCommand);

      // then
      assertThat(result).isNotNull();
      assertThat(result.reviewUuid()).isEqualTo(reviewId);
      assertThat(result.content()).isEqualTo("관리자가 수정한 내용");
      assertThat(result.rating()).isEqualTo(2);
    }

    @Test
    void 존재하지_않는_리뷰를_수정하려고_하면_예외를_발생시킨다() {
      // given
      String nonExistentReviewId = UUID.randomUUID().toString();

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.updateReview(nonExistentReviewId, updateCommand));

      assertThat(exception.getMessage()).isEqualTo("해당 리뷰를 찾을 수 없습니다.");
    }

    @Test
    void 리뷰_내용과_평점을_모두_수정할_수_있다() {
      // given
      String newContent = "완전히 다른 내용으로 수정합니다.";
      int newRating = 1;
      UpdateReviewCommand fullUpdateCommand = new UpdateReviewCommand(
          newContent, newRating, customerInfo);

      // when
      GetReviewInfo result = reviewService.updateReview(reviewId, fullUpdateCommand);

      // then
      assertThat(result).isNotNull();
      assertThat(result.content()).isEqualTo(newContent);
      assertThat(result.rating()).isEqualTo(newRating);
    }
  }

  @Nested
  class searchReviews_는 {

    private String restaurantId;
    private Long customerId;
    private Long otherUserId;
    private CurrentUserInfoDto userInfo;
    private SearchReviewQuery query;
    private Review myPublicReview;
    private Review myPrivateReview;
    private Review otherPublicReview;
    private Review otherPrivateReview;

    @BeforeEach
    void setUp() {
      String serviceId = UUID.randomUUID().toString();
      restaurantId = UUID.randomUUID().toString();
      customerId = 123L;
      otherUserId = 456L;

      userInfo = new CurrentUserInfoDto(customerId, CUSTOMER);
      query = SearchReviewQuery.builder()
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      CreateReviewCommand myPublicCommand = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "RESERVATION",
          "맛있는 식당이었습니다.", 4, true, CUSTOMER
      );
      myPublicReview = reviewRepository.save(myPublicCommand.toEntity());

      CreateReviewCommand myPrivateCommand = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "RESERVATION",
          "서비스가 아쉬웠습니다.", 2, false, CUSTOMER
      );
      myPrivateReview = reviewRepository.save(myPrivateCommand.toEntity());

      CreateReviewCommand otherPublicCommand = new CreateReviewCommand(
          restaurantId, serviceId, otherUserId, "WAITING",
          "최고의 레스토랑입니다!", 5, true, CUSTOMER
      );
      otherPublicReview = reviewRepository.save(otherPublicCommand.toEntity());

      CreateReviewCommand otherPrivateCommand = new CreateReviewCommand(
          restaurantId, serviceId, otherUserId, "WAITING",
          "실망스러웠습니다.", 1, false, CUSTOMER
      );
      otherPrivateReview = reviewRepository.save(otherPrivateCommand.toEntity());
    }

    @Test
    void 자신의_모든_리뷰와_타인의_공개_리뷰를_반환한다() {
      // when
      PaginatedInfo<SearchReviewInfo> result = reviewService.searchReviews(query, userInfo);

      // then
      assertThat(result.content()).hasSize(3);
      assertThat(result.content().stream()
          .map(SearchReviewInfo::reviewUuid))
          .contains(
              myPublicReview.getReviewId(),
              myPrivateReview.getReviewId(),
              otherPublicReview.getReviewId()
          )
          .doesNotContain(otherPrivateReview.getReviewId());
    }

    @Test
    void userId로_필터링시_자신의_모든_리뷰를_반환한다() {
      // given
      SearchReviewQuery myReviewsQuery = SearchReviewQuery.builder()
          .userId(customerId)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchReviewInfo> result =
          reviewService.searchReviews(myReviewsQuery, userInfo);

      // then
      assertThat(result.content()).hasSize(2);
      assertThat(result.content().stream()
          .map(SearchReviewInfo::reviewUuid))
          .containsExactlyInAnyOrder(
              myPublicReview.getReviewId(),
              myPrivateReview.getReviewId()
          );
    }

    @Test
    void userId와_isVisible_필터로_자신의_공개_리뷰만_반환한다() {
      // given
      SearchReviewQuery myPublicReviewsQuery = SearchReviewQuery.builder()
          .userId(customerId)
          .isVisible(true)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchReviewInfo> result =
          reviewService.searchReviews(myPublicReviewsQuery, userInfo);

      // then
      assertThat(result.content()).hasSize(1);
      assertThat(result.content().get(0).reviewUuid()).isEqualTo(myPublicReview.getReviewId());
      assertThat(result.content().get(0).isVisible()).isTrue();
    }

    @Test
    void userId와_isVisible_필터로_자신의_비공개_리뷰만_반환한다() {
      // given
      SearchReviewQuery myPrivateReviewsQuery = SearchReviewQuery.builder()
          .userId(customerId)
          .isVisible(false)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchReviewInfo> result =
          reviewService.searchReviews(myPrivateReviewsQuery, userInfo);

      // then
      assertThat(result.content()).hasSize(1);
      assertThat(result.content().get(0).reviewUuid()).isEqualTo(myPrivateReview.getReviewId());
      assertThat(result.content().get(0).isVisible()).isFalse();
    }

    @Test
    void 다른_사용자의_리뷰는_공개_리뷰만_반환한다() {
      // given
      SearchReviewQuery otherUserReviewsQuery = SearchReviewQuery.builder()
          .userId(otherUserId)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchReviewInfo> result =
          reviewService.searchReviews(otherUserReviewsQuery, userInfo);

      // then
      assertThat(result.content()).hasSize(1);
      assertThat(result.content().get(0).reviewUuid()).isEqualTo(otherPublicReview.getReviewId());
      assertThat(result.content().get(0).isVisible()).isTrue();
    }

    @Test
    void restaurantId로_필터링시_자신의_모든_리뷰와_타인의_공개_리뷰를_반환한다() {
      // given
      SearchReviewQuery restaurantReviewsQuery = SearchReviewQuery.builder()
          .restaurantId(restaurantId)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchReviewInfo> result =
          reviewService.searchReviews(restaurantReviewsQuery, userInfo);

      // then
      assertThat(result.content()).hasSize(3);
      assertThat(result.content().stream()
          .map(SearchReviewInfo::reviewUuid))
          .contains(
              myPublicReview.getReviewId(),
              myPrivateReview.getReviewId(),
              otherPublicReview.getReviewId()
          )
          .doesNotContain(otherPrivateReview.getReviewId());
    }

    @Test
    void serviceType으로_필터링시_해당_타입의_리뷰만_반환한다() {
      // given
      SearchReviewQuery waitingReviewsQuery = SearchReviewQuery.builder()
          .serviceType("WAITING")
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchReviewInfo> result =
          reviewService.searchReviews(waitingReviewsQuery, userInfo);

      // then
      assertThat(result.content()).hasSize(1);
      assertThat(result.content().get(0).reviewUuid()).isEqualTo(otherPublicReview.getReviewId());
      assertThat(result.content().get(0).serviceType()).isEqualTo("WAITING");
    }

    @Test
    void 평점_범위로_필터링시_해당_평점_범위의_리뷰만_반환한다() {
      // given
      SearchReviewQuery highRatingReviewsQuery = SearchReviewQuery.builder()
          .minRating(4)
          .maxRating(5)
          .orderBy("rating")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchReviewInfo> result =
          reviewService.searchReviews(highRatingReviewsQuery, userInfo);

      // then
      assertThat(result.content()).hasSize(2);
      assertThat(result.content().stream()
          .map(SearchReviewInfo::reviewUuid))
          .containsExactlyInAnyOrder(
              myPublicReview.getReviewId(),
              otherPublicReview.getReviewId()
          );
      assertThat(result.content().get(0).rating())
          .isGreaterThanOrEqualTo(result.content().get(1).rating());
    }

    @Test
    void 여러_조건으로_필터링시_모든_조건을_만족하는_리뷰만_반환한다() {
      // given
      SearchReviewQuery complexQuery = SearchReviewQuery.builder()
          .serviceType("RESERVATION")
          .minRating(3)
          .maxRating(5)
          .orderBy("rating")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchReviewInfo> result = reviewService.searchReviews(complexQuery, userInfo);

      // then
      assertThat(result.content()).hasSize(1);
      assertThat(result.content().get(0).reviewUuid()).isEqualTo(myPublicReview.getReviewId());
      assertThat(result.content().get(0).serviceType()).isEqualTo("RESERVATION");
      assertThat(result.content().get(0).rating()).isGreaterThanOrEqualTo(3);
    }
  }

  @Nested
  class searchAdminReviews_는 {

    private Long customerId;
    private Long staffId;
    private Long ownerId;
    private CurrentUserInfoDto masterUserInfo;
    private CurrentUserInfoDto ownerUserInfo;
    private CurrentUserInfoDto staffUserInfo;
    private SearchAdminReviewQuery query;
    private Review myPublicReview;
    private Review myPrivateReview;
    private Review otherPublicReview;
    private Review otherPrivateReview;
    private String otherRestaurantId;

    @BeforeEach
    void setUp() {
      String serviceId = UUID.randomUUID().toString();
      String restaurantId = UUID.randomUUID().toString();
      customerId = 123L;
      Long otherUserId = 456L;
      staffId = 789L;
      ownerId = 999L;

      masterUserInfo = new CurrentUserInfoDto(otherUserId, MASTER);
      ownerUserInfo = new CurrentUserInfoDto(ownerId, OWNER);
      staffUserInfo = new CurrentUserInfoDto(staffId, STAFF);
      otherRestaurantId = UUID.randomUUID().toString();

      GetRestaurantInfo restaurantInfo = new GetRestaurantInfo(restaurantId);

      when(restaurantClient.getRestaurant()).thenReturn(restaurantInfo);

      query = SearchAdminReviewQuery.builder()
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      CreateReviewCommand myPublicCommand = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "RESERVATION",
          "맛있는 식당이었습니다.", 4, true, CUSTOMER
      );
      myPublicReview = reviewRepository.save(myPublicCommand.toEntity());

      CreateReviewCommand myPrivateCommand = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "RESERVATION",
          "서비스가 아쉬웠습니다.", 2, false, CUSTOMER
      );
      myPrivateReview = reviewRepository.save(myPrivateCommand.toEntity());

      CreateReviewCommand otherPublicCommand = new CreateReviewCommand(
          otherRestaurantId, serviceId, otherUserId, "WAITING",
          "최고의 레스토랑입니다!", 5, true, CUSTOMER
      );
      otherPublicReview = reviewRepository.save(otherPublicCommand.toEntity());

      CreateReviewCommand otherPrivateCommand = new CreateReviewCommand(
          otherRestaurantId, serviceId, otherUserId, "WAITING",
          "실망스러웠습니다.", 1, false, CUSTOMER
      );
      otherPrivateReview = reviewRepository.save(otherPrivateCommand.toEntity());
    }

    @Test
    void 마스터_권한으로_조회시_모든_리뷰를_반환한다() {
      // when
      PaginatedInfo<SearchAdminReviewInfo> result =
          reviewService.searchAdminReviews(query, masterUserInfo);

      // then
      assertThat(result.content()).hasSize(4);
      assertThat(result.content().stream()
          .map(SearchAdminReviewInfo::reviewUuid))
          .contains(
              myPublicReview.getReviewId(),
              myPrivateReview.getReviewId(),
              otherPublicReview.getReviewId(),
              otherPrivateReview.getReviewId()
          );
    }

    @Test
    void 식당_주인_권한으로_조회시_자신의_식당_리뷰와_다른_공개_리뷰를_반환한다() {
      // when
      PaginatedInfo<SearchAdminReviewInfo> result =
          reviewService.searchAdminReviews(query, ownerUserInfo);

      // then
      assertThat(result.content()).hasSize(3);
      assertThat(result.content().stream()
          .map(SearchAdminReviewInfo::reviewUuid))
          .contains(
              myPublicReview.getReviewId(),
              myPrivateReview.getReviewId(),
              otherPublicReview.getReviewId()
          )
          .doesNotContain(otherPrivateReview.getReviewId());

      verify(restaurantClient).getRestaurant();
    }

    @Test
    void 직원_권한으로_조회시_자신의_식당_리뷰와_다른_공개_리뷰를_반환한다() {
      // when
      PaginatedInfo<SearchAdminReviewInfo> result =
          reviewService.searchAdminReviews(query, staffUserInfo);

      // then
      assertThat(result.content()).hasSize(3);
      assertThat(result.content().stream()
          .map(SearchAdminReviewInfo::reviewUuid))
          .contains(
              myPublicReview.getReviewId(),
              myPrivateReview.getReviewId(),
              otherPublicReview.getReviewId()
          )
          .doesNotContain(otherPrivateReview.getReviewId());

      verify(restaurantClient).getRestaurant();
    }

    @Test
    void 마스터_권한으로_isVisible_true_필터링시_공개_리뷰만_반환한다() {
      // given
      SearchAdminReviewQuery visibleQuery = SearchAdminReviewQuery.builder()
          .isVisible(true)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchAdminReviewInfo> result =
          reviewService.searchAdminReviews(visibleQuery, masterUserInfo);

      // then
      assertThat(result.content()).hasSize(2);
      assertThat(result.content().stream()
          .map(SearchAdminReviewInfo::reviewUuid))
          .containsExactlyInAnyOrder(
              myPublicReview.getReviewId(),
              otherPublicReview.getReviewId()
          );
      assertThat(result.content().stream()
          .allMatch(SearchAdminReviewInfo::isVisible)).isTrue();
    }

    @Test
    void 마스터_권한으로_isVisible_false_필터링시_비공개_리뷰만_반환한다() {
      // given
      SearchAdminReviewQuery invisibleQuery = SearchAdminReviewQuery.builder()
          .isVisible(false)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchAdminReviewInfo> result =
          reviewService.searchAdminReviews(invisibleQuery, masterUserInfo);

      // then
      assertThat(result.content()).hasSize(2);
      assertThat(result.content().stream()
          .map(SearchAdminReviewInfo::reviewUuid))
          .containsExactlyInAnyOrder(
              myPrivateReview.getReviewId(),
              otherPrivateReview.getReviewId()
          );
      assertThat(result.content().stream()
          .noneMatch(SearchAdminReviewInfo::isVisible)).isTrue();
    }

    @Test
    void 식당_관계자가_isVisible_false_필터링시_해당_식당의_비공개_리뷰만_반환한다() {
      // given
      SearchAdminReviewQuery invisibleQuery = SearchAdminReviewQuery.builder()
          .isVisible(false)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchAdminReviewInfo> result =
          reviewService.searchAdminReviews(invisibleQuery, ownerUserInfo);

      // then
      assertThat(result.content()).hasSize(1);
      assertThat(result.content().get(0).reviewUuid()).isEqualTo(myPrivateReview.getReviewId());
      assertThat(result.content().get(0).isVisible()).isFalse();
      verify(restaurantClient).getRestaurant();
    }

    @Test
    void userId로_필터링시_해당_사용자의_리뷰만_반환한다() {
      // given
      SearchAdminReviewQuery userQuery = SearchAdminReviewQuery.builder()
          .userId(customerId)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchAdminReviewInfo> result =
          reviewService.searchAdminReviews(userQuery, masterUserInfo);

      // then
      assertThat(result.content()).hasSize(2);
      assertThat(result.content().stream()
          .map(SearchAdminReviewInfo::reviewUuid))
          .containsExactlyInAnyOrder(
              myPublicReview.getReviewId(),
              myPrivateReview.getReviewId()
          );
      assertThat(result.content().stream()
          .allMatch(info -> info.customerId() == customerId)).isTrue();
    }

    @Test
    void restaurantId로_필터링시_해당_식당의_리뷰만_반환한다() {
      // given
      String theOtherRestaurantId = UUID.randomUUID().toString();
      SearchAdminReviewQuery restaurantQuery = SearchAdminReviewQuery.builder()
          .restaurantId(theOtherRestaurantId)
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // 다른 식당의 리뷰 추가
      String serviceId = UUID.randomUUID().toString();
      CreateReviewCommand otherRestaurantReview = new CreateReviewCommand(
          theOtherRestaurantId, serviceId, customerId, "RESERVATION",
          "다른 식당 리뷰입니다.", 3, true, CUSTOMER
      );
      Review savedOtherRestaurantReview = reviewRepository.save(otherRestaurantReview.toEntity());

      // when
      PaginatedInfo<SearchAdminReviewInfo> result = reviewService
          .searchAdminReviews(restaurantQuery, masterUserInfo);

      // then
      assertThat(result.content()).hasSize(1);
      assertThat(result.content().get(0).reviewUuid())
          .isEqualTo(savedOtherRestaurantReview.getReviewId());
      assertThat(result.content().get(0).restaurantId()).isEqualTo(theOtherRestaurantId);
    }

    @Test
    void serviceType으로_필터링시_해당_타입의_리뷰만_반환한다() {
      // given
      SearchAdminReviewQuery waitingQuery = SearchAdminReviewQuery.builder()
          .serviceType("WAITING")
          .orderBy("createdAt")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchAdminReviewInfo> result = reviewService.searchAdminReviews(waitingQuery,
          masterUserInfo);

      // then
      assertThat(result.content()).hasSize(2);
      assertThat(result.content().stream()
          .map(SearchAdminReviewInfo::reviewUuid))
          .containsExactlyInAnyOrder(
              otherPublicReview.getReviewId(),
              otherPrivateReview.getReviewId()
          );
      assertThat(result.content().stream()
          .allMatch(info -> "WAITING".equals(info.serviceType()))).isTrue();
    }

    @Test
    void 평점_범위로_필터링시_해당_평점_범위의_리뷰만_반환한다() {
      // given
      SearchAdminReviewQuery ratingQuery = SearchAdminReviewQuery.builder()
          .minRating(4)
          .maxRating(5)
          .orderBy("rating")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchAdminReviewInfo> result = reviewService.searchAdminReviews(ratingQuery,
          masterUserInfo);

      // then
      assertThat(result.content()).hasSize(2);
      assertThat(result.content().stream()
          .map(SearchAdminReviewInfo::reviewUuid))
          .containsExactlyInAnyOrder(
              myPublicReview.getReviewId(),
              otherPublicReview.getReviewId()
          );
      assertThat(result.content().stream()
          .allMatch(info -> info.rating() >= 4 && info.rating() <= 5)).isTrue();
      assertThat(result.content().get(0).rating())
          .isGreaterThanOrEqualTo(result.content().get(1).rating());
    }

    @Test
    void 여러_조건으로_필터링시_모든_조건을_만족하는_리뷰만_반환한다() {
      // given
      SearchAdminReviewQuery complexQuery = SearchAdminReviewQuery.builder()
          .serviceType("RESERVATION")
          .minRating(3)
          .maxRating(5)
          .isVisible(true)
          .orderBy("rating")
          .sort("desc")
          .page(0)
          .size(10)
          .build();

      // when
      PaginatedInfo<SearchAdminReviewInfo> result = reviewService.searchAdminReviews(complexQuery,
          masterUserInfo);

      // then
      assertThat(result.content()).hasSize(1);
      assertThat(result.content().get(0).reviewUuid()).isEqualTo(myPublicReview.getReviewId());
      assertThat(result.content().get(0).serviceType()).isEqualTo("RESERVATION");
      assertThat(result.content().get(0).rating()).isGreaterThanOrEqualTo(3);
      assertThat(result.content().get(0).isVisible()).isTrue();
    }
  }

  @Nested
  class deleteReview_는 {

    private String reviewId;
    private CurrentUserInfoDto customerInfo;
    private CurrentUserInfoDto otherUserInfo;
    private CurrentUserInfoDto masterInfo;

    @BeforeEach
    void setUp() {
      String serviceId = UUID.randomUUID().toString();
      Long customerId = 123L;
      Long otherUserId = 456L;
      String restaurantId = UUID.randomUUID().toString();

      customerInfo = new CurrentUserInfoDto(customerId, CUSTOMER);
      otherUserInfo = new CurrentUserInfoDto(otherUserId, CUSTOMER);
      masterInfo = new CurrentUserInfoDto(otherUserId, MASTER);

      CreateReviewCommand command = new CreateReviewCommand(
          restaurantId, serviceId, customerId, "RESERVATION",
          "맛있는 식당이었습니다.", 4, true, CUSTOMER
      );

      Review review = reviewRepository.save(command.toEntity());
      reviewId = review.getReviewId();
    }

    @Test
    void 작성자가_요청시_리뷰를_성공적으로_삭제한다() {
      // when
      assertDoesNotThrow(() -> reviewService.deleteReview(reviewId, customerInfo));

      // then
      assertThrows(CustomException.class, () ->
          reviewService.getReview(reviewId, customerInfo));
    }

    @Test
    void 작성자가_아닌_일반_사용자가_요청시_예외를_발생시킨다() {
      // when & then
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
          reviewService.deleteReview(reviewId, otherUserInfo));

      assertThat(exception.getMessage()).contains("이 작업에 대한 권한은 작성자에게만 있습니다.");
    }

    @Test
    void 마스터_권한은_리뷰를_삭제할_수_있다() {
      // when & then
      assertDoesNotThrow(() -> reviewService.deleteReview(reviewId, masterInfo));
      assertThrows(CustomException.class, () ->
          reviewService.getReview(reviewId, masterInfo));
    }

    @Test
    void 존재하지_않는_리뷰를_삭제하려고_하면_예외를_발생시킨다() {
      // given
      String nonExistentReviewId = UUID.randomUUID().toString();

      // when & then
      CustomException exception = assertThrows(CustomException.class, () ->
          reviewService.deleteReview(nonExistentReviewId, customerInfo));

      assertThat(exception.getMessage()).isEqualTo("해당 리뷰를 찾을 수 없습니다.");
    }
  }
}