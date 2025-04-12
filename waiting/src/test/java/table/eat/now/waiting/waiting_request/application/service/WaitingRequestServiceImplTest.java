package table.eat.now.waiting.waiting_request.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting_request.application.dto.request.CreateWaitingRequestCommand;
import table.eat.now.waiting.waiting_request.application.exception.WaitingRequestErrorCode;
import table.eat.now.waiting.waiting_request.domain.entity.WaitingRequest;
import table.eat.now.waiting.waiting_request.domain.repository.WaitingRequestRepository;
import table.eat.now.waiting.waiting_request.fixture.WaitingRequestFixture;

class WaitingRequestServiceImplTest extends IntegrationTestSupport {

  @Autowired
  private WaitingRequestService waitingRequestService;

  @Autowired
  private WaitingRequestRepository waitingRequestRepository;

  private WaitingRequest waitingRequest;

  @BeforeEach
  void setUp() {
    waitingRequest = WaitingRequestFixture.create(UUID.randomUUID().toString(), "01012345678", 1);
    waitingRequestRepository.save(waitingRequest);
  }

  @DisplayName("대기 요청 생성 검증")
  @Nested
  class createWaitingRequest {

    @DisplayName("생성 성공")
    @Test
    void createWaitingRequest() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
      CreateWaitingRequestCommand command = CreateWaitingRequestCommand.builder()
          .dailyWaitingUuid(UUID.randomUUID().toString())
          .phone("01000000000")
          .slackId("slack@slack.com")
          .seatSize(3)
          .build();

      // when
      String waitingRequestUuid = waitingRequestService.createWaitingRequest(userInfo, command);

      // then
      WaitingRequest waitingRequest =
          waitingRequestRepository.findByWaitingRequestUuidAndDeletedAtIsNull(waitingRequestUuid);
      Integer sequence = waitingRequestRepository.getLastWaitingSequence(command.dailyWaitingUuid());

      assertThat(waitingRequest.getDailyWaitingUuid()).isEqualTo(command.dailyWaitingUuid());
      assertThat(waitingRequest.getWaitingRequestUuid()).isEqualTo(waitingRequestUuid);
      assertThat(waitingRequest.getSequence()).isEqualTo(sequence);
    }

    @DisplayName("이미 대기 요청한 휴대전화 번호로 재요청하는 경우 생성 실패")
    @Test
    void failCreateWaitingRequestForDuplicatePhone() {
      // given
      CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(2L, UserRole.CUSTOMER);
      CreateWaitingRequestCommand command = CreateWaitingRequestCommand.builder()
          .dailyWaitingUuid(waitingRequest.getDailyWaitingUuid())
          .phone("01012345678")
          .slackId("slack@slack.com")
          .seatSize(3)
          .build();

      // when, then
      assertThatThrownBy(() -> waitingRequestService.createWaitingRequest(userInfo, command))
          .isInstanceOf(CustomException.class)
          .hasMessage(WaitingRequestErrorCode.ALREADY_EXISTS_WAITING.getMessage());
    }
  }
}