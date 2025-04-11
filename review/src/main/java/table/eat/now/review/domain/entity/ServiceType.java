package table.eat.now.review.domain.entity;

public enum ServiceType {
  WAITING,
  RESERVATION,
  ;

  public static ServiceType from(String name) {
    try {
      return valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("유효하지 않은 서비스 타입 입니다: " + name);
    }
  }
}
