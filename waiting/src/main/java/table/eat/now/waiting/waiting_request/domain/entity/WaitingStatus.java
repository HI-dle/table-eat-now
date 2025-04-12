package table.eat.now.waiting.waiting_request.domain.entity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum WaitingStatus {

  WAITING("대기중"),
  POSTPONED("입장연기"),
  SEATED("착석"),
  CANCELED("취소"),
  LEAVED("식사종료"),
  NO_SHOW("노쇼"),
  ;

  private final String description;
}
