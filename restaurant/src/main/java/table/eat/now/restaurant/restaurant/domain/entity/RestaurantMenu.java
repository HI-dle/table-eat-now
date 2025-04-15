/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 08.
 */
package table.eat.now.restaurant.restaurant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Entity
@Table(name = "p_restaurant_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantMenu extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

  @Column(name = "restaurant_menu_uuid", nullable = false, unique = true, length = 100)
  private String restaurantMenuUuid;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "price")
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private MenuStatus status;

  @Builder(builderMethodName = "inactiveMenuBuilder")
  private RestaurantMenu(
      String name,
      BigDecimal price,
      Restaurant restaurant
  ) {
    this.name = name;
    this.price = price;
    this.restaurant = restaurant;
    this.restaurantMenuUuid = UUID.randomUUID().toString();
    this.status = MenuStatus.INACTIVE;
  }

  public void modifyRestaurant(Restaurant restaurant) {
    this.restaurant = restaurant;
  }

  @Getter
  @RequiredArgsConstructor
  public enum MenuStatus {
    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    SOLDOUT("매진"),
    ;
    private final String name;
  }
}

