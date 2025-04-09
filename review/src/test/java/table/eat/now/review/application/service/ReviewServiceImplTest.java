package table.eat.now.review.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import table.eat.now.common.resolver.dto.UserRole;
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
	public class createReview_는 {

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
					"맛있는 식당이었습니다.", 4, true, UserRole.CUSTOMER);

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
					"맛있는 식당이었습니다.", 4, true, UserRole.CUSTOMER);
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

			when(reservationClient.getReservation(serviceId, customerId)).thenReturn(differentServiceInfo);

			// when & then
			CustomException exception = assertThrows(CustomException.class, () ->
					reviewService.createReview(command));

			assertThat(exception.getMessage()).isEqualTo("서비스 정보와 사용자 정보가 일치하지 않습니다.");
		}
	}

	@Nested
	public class getReview_는 {

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

			customerInfo = new CurrentUserInfoDto(customerId, UserRole.CUSTOMER);
			otherUserInfo = new CurrentUserInfoDto(otherUserId, UserRole.CUSTOMER);
			staffInfo = new CurrentUserInfoDto(staffId, UserRole.STAFF);
			ownerInfo = new CurrentUserInfoDto(ownerId, UserRole.OWNER);
			serviceInfo = new GetServiceInfo(restaurantId, customerId);

			CreateReviewCommand command = new CreateReviewCommand(
					restaurantId, serviceId, customerId, "RESERVATION",
					"맛있는 식당이었습니다.", 4,
					false,
					UserRole.CUSTOMER
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
			CurrentUserInfoDto differentStaffInfo = new CurrentUserInfoDto(differentStaffId, UserRole.STAFF);

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
					UserRole.CUSTOMER
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
			CurrentUserInfoDto masterInfo = new CurrentUserInfoDto(otherUserId, UserRole.MASTER);

			// when
			GetReviewInfo result = reviewService.getReview(reviewId, masterInfo);

			// then
			assertThat(result).isNotNull();
			assertThat(result.reviewUuid()).isEqualTo(reviewId);
		}
	}

}