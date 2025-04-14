package table.eat.now.waiting.waiting_request.application.service;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.waiting.waiting_request.application.dto.request.CreateWaitingRequestCommand;

public interface WaitingRequestService {

  String createWaitingRequest(CurrentUserInfoDto userInfo, CreateWaitingRequestCommand command);
}
