package table.eat.now.waiting.waiting_request.application.service;

import org.springframework.data.domain.Pageable;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.waiting.waiting_request.application.dto.request.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.dto.response.GetWaitingRequestInfo;
import table.eat.now.waiting.waiting_request.application.dto.response.PageResult;

public interface WaitingRequestService {

  String createWaitingRequest(CurrentUserInfoDto userInfo, CreateWaitingRequestCommand command);

  void processWaitingRequestEntrance(
      CurrentUserInfoDto userInfo, String waitingRequestsUuid);

  GetWaitingRequestInfo getWaitingRequest(
      CurrentUserInfoDto userInfo, String waitingRequestUuid, String phone);

  GetWaitingRequestInfo getWaitingRequestAdmin(CurrentUserInfoDto userInfo, String string);

  void postponeWaitingRequest(CurrentUserInfoDto userInfo, String waitingRequestUuid, String phone);

  PageResult<GetWaitingRequestInfo> getWaitingRequestsAdmin(CurrentUserInfoDto userInfo, String dailyWaitingUuid, Pageable pageable);
}
