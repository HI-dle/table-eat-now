package table.eat.now.review.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import table.eat.now.common.exception.type.ErrorCode;

@Getter
public enum ReviewErrorCode implements ErrorCode {

  REVIEW_NOT_FOUND("해당 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  SERVICE_USER_MISMATCH("서비스 정보와 사용자 정보가 일치하지 않습니다.", HttpStatus.FORBIDDEN),
  REVIEW_IS_INVISIBLE("비공개 처리된 리뷰입니다.", HttpStatus.FORBIDDEN),
  MODIFY_PERMISSION_DENIED("수정 요청에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
  ;

  private final String message;
  private final HttpStatus status;

  ReviewErrorCode(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
}
