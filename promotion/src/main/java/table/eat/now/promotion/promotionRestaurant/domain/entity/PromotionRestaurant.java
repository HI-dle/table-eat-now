package table.eat.now.promotion.promotionRestaurant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Entity
@Table(name = "p_promotion_restaurant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionRestaurant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, unique = true, length = 100, name = "promotion_restaurant_uuid")
  private String promotionRestaurantUuid;

  @Column(nullable = false, columnDefinition = "VARCHAR(100)")
  private String promotionUuid;

  @Column(nullable = false, columnDefinition = "VARCHAR(100)")
  private String restaurantUuid;

  private PromotionRestaurant(String promotionUuid,
      String restaurantUuid) {
    this.promotionRestaurantUuid = UUID.randomUUID().toString();
    this.promotionUuid = promotionUuid;
    this.restaurantUuid = restaurantUuid;
  }

  public static PromotionRestaurant of(String promotionUuid, String restaurantUuid) {
    return new PromotionRestaurant(
        promotionUuid,
        restaurantUuid);
  }

  public void modifyPromotionRestaurant(String promotionUuid, String restaurantUuid) {
    this.promotionUuid = promotionUuid;
    this.restaurantUuid = restaurantUuid;
  }
}
