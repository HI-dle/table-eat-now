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
  RESTAURANT_NOT_FOUND("식당을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ;
  private final String message;
  private final HttpStatus status;
}
