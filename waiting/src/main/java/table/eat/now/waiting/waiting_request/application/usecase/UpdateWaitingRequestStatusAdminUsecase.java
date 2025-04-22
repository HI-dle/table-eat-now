package table.eat.now.waiting.waiting_request.application.usecase;

import static table.eat.now.waiting.waiting_request.application.usecase.utils.WaitingRequestValidator.validateRestaurantAuthority;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.waiting.waiting_request.application.client.RestaurantClient;
import table.eat.now.waiting.waiting_request.application.client.dto.response.GetRestaurantInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.Command;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.UpdateWaitingRequestStatusAdminCommand;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;

@RequiredArgsConstructor
@Service
public class UpdateWaitingRequestStatusAdminUsecase
    implements CommandUsecase<UpdateWaitingRequestStatusAdminCommand, Void> {

  private final RestaurantClient restaurantClient;
  private final WaitingRequestReader reader;

  @Override
  public Class<? extends Command> getCommandClass() {
    return UpdateWaitingRequestStatusAdminCommand.class;
  }

  @Override
  @Transactional
  public Void execute(UpdateWaitingRequestStatusAdminCommand command) {

    WaitingRequest waitingRequest = reader.getWaitingRequestBy(command.waitingRequestUuid());
    GetRestaurantInfo restaurantInfo = restaurantClient.getRestaurantInfo(waitingRequest.getRestaurantUuid());

    validateRestaurantAuthority(command.userId(), command.userRole(), restaurantInfo);

    waitingRequest.updateStatus(command.type());
    return null;
  }

}
