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
public enum RestaurantTimeSlotErrorCode implements ErrorCode {

  // BAD_REQUEST
  EXCEEDS_CAPACITY("식당 타임 슬롯의 자리가 부족합니다.", HttpStatus.BAD_REQUEST),

  // NOT_FOUNT
  NOT_FOUND("식당 타임 슬롯을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ;
  private final String message;
  private final HttpStatus status;
}
