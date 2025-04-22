package table.eat.now.waiting.waiting_request.application.usecase.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.waiting.waiting_request.application.messaging.EventPublisher;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestCreatedEvent;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestCreatedInfo;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestEntranceEvent;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestEntranceInfo;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestEvent;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestPostponedEvent;
import table.eat.now.waiting.waiting_request.application.messaging.dto.WaitingRequestPostponedInfo;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;

@Component
@RequiredArgsConstructor
public class WaitingRequestNotifyHandler {

  private final EventPublisher<WaitingRequestEvent> eventPublisher;

  public void notifyCreated(
      CreateWaitingRequestCommand command, String waitingRequestUuid,
      String restaurantName, Long sequence, Long rank, Long estimatedWaitingMin) {

    WaitingRequestCreatedInfo createdInfo = WaitingRequestCreatedInfo.of(
        waitingRequestUuid, command.phone(), command.slackId(), restaurantName, sequence, rank, estimatedWaitingMin);

    eventPublisher.publish(WaitingRequestCreatedEvent.from(createdInfo));
  }

  public void notifyPostponed(
      WaitingRequest waitingRequest, String restaurantName, Long rank, Long estimatedWaitingMin) {

    WaitingRequestPostponedInfo postponedInfo = WaitingRequestPostponedInfo.of(
        waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone(), waitingRequest.getSlackId(),
        restaurantName, waitingRequest.getSequence().longValue(), rank, estimatedWaitingMin);

    eventPublisher.publish(WaitingRequestPostponedEvent.from(postponedInfo));
  }

  public void notifyEntrance(WaitingRequest waitingRequest, String restaurantName) {

    WaitingRequestEntranceInfo entranceInfo = WaitingRequestEntranceInfo.of(
        waitingRequest.getWaitingRequestUuid(), waitingRequest.getPhone(), waitingRequest.getSlackId(),
        restaurantName, waitingRequest.getSequence().longValue());

    eventPublisher.publish(WaitingRequestEntranceEvent.from(entranceInfo));
  }
}
