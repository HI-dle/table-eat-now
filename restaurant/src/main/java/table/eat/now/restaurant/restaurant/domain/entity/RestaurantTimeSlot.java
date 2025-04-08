/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 08.
 */
package table.eat.now.restaurant.restaurant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Entity
@Table(name = "p_restaurant_timeslot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantTimeSlot extends BaseEntity {

  @Id
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

  @Column(name = "restaurant_timeslot_uuid", nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
  private UUID restaurantTimeslotUuid;

  @Column(name = "available_date", nullable = false)
  private LocalDate availableDate;

  @Column(name = "timeslot", nullable = false)
  private LocalTime timeslot;

  @Column(name = "max_capacity", nullable = false)
  private Integer maxCapacity;

  @Column(name = "cur_total_guest_count", nullable = false)
  private Integer curTotalGuestCount;

  public void modifyRestaurant(Restaurant restaurant) {
    this.restaurant = restaurant;
  }
}
