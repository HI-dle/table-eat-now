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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import table.eat.now.common.domain.BaseEntity;
import table.eat.now.restaurant.restaurant.domain.entity.vo.ContactInfo;
import table.eat.now.restaurant.restaurant.domain.entity.vo.OperatingTime;

@Entity
@Table(name = "p_restaurant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "restaurant_uuid", nullable = false, unique = true, length = 100)
  private String restaurantUuid;

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
      Long ownerId,
      String name,
      String info,
      Integer maxReservationGuestCountPerTeamOnline,
      String contactNumber,
      String address,
      LocalTime openingAt,
      LocalTime closingAt
  ) {
    this.restaurantUuid = UUID.randomUUID().toString();
    this.ownerId = ownerId;
    this.name = name;
    this.info = info;
    this.maxReservationGuestCountPerTeamOnline = maxReservationGuestCountPerTeamOnline;
    this.contactInfo = ContactInfo.of(contactNumber, address);
    this.operatingTime = OperatingTime.of(openingAt, closingAt);
    this.status = RestaurantStatus.INACTIVE;
    this.waitingStatus = WaitingStatus.INACTIVE;
  }

  @Builder(builderMethodName = "fullBuilder")
  private Restaurant(ContactInfo contactInfo, Long id, String info,
      Integer maxReservationGuestCountPerTeamOnline, List<RestaurantMenu> menus, String name,
      OperatingTime operatingTime, Long ownerId, String restaurantUuid, BigDecimal reviewRatingAvg,
      Long staffId, RestaurantStatus status, List<RestaurantTimeSlot> timeSlots,
      WaitingStatus waitingStatus) {
    this.contactInfo = contactInfo;
    this.id = id;
    this.info = info;
    this.maxReservationGuestCountPerTeamOnline = maxReservationGuestCountPerTeamOnline;
    this.menus = menus;
    this.name = name;
    this.operatingTime = operatingTime;
    this.ownerId = ownerId;
    this.restaurantUuid = restaurantUuid;
    this.reviewRatingAvg = reviewRatingAvg;
    this.staffId = staffId;
    this.status = status;
    this.timeSlots = timeSlots;
    this.waitingStatus = waitingStatus;
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
