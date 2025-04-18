/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 15.
 */
package table.eat.now.restaurant.restaurant.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum RestaurantErrorCode implements ErrorCode {
  /**
   * NOT_FOUND
   */
  RESTAURANT_NOT_FOUND("식당을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  /**
   * FORBIDDEN
   */
  NO_MODIFY_PERMISSION("해당 식당에 대한 수정 권한이 없습니다.",HttpStatus.FORBIDDEN),
  ;

  private final String message;
  private final HttpStatus status;
}
