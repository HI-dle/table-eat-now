package table.eat.now.coupon.coupon.application.service.strategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum IssueStrategyAlias {
  GENERAL_BASE("기본 발급"),
  GENERAL_LIMITED("기본 한정 수량 발급"),
  GENERAL_NONDUP("기본 중복 제한 발급"),
  GENERAL_LIMITED_NONDUP("기본 한정 수량 및 중복 제한 발급"),
  HOT_LIMITED("핫딜 한정 수량 발급"),
  HOT_NONDUP("핫딜 중복 제한 발급"),
  HOT_LIMITED_NONDUP("핫딜 한정 수량 및 중복 제한 발급"),
  SYSTEM_NONDUP("시스템 중복 제한 발급"),
  ;

  private final String description;
}
