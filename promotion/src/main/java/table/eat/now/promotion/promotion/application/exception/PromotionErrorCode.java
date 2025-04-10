package table.eat.now.promotion.promotion.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 10.
 */
@Getter
@RequiredArgsConstructor
public enum PromotionErrorCode implements ErrorCode {

  INVALID_PROMOTION_UUID(
      "존재하지 않는 프로모션 입니다.", HttpStatus.NOT_FOUND),
  ;

  private final String message;
  private final HttpStatus status;
}
