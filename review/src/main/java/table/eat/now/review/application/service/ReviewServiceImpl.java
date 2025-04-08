package table.eat.now.review.application.service;

import static table.eat.now.review.application.service.dto.exception.ReviewErrorCode.SERVICE_USER_MISMATCH;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.common.exception.CustomException;
import table.eat.now.review.application.client.ReservationClient;
import table.eat.now.review.application.client.WaitingClient;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.application.service.dto.response.GetServiceInfo;
import table.eat.now.review.domain.entity.ServiceType;
import table.eat.now.review.domain.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final WaitingClient waitingClient;
	private final ReservationClient reservationClient;

	@Override
	public CreateReviewInfo createReview(CreateReviewCommand command) {
		validateUser(command);
		return CreateReviewInfo.from(reviewRepository.save(command.toEntity()));
	}

	private void validateUser(CreateReviewCommand command) {
		GetServiceInfo serviceInfo = getServiceInfo(ServiceType.from(
				command.serviceType()), command.serviceId(), command.customerId());

		if(!serviceInfo.customerId().equals(command.customerId())) {
			throw CustomException.from(SERVICE_USER_MISMATCH);
		}
	}

	private GetServiceInfo getServiceInfo(ServiceType serviceType, UUID serviceId, Long customerId) {
		return switch (serviceType) {
			case WAITING -> waitingClient.getWaiting(serviceId, customerId);
			case RESERVATION -> reservationClient.getReservation(serviceId, customerId);
		};
	}
}
