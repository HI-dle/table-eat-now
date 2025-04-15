package table.eat.now.waiting.waiting.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import table.eat.now.waiting.helper.IntegrationTestSupport;
import table.eat.now.waiting.waiting.application.service.dto.response.GetDailyWaitingInfo;
import table.eat.now.waiting.waiting.domain.entity.DailyWaiting;
import table.eat.now.waiting.waiting.domain.repository.WaitingRepository;

class WaitingServiceImplTest extends IntegrationTestSupport {

  @Autowired
  private WaitingService waitingService;
  @Autowired
  private WaitingRepository waitingRepository;
  private DailyWaiting dailyWaiting;

  @BeforeEach
  void setUp() {
    dailyWaiting = DailyWaiting.of(UUID.randomUUID().toString(), "지훈이네 식당",
        "AVAILABLE", LocalDate.now(), 600L);
    waitingRepository.save(dailyWaiting);
  }

  @DisplayName("일간 대기 정보 단건 조회 검증 - 성공")
  @Test
  void getDailyWaitingInfo() {
    // given
    // when
    GetDailyWaitingInfo dailyWaitingInfo = waitingService.getDailyWaitingInfo(
        dailyWaiting.getDailyWaitingUuid());

    // then
    assertThat(dailyWaitingInfo.status()).isEqualTo(dailyWaiting.getStatus().toString());
    assertThat(dailyWaitingInfo.waitingDate()).isEqualTo(dailyWaiting.getWaitingDate());
  }
}