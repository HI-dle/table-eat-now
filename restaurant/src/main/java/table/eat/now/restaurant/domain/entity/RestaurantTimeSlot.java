package table.eat.now.restaurant.domain.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Entity
@Table(name = "p_restaurant_timeslot")
@Getter
@NoArgsConstructor
public class RestaurantTimeSlot extends BaseEntity {

  @Id
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

  @Column(name = "restaurant_timeslot_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID restaurantTimeslotUuid;

  @Column(nullable = false)
  private LocalDate availableDate;

  @Column(nullable = false)
  private LocalTime timeslot;

  @Column(nullable = false)
  private Integer maxCapacity;

  private Integer curTotalGuestCount;
}
