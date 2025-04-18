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
  MAX_CAPACITY_CANNOT_BE_LESS_THAN_CURRENT("현재 예약 인원보다 수용 인원을 작게 설정할 수 없습니다.", HttpStatus.BAD_REQUEST),

  // NOT_FOUNT
  NOT_FOUND("식당 타임 슬롯을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  // CONFLICT
  CANNOT_DELETE_RESERVED_TIMESLOT("예약 인원이 존재하는 타임 슬롯은 삭제할 수 없습니다.", HttpStatus.CONFLICT),
  CANNOT_MODIFY_DATETIME_WHEN_RESERVED_TIMESLOT("예약 인원이 있는 타임 슬롯의 날짜나 시간은 수정할 수 없습니다.", HttpStatus.CONFLICT),
  ;

  private final String message;
  private final HttpStatus status;
}
