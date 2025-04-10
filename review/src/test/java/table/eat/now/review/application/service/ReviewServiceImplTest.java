package table.eat.now.review.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static table.eat.now.common.resolver.dto.UserRole.CUSTOMER;
import static table.eat.now.common.resolver.dto.UserRole.MASTER;
import static table.eat.now.common.resolver.dto.UserRole.OWNER;
import static table.eat.now.common.resolver.dto.UserRole.STAFF;

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
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.application.service.dto.response.GetRestaurantStaffInfo;
import table.eat.now.review.application.service.dto.response.GetReviewInfo;
import table.eat.now.review.application.service.dto.response.GetServiceInfo;
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
			when(reservationClient.getReservation(serviceId, customerId)).thenReturn(serviceInfo);

			// when
			CreateReviewInfo result = reviewService.createReview(command);

			// then
			assertThat(result.createdAt()).isNotNull();
			verify(reservationClient).getReservation(serviceId, customerId);
		}

		@Test
		void WAITING_타입의_리뷰를_생성하면_웨이팅_클라이언트를_호출한다() {
			// given
			CreateReviewCommand waitingCommand = new CreateReviewCommand(
					restaurantId, serviceId, customerId, "WAITING",
					"맛있는 식당이었습니다.", 4, true, CUSTOMER);
			when(waitingClient.getWaiting(serviceId, customerId)).thenReturn(serviceInfo);

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

			when(reservationClient.getReservation(serviceId, customerId)).thenReturn(
					differentServiceInfo);

			// when & then
			CustomException exception = assertThrows(CustomException.class, () ->
					reviewService.createReview(command));

			assertThat(exception.getMessage()).isEqualTo("서비스 정보와 사용자 정보가 일치하지 않습니다.");
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
		private Review review;

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

			when(reservationClient.getReservation(serviceId, customerId)).thenReturn(serviceInfo);
			review = reviewRepository.save(command.toEntity());
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
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);

			// when
			GetReviewInfo result = reviewService.getReview(reviewId, staffInfo);

			// then
			assertThat(result).isNotNull();
			assertThat(result.reviewUuid()).isEqualTo(reviewId);
			verify(restaurantClient).getRestaurantStaffInfo(restaurantId);
		}

		@Test
		void 레스토랑_주인이_비공개_리뷰에_접근할_시_리뷰_정보를_반환한다() {
			// given
			GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);

			// when
			GetReviewInfo result = reviewService.getReview(reviewId, ownerInfo);

			// then
			assertThat(result).isNotNull();
			assertThat(result.reviewUuid()).isEqualTo(reviewId);
			verify(restaurantClient).getRestaurantStaffInfo(restaurantId);
		}

		@Test
		void 다른_레스토랑_직원이_비공개_리뷰에_접근할_시_예외를_발생시킨다() {
			// given
			Long differentStaffId = 555L;
			CurrentUserInfoDto differentStaffInfo = new CurrentUserInfoDto(differentStaffId, STAFF);

			GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);

			// when & then
			CustomException exception = assertThrows(CustomException.class, () ->
					reviewService.getReview(reviewId, differentStaffInfo));

			assertThat(exception.getMessage()).contains("비공개 처리된 리뷰입니다");
			verify(restaurantClient).getRestaurantStaffInfo(restaurantId);
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

			when(reservationClient.getReservation(serviceId, customerId)).thenReturn(serviceInfo);
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
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);

			// when
			GetReviewInfo result = reviewService.hideReview(reviewId, staffInfo);

			// then
			assertThat(result).isNotNull();
			assertThat(result.reviewUuid()).isEqualTo(reviewId);
			assertThat(result.isVisible()).isFalse();
			verify(restaurantClient).getRestaurantStaffInfo(restaurantId);
		}

		@Test
		void 레스토랑_주인이_요청시_리뷰를_숨길_수_있다() {
			// given
			GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);

			// when
			GetReviewInfo result = reviewService.hideReview(reviewId, ownerInfo);

			// then
			assertThat(result).isNotNull();
			assertThat(result.reviewUuid()).isEqualTo(reviewId);
			assertThat(result.isVisible()).isFalse();
			verify(restaurantClient).getRestaurantStaffInfo(restaurantId);
		}

		@Test
		void 다른_레스토랑_직원이_요청시_예외를_발생시킨다() {
			// given
			Long differentStaffId = 555L;
			CurrentUserInfoDto differentStaffInfo = new CurrentUserInfoDto(differentStaffId, STAFF);

			GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);

			// when & then
			CustomException exception = assertThrows(CustomException.class, () ->
					reviewService.hideReview(reviewId, differentStaffInfo));

			assertThat(exception.getMessage()).contains("수정 요청에 대한 권한이 없습니다.");
			verify(restaurantClient).getRestaurantStaffInfo(restaurantId);
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
		private String serviceId;
		private Long staffId;
		private Long ownerId;
		private CurrentUserInfoDto customerInfo;
		private CurrentUserInfoDto otherUserInfo;
		private CurrentUserInfoDto staffInfo;
		private CurrentUserInfoDto ownerInfo;
		private CurrentUserInfoDto masterInfo;

		@BeforeEach
		void setUp() {
			serviceId = UUID.randomUUID().toString();
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
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);

			// when
			GetReviewInfo result = reviewService.showReview(reviewId, staffInfo);

			// then
			assertThat(result).isNotNull();
			assertThat(result.reviewUuid()).isEqualTo(reviewId);
			assertThat(result.isVisible()).isTrue();
			verify(restaurantClient).getRestaurantStaffInfo(restaurantId);
		}

		@Test
		void 레스토랑_주인이_요청시_리뷰를_공개할_수_있다() {
			// given
			GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);

			// when
			GetReviewInfo result = reviewService.showReview(reviewId, ownerInfo);

			// then
			assertThat(result).isNotNull();
			assertThat(result.reviewUuid()).isEqualTo(reviewId);
			assertThat(result.isVisible()).isTrue();
			verify(restaurantClient).getRestaurantStaffInfo(restaurantId);
		}

		@Test
		void 다른_레스토랑_직원이_요청시_예외를_발생시킨다() {
			// given
			Long differentStaffId = 555L;
			CurrentUserInfoDto differentStaffInfo = new CurrentUserInfoDto(differentStaffId, STAFF);

			GetRestaurantStaffInfo staffInfoResponse = new GetRestaurantStaffInfo(staffId, ownerId);
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);

			// when & then
			CustomException exception = assertThrows(CustomException.class, () ->
					reviewService.showReview(reviewId, differentStaffInfo));

			assertThat(exception.getMessage()).contains("수정 요청에 대한 권한이 없습니다.");
			verify(restaurantClient).getRestaurantStaffInfo(restaurantId);
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
			when(restaurantClient.getRestaurantStaffInfo(restaurantId)).thenReturn(staffInfoResponse);
			reviewService.hideReview(reviewId, staffInfo);

			// when & then
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
					reviewService.showReview(reviewId, customerInfo));

			assertThat(exception.getMessage()).contains("관리자가 숨긴 리뷰는 일반 사용자가 공개할 수 없습니다");
		}
	}
}