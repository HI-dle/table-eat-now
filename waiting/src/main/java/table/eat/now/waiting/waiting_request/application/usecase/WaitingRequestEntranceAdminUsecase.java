package table.eat.now.waiting.waiting_request.application.usecase;

import static table.eat.now.waiting.waiting_request.application.usecase.utils.WaitingRequestValidator.validateRestaurantAuthority;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.Command;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.WaitingRequestEntranceAdminCommand;
import table.eat.now.waiting.waiting_request.application.usecase.handler.WaitingRequestNotifyHandler;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;

@RequiredArgsConstructor
@Service
public class WaitingRequestEntranceAdminUsecase
    implements CommandUsecase<WaitingRequestEntranceAdminCommand, Void> {

  private final WaitingRequestNotifyHandler notifyHandler;
  private final RestaurantClient restaurantClient;
  private final WaitingRequestReader reader;
  private final WaitingRequestStore store;

  @Override
  public Class<? extends Command> getCommandClass() {
    return WaitingRequestEntranceAdminCommand.class;
  }

  @Override
  public Void execute(WaitingRequestEntranceAdminCommand command) {

    WaitingRequest waitingRequest = reader.getWaitingRequestBy(command.waitingRequestUuid());
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(waitingRequest.getRestaurantUuid());

    validateRestaurantAuthority(command.userId(), command.userRole(), restaurantInfo);

    store.dequeueWaitingRequest(waitingRequest.getDailyWaitingUuid(), command.waitingRequestUuid());

    notifyHandler.notifyEntrance(waitingRequest, restaurantInfo.name());
    return null;
  }
}
