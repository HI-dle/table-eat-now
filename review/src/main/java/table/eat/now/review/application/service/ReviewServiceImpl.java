package table.eat.now.review.application.service;

import static table.eat.now.common.resolver.dto.UserRole.CUSTOMER;
import static table.eat.now.common.resolver.dto.UserRole.MASTER;
import static table.eat.now.common.resolver.dto.UserRole.OWNER;
import static table.eat.now.common.resolver.dto.UserRole.STAFF;
import static table.eat.now.review.application.exception.ReviewErrorCode.MODIFY_PERMISSION_DENIED;
import static table.eat.now.review.application.exception.ReviewErrorCode.REVIEW_ALREADY_EXISTS;
import static table.eat.now.review.application.exception.ReviewErrorCode.REVIEW_IS_INVISIBLE;
import static table.eat.now.review.application.exception.ReviewErrorCode.REVIEW_NOT_FOUND;
import static table.eat.now.review.application.exception.ReviewErrorCode.SERVICE_USER_MISMATCH;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.review.application.client.ReservationClient;
import table.eat.now.review.application.client.RestaurantClient;
import table.eat.now.review.application.client.WaitingClient;
import table.eat.now.review.application.event.RestaurantRatingUpdateEvent;
import table.eat.now.review.application.event.RestaurantRatingUpdatePayload;
import table.eat.now.review.application.event.ReviewEventPublisher;
import table.eat.now.review.application.service.dto.request.CreateReviewCommand;
import table.eat.now.review.application.service.dto.request.SearchAdminReviewQuery;
import table.eat.now.review.application.service.dto.request.SearchReviewQuery;
import table.eat.now.review.application.service.dto.request.UpdateReviewCommand;
import table.eat.now.review.application.service.dto.response.CreateReviewInfo;
import table.eat.now.review.application.service.dto.response.GetRestaurantStaffInfo;
import table.eat.now.review.application.service.dto.response.GetReviewInfo;
import table.eat.now.review.application.service.dto.response.GetServiceInfo;
import table.eat.now.review.application.service.dto.response.PaginatedInfo;
import table.eat.now.review.application.service.dto.response.SearchAdminReviewInfo;
import table.eat.now.review.application.service.dto.response.SearchReviewInfo;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.entity.ReviewReference;
import table.eat.now.review.domain.entity.ServiceType;
import table.eat.now.review.domain.repository.ReviewRepository;
import table.eat.now.review.domain.repository.search.RestaurantRatingResult;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final WaitingClient waitingClient;
  private final ReservationClient reservationClient;
  private final RestaurantClient restaurantClient;

  @Override
  public CreateReviewInfo createReview(CreateReviewCommand command) {
    validateReference(command.toReviewReference());

    return CreateReviewInfo.from(reviewRepository.save(command.toEntity()));
  }

  private void validateReference(ReviewReference reference) {
    validateServiceUser(reference);
    if (reviewRepository.existsByReferenceAndDeletedAtIsNull(reference)) {
      throw CustomException.from(REVIEW_ALREADY_EXISTS);
    }
  }

  private void validateServiceUser(ReviewReference reference) {
    GetServiceInfo serviceInfo = getServiceInfo(
        reference.getServiceType(), reference.getServiceId(), reference.getCustomerId()
    );

    if (!serviceInfo.customerId().equals(reference.getCustomerId())) {
      throw CustomException.from(SERVICE_USER_MISMATCH);
    }
  }

  private GetServiceInfo getServiceInfo(
      ServiceType serviceType, String serviceId, Long customerId) {
    return switch (serviceType) {
      case WAITING -> waitingClient.getWaiting(serviceId, customerId);
      case RESERVATION -> reservationClient.getReservation(serviceId, customerId);
    };
  }

  @Override
  @Transactional(readOnly = true)
  public GetReviewInfo getReview(String reviewId, CurrentUserInfoDto userInfo) {
    Review review = findReview(reviewId);
    validateAccess(review, userInfo.userId(), userInfo.role());
    return GetReviewInfo.from(review);
  }

  private Review findReview(String reviewId) {
    return reviewRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
        .orElseThrow(() -> CustomException.from(REVIEW_NOT_FOUND));
  }

  private void validateAccess(Review review, Long userId, UserRole role) {
    if (!review.isAccessible(userId, role.name())
        && !isRestaurantStaff(review.getRestaurantId(), userId, role)) {
      throw CustomException.from(REVIEW_IS_INVISIBLE);
    }
  }

  private boolean isRestaurantStaff(String restaurantId, Long currentUserId, UserRole role) {
    if (role != STAFF && role != OWNER) {
      return false;
    }
    GetRestaurantStaffInfo staffInfo = getStaffInfo(restaurantId);
    return staffInfo.staffId().equals(currentUserId) || staffInfo.ownerId().equals(currentUserId);
  }

  private GetRestaurantStaffInfo getStaffInfo(String restaurantId) {
    return restaurantClient.getRestaurantStaffInfo(restaurantId);
  }

  @Override
  @Transactional
  public GetReviewInfo hideReview(String reviewId, CurrentUserInfoDto userInfo) {
    Review review = findReview(reviewId);
    validateModify(userInfo, review);
    return GetReviewInfo.from(review.hide(userInfo.userId(), userInfo.role().name()));
  }

  private void validateModify(CurrentUserInfoDto userInfo, Review review) {
    if (!isCustomer(userInfo) && !isAdmin(userInfo, review.getRestaurantId())) {
      throw CustomException.from(MODIFY_PERMISSION_DENIED);
    }
  }

  private boolean isCustomer(CurrentUserInfoDto userInfo) {
    return userInfo.role().equals(CUSTOMER);
  }

  private boolean isAdmin(CurrentUserInfoDto userInfo, String restaurantId) {
    return isRestaurantStaff(restaurantId, userInfo.userId(), userInfo.role())
        || userInfo.role().equals(MASTER);
  }

  @Override
  @Transactional
  public GetReviewInfo showReview(String reviewId, CurrentUserInfoDto userInfo) {
    Review review = findReview(reviewId);
    validateModify(userInfo, review);
    return GetReviewInfo.from(review.show(userInfo.userId(), userInfo.role().name()));
  }

  @Override
  @Transactional
  public GetReviewInfo updateReview(String reviewId, UpdateReviewCommand command) {
    Review review = findReview(reviewId);
    validateModify(command.userInfo(), review);
    return GetReviewInfo.from(review.update(command.toEntity()));
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedInfo<SearchReviewInfo> searchReviews(
      SearchReviewQuery query, CurrentUserInfoDto userInfo) {

    return PaginatedInfo.fromResult(
        reviewRepository.searchReviews(query.toCriteria(userInfo.userId())));
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedInfo<SearchAdminReviewInfo> searchAdminReviews(
      SearchAdminReviewQuery query, CurrentUserInfoDto userInfo) {

    String accessibleRestaurantId =
        isStaff(userInfo.role()) ? getRestaurantId(userInfo.userId()) : null;

    return PaginatedInfo.fromAdminResult(
        reviewRepository.searchAdminReviews(
            query.toCriteria(accessibleRestaurantId, isMaster(userInfo.role()))));
  }

  private boolean isStaff(UserRole role) {
    return role == OWNER || role == STAFF;
  }

  private String getRestaurantId(Long staffId) {
    return restaurantClient.getRestaurantInfo(staffId).restaurantId();
  }

  private boolean isMaster(UserRole role) {
    return role == MASTER;
  }

  @Override
  @Transactional
  public void deleteReview(String reviewId, CurrentUserInfoDto userInfo) {
    findReview(reviewId)
        .delete(userInfo.userId(), userInfo.role().name());
  }
}
