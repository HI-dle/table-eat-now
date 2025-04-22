package table.eat.now.waiting.waiting_request.application.usecase.utils;

import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;

public class WaitingRequestValidator {

  public static void validateWaitingAvailable(GetDailyWaitingInfo dailyWaitingInfo) {
    if (!dailyWaitingInfo.isAvailable()) {
      throw CustomException.from(WaitingRequestErrorCode.UNAVAILABLE_WAITING);
    }
  }

  public static void validateNoDuplicateWaitingRequest(boolean existsBy) {
    if (existsBy) {
      throw CustomException.from(WaitingRequestErrorCode.ALREADY_EXISTS_WAITING);
    }
  }

  public static void validateUserPhoneNumber(String phone, String savedPhone) {
    if (!savedPhone.equals(phone)) {
      throw CustomException.from(WaitingRequestErrorCode.UNAUTH_REQUEST);
    }
  }

  public static void validateCustomerUserId(Long userId, UserRole userRole, Long registeredUserId) {
    if (userRole.isMaster()) {
      return;
    }
    if (!isSameCustomer(userId, userRole, registeredUserId)) {
      throw CustomException.from(WaitingRequestErrorCode.UNAUTH_REQUEST);
    }
  }

  public static void validateRestaurantAuthority(
      Long userId, UserRole userRole, GetRestaurantInfo restaurantInfo) {

    if (userRole.isMaster()) {
      return;
    }
    if (!isOwnerOfRestaurant(userId, userRole, restaurantInfo.ownerId())
        && !isStaffOfRestaurant(userId, userRole, restaurantInfo.staffId())) {
      throw CustomException.from(WaitingRequestErrorCode.UNAUTH_REQUEST);
    }
  }

  private static boolean isSameCustomer(Long userId, UserRole userRole, Long registeredUserId) {
    return userRole.isCustomer() && userId.equals(registeredUserId);
  }

  private static boolean isStaffOfRestaurant(Long userId, UserRole userRole, Long staffId) {
    return userRole.isStaff() && staffId.equals(userId);
  }

  private static boolean isOwnerOfRestaurant(Long userId, UserRole userRole, Long ownerId) {
    return userRole.isOwner() && ownerId.equals(userId);
  }
}
