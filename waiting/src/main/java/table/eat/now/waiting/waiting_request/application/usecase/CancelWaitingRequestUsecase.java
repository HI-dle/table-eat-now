package table.eat.now.waiting.waiting_request.application.usecase;

import static table.eat.now.waiting.waiting_request.application.usecase.utils.WaitingRequestValidator.validateUserPhoneNumber;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.CancelWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.Command;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingStatus;
import table.eat.now.waiting.waiting_request.domain.reader.WaitingRequestReader;
import table.eat.now.waiting.waiting_request.domain.store.WaitingRequestStore;

@RequiredArgsConstructor
@Service
public class CancelWaitingRequestUsecase implements CommandUsecase<CancelWaitingRequestCommand, Void> {

  private final WaitingRequestReader reader;
  private final WaitingRequestStore store;

  @Override
  public Class<? extends Command> getCommandClass() {
    return CancelWaitingRequestCommand.class;
  }

  @Override
  @Transactional
  public Void execute(CancelWaitingRequestCommand command) {

    WaitingRequest waitingRequest = reader.getWaitingRequestBy(command.waitingRequestUuid());
    validateUserPhoneNumber(command.phone(), waitingRequest.getPhone());

    waitingRequest.updateStatus(WaitingStatus.CANCELED);

    store.dequeueWaitingRequest(waitingRequest.getDailyWaitingUuid(), command.waitingRequestUuid());
    return null;
  }
}
