package table.eat.now.promotion.promotionUser.application.exception;

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
public enum PromotionUserErrorCode implements ErrorCode {

  INVALID_PROMOTION_USER_UUID(
      "프로모션에 참여하지 않는 사용자 입니다.", HttpStatus.NOT_FOUND),
  ;

  private final String message;
  private final HttpStatus status;
}
