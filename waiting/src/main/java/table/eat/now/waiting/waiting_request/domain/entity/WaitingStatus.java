package table.eat.now.waiting.waiting_request.domain.entity;

import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum WaitingStatus {

  WAITING("대기중", 1, status -> true),
  POSTPONED("입장연기", 2, status -> status.order > 1 || status.order < 0),
  SEATED("착석", 3, status -> status.order > 3 || status.order < 0),
  LEAVED("식사종료", 4, status -> false),
  CANCELED("취소", -1, status -> false),
  NO_SHOW("노쇼", -2, status -> status.order > 1 || status.order == -1),
  ;

  private final String description;
  private final int order;
  private final Predicate<WaitingStatus> updateRule;

  public boolean isPossibleToUpdate(WaitingStatus status) {
    return updateRule.test(status);
  }
}
