package table.eat.now.review.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class ReviewReference {

  @Column(name = "restaurant_uuid", nullable = false)
  private String restaurantId;

  @Column(name = "service_uuid", nullable = false)
  private String serviceId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ServiceType serviceType;

  public static ReviewReference create(
      String restaurantId, String serviceId, Long customerId, ServiceType serviceType) {

    validateNull(restaurantId, serviceId, customerId, serviceType);
    return new ReviewReference(restaurantId, serviceId, customerId, serviceType);
  }

  private static void validateNull(
      String restaurantId, String serviceId, Long customerId, ServiceType serviceType) {

    if (restaurantId == null || serviceId == null || customerId == null || serviceType == null) {
      throw new IllegalArgumentException("null이 될 수 없습니다.");
    }
  }

  private ReviewReference(
      String restaurantId, String serviceId, Long customerId, ServiceType serviceType) {

    this.restaurantId = restaurantId;
    this.serviceId = serviceId;
    this.customerId = customerId;
    this.serviceType = serviceType;
  }

  protected ReviewReference() {
  }
}
