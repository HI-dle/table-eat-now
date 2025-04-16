package table.eat.now.common.resolver.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
  MASTER("마스터 관리자"),
  OWNER("식당 주인"),
  STAFF("식당 직원"),
  CUSTOMER("고객");

  private final String description;

  public boolean isRestaurantStaff() {
    return this == STAFF || this == OWNER;
  }

  public boolean isOwner() {
    return this == OWNER;
  }

  public boolean isStaff() {
    return this == STAFF;
  }

  public boolean isMaster() {
    return this == MASTER;
  }

  public boolean isCustomer() {
    return this == CUSTOMER;
  }
}
