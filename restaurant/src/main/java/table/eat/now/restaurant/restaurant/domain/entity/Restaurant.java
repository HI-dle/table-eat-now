/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 08.
 */
package table.eat.now.restaurant.restaurant.domain.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

@Entity
@Table(name = "p_restaurant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseEntity {

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "restaurant_uuid", nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
  private UUID restaurantUuid;

  @Column(name = "owner_id", nullable = false)
  private Long ownerId;

  @Column(name = "staff_id")
  private Long staffId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "review_rating_avg")
  private BigDecimal reviewRatingAvg;

  @Column(name = "info", columnDefinition = "TEXT")
  private String info;

  @Column(name = "max_reservation_guest_count_per_team_online", nullable = false)
  private Integer maxReservationGuestCountPerTeamOnline;

  @Enumerated(EnumType.STRING)
  @Column(name = "waiting_status", nullable = false)
  private WaitingStatus waitingStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private RestaurantStatus status;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "contactNumber", column = @Column(name = "contact_number", length = 100)),
      @AttributeOverride(name = "address", column = @Column(name = "address", length = 200, nullable = false))
  })
  private ContactInfo contactInfo;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "openingAt", column = @Column(name = "opening_at")),
      @AttributeOverride(name = "closingAt", column = @Column(name = "closing_at"))
  })
  private OperatingTime operatingTime;

  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RestaurantMenu> menus = new ArrayList<>();

  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RestaurantTimeSlot> timeSlots = new ArrayList<>();

  @Builder(builderMethodName = "inactiveRestaurantBuilder")
  private Restaurant(
      UUID restaurantUuid,
      Long ownerId,
      String name,
      String info,
      Integer maxReservationGuestCountPerTeamOnline,
      ContactInfo contactInfo,
      OperatingTime operatingTime
  ) {
    this.restaurantUuid = restaurantUuid;
    this.ownerId = ownerId;
    this.name = name;
    this.info = info;
    this.maxReservationGuestCountPerTeamOnline = maxReservationGuestCountPerTeamOnline;
    this.contactInfo = contactInfo;
    this.operatingTime = operatingTime;
    this.status = RestaurantStatus.INACTIVE;
    this.waitingStatus = WaitingStatus.INACTIVE;
  }

  public void addMenus(List<RestaurantMenu> menus) {
    for (RestaurantMenu menu : menus) {
      addMenu(menu);
    }
  }

  public void addTimeSlots(List<RestaurantTimeSlot> timeSlots) {
    for (RestaurantTimeSlot timeSlot : timeSlots) {
      addTimeSlot(timeSlot);
    }
  }

  public void addMenu(RestaurantMenu menu) {
    this.menus.add(menu);
    menu.modifyRestaurant(this);
  }

  public void addTimeSlot(RestaurantTimeSlot timeSlot) {
    this.timeSlots.add(timeSlot);
    timeSlot.modifyRestaurant(this);
  }

  @Getter
  @RequiredArgsConstructor
  public enum RestaurantStatus {
    OPENED("운영중"),
    CLOSED("마감된"),
    BREAK("브레이크 타임"),
    HOLIDAY("휴일"),
    INACTIVE("비활성화"),
    ;
    private final String name;
  }

  @Getter
  @RequiredArgsConstructor
  public enum WaitingStatus {
    OPENED("운영중"),
    LIMITED("제한된"),
    INACTIVE("비활성화"),
    ;
    private final String name;
  }
}
