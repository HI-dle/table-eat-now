package table.eat.now.waiting.waiting_request.application.service;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.waiting.waiting_request.application.dto.request.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.dto.request.EnterWaitingRequestCommand;

public interface WaitingRequestService {

  String createWaitingRequest(CurrentUserInfoDto userInfo, CreateWaitingRequestCommand command);

  void processWaitingRequestEntrance(
      CurrentUserInfoDto userInfo, String waitingRequestsUuid, EnterWaitingRequestCommand command);
}
