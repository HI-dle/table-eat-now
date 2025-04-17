/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 09.
 */
package table.eat.now.restaurant.restaurant.application.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.restaurant.restaurant.application.exception.RestaurantErrorCode;
import table.eat.now.restaurant.restaurant.application.exception.RestaurantTimeSlotErrorCode;
import table.eat.now.restaurant.restaurant.application.service.dto.request.CreateRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.request.GetRestaurantCriteria;
import table.eat.now.restaurant.restaurant.application.service.dto.request.ModifyRestaurantCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.request.ModifyRestaurantCommand.MenuCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.request.ModifyRestaurantCommand.TimeSlotCommand;
import table.eat.now.restaurant.restaurant.application.service.dto.response.CreateRestaurantInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.GetRestaurantInfo;
import table.eat.now.restaurant.restaurant.application.service.dto.response.ModifyRestaurantInfo;
import table.eat.now.restaurant.restaurant.domain.entity.Restaurant;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantMenu;
import table.eat.now.restaurant.restaurant.domain.entity.RestaurantTimeSlot;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantRepository;
import table.eat.now.restaurant.restaurant.domain.repository.RestaurantTimeSlotRepository;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

  private final RestaurantRepository restaurantRepository;

  private final RestaurantTimeSlotRepository restaurantTimeSlotRepository;

  @Override
  public CreateRestaurantInfo createRestaurant(CreateRestaurantCommand command) {
    return CreateRestaurantInfo.from(restaurantRepository.save(command.toEntity()));
  }

  @Override
  @Transactional(readOnly = true)
  public GetRestaurantInfo getRestaurant(GetRestaurantCriteria criteria) {
    boolean includeDeleted = false;
    boolean includeInactive = false;

    switch (criteria.role()) {
      case MASTER -> {
        includeDeleted = true;
        includeInactive = true;
      }
      case OWNER -> {
        boolean isOwner = restaurantRepository.isOwner(criteria.userId(),
            criteria.restaurantUuid());
        if (isOwner) {
          includeDeleted = true;
          includeInactive = true;
        }
      }
      case STAFF -> {
        boolean isStaff = restaurantRepository.isStaff(criteria.userId(),
            criteria.restaurantUuid());
        if (isStaff) {
          includeInactive = true;
        }
      }
    }

    Restaurant restaurant = restaurantRepository.findByDynamicCondition(
        criteria.restaurantUuid(), includeDeleted, includeInactive
    ).orElseThrow(() -> CustomException.from(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

    return GetRestaurantInfo.from(restaurant);
  }

  @Override
  @Transactional
  public void increaseOrDecreaseTimeSlotGuestCount(String restaurantTimeSlotUuid, int delta) {
    RestaurantTimeSlot timeSlot = restaurantTimeSlotRepository
        .findWithLockByRestaurantTimeslotUuid(restaurantTimeSlotUuid)
        .orElseThrow(() -> new CustomException(RestaurantTimeSlotErrorCode.NOT_FOUND));

    int newCount = timeSlot.getCurTotalGuestCount() + delta;

    if (newCount < 0 || newCount > timeSlot.getMaxCapacity()) {
      throw new CustomException(RestaurantTimeSlotErrorCode.EXCEEDS_CAPACITY);
    }

    timeSlot.modifyCurTotalGuestCount(newCount);
  }

  @Override
  @Transactional
  public ModifyRestaurantInfo modifyRestaurant(ModifyRestaurantCommand command) {
    Restaurant restaurant = getRestaurantByRestaurantUuidWithMenusAndTimeslots(
        command.restaurantUuid());

    // 레스토랑 정보 수정
    restaurant.modify(
        command.name(),
        command.info(),
        command.maxReservationGuestCountPerTeamOnline(),
        command.waitingStatus(),
        command.status(),
        command.contactNumber(),
        command.address(),
        command.openingAt(),
        command.closingAt()
    );

    modifyMenus(command, restaurant);
    modifyTimeSlots(command, restaurant);

    return ModifyRestaurantInfo.builder()
        .restaurantUuid(command.restaurantUuid())
        .name(command.name())
        .build();
  }

  private static void modifyMenus(ModifyRestaurantCommand command, Restaurant restaurant) {
    // 메뉴 수정 처리
    Map<String, MenuCommand> menuMap = command.menus().stream()
        .collect(Collectors.toMap(
            m -> Optional.ofNullable(m.restaurantMenuUuid()).orElse(UUID.randomUUID().toString()),
            m -> m
        ));

    Long requesterId = command.requesterId();
    for(RestaurantMenu menu :  new ArrayList<>(restaurant.getMenus())) {
      String restaurantMenuUuid = menu.getRestaurantMenuUuid();
      if(!menuMap.containsKey(restaurantMenuUuid)) {
        menu.delete(requesterId);
        continue;
      }
      MenuCommand menuCommand = menuMap.get(restaurantMenuUuid);
      menu.modify(menuCommand.name(), menuCommand.price(), menuCommand.status());
      menuMap.remove(restaurantMenuUuid);
    }

    for(Map.Entry<String, MenuCommand> entry : menuMap.entrySet()){
      MenuCommand requestedNewMenu = entry.getValue();
      String newRestaurantMenuUuid = entry.getKey();
      restaurant.addMenu(requestedNewMenu.toNewEntity(newRestaurantMenuUuid));
    }
  }

  private static void modifyTimeSlots(ModifyRestaurantCommand command, Restaurant restaurant) {
    // 타임슬롯 수정 처리
    //    제약사항 좀 더 필요할수도..
    //    command.timeslots().stream()
    //        .filter(ts -> ts.availableDate().isBefore(LocalDate.now().plusDays(101)));
    Map<String, TimeSlotCommand> timeSlotMap = command.timeslots().stream()
        .collect(Collectors.toMap(
            t -> Optional.ofNullable(t.restaurantTimeslotUuid()).orElse(UUID.randomUUID().toString()),
            t -> t
        ));

    Long requesterId = command.requesterId();
    for (RestaurantTimeSlot timeSlot : new ArrayList<>(restaurant.getTimeSlots())) {
      String timeSlotUuid = timeSlot.getRestaurantTimeslotUuid();
      if (!timeSlotMap.containsKey(timeSlotUuid)) {
        if (timeSlot.getCurTotalGuestCount() > 0) {
          throw new IllegalStateException("예약 인원이 존재하는 타임슬롯은 삭제할 수 없습니다.");
        }
        timeSlot.delete(requesterId);
        continue;
      }
      TimeSlotCommand timeSlotCommand = timeSlotMap.get(timeSlotUuid);

      if (timeSlot.getCurTotalGuestCount() > 0) {
        throw new IllegalStateException("예약 인원이 있는 타임슬롯은 수정할 수 없습니다.");
      }

      if (timeSlotCommand.maxCapacity() < timeSlot.getCurTotalGuestCount()) {
        throw new IllegalArgumentException("현재 예약 인원보다 수용 인원을 작게 설정할 수 없습니다.");
      }

      timeSlot.modify(
          timeSlotCommand.availableDate(),
          timeSlotCommand.timeslot(),
          timeSlotCommand.maxCapacity());
      timeSlotMap.remove(timeSlotUuid);
    }

    for (Map.Entry<String, TimeSlotCommand> entry : timeSlotMap.entrySet()) {
      TimeSlotCommand newTimeSlot = entry.getValue();
      String newTimeSlotUuid = entry.getKey();
      RestaurantTimeSlot newSlot = newTimeSlot.toNewEntity(newTimeSlotUuid);

      restaurant.addTimeSlot(newSlot);
    }
  }

  private Restaurant getRestaurantByRestaurantUuidWithMenusAndTimeslots(String restaurantUuid) {
    return restaurantRepository.findByRestaurantUuidWithMenusAndTimeslots(restaurantUuid)
        .orElseThrow(() -> CustomException.from(RestaurantErrorCode.RESTAURANT_NOT_FOUND));
  }

}
