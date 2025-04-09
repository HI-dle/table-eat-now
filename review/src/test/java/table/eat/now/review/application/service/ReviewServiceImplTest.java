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
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.review.application.client.ReservationClient;
import table.eat.now.review.application.client.WaitingClient;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.application.service.dto.response.GetServiceInfo;
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

	private UUID restaurantId;
	private UUID serviceId;
	private Long customerId;
	private CreateReviewCommand command;
	private GetServiceInfo serviceInfo;

	@BeforeEach
	void setUp() {
		restaurantId = UUID.randomUUID();
		serviceId = UUID.randomUUID();
		customerId = 123L;

		command = new CreateReviewCommand(
				restaurantId, serviceId, customerId, "RESERVATION",
				"맛있는 식당이었습니다.", 4, true, UserRole.CUSTOMER);

		serviceInfo = new GetServiceInfo(restaurantId, customerId);
	}

	@Nested
	class createReview_는 {

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
}