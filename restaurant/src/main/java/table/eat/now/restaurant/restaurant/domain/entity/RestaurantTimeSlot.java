/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 08.
 */
package table.eat.now.restaurant.restaurant.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Entity
@Table(name = "p_restaurant_timeslot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantTimeSlot extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

  @Column(name = "restaurant_timeslot_uuid", nullable = false, unique = true)
  private String restaurantTimeslotUuid;

  @Column(name = "available_date", nullable = false)
  private LocalDate availableDate;

  @Column(name = "timeslot", nullable = false)
  private LocalTime timeslot;

  @Column(name = "max_capacity", nullable = false)
  private Integer maxCapacity;

  @Column(name = "cur_total_guest_count", nullable = false)
  private Integer curTotalGuestCount;

  @Builder(builderMethodName = "baseBuilder")
  private RestaurantTimeSlot(
      LocalDate availableDate,
      Integer maxCapacity,
      Restaurant restaurant,
      LocalTime timeslot
  ) {
    this.availableDate = availableDate;
    this.maxCapacity = maxCapacity;
    this.restaurant = restaurant;
    this.restaurantTimeslotUuid = UUID.randomUUID().toString();
    this.timeslot = timeslot;
    this.curTotalGuestCount = 0;
  }

  public void modifyRestaurant(Restaurant restaurant) {
    this.restaurant = restaurant;
  }

  public void modifyCurTotalGuestCount(int newCount) {
    if(newCount < 0) {
      throw new IllegalArgumentException("curTotalGuestCount 는 음수가 될 수 없습니다.");
    }
    if(newCount > maxCapacity) {
      throw new IllegalArgumentException("curTotalGuestCount 는 maxCapacity 보다 클 수 없습니다.");
    }
    this.curTotalGuestCount = newCount;
  }
}
