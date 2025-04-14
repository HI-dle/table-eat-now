package table.eat.now.payment.payment.application.helper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TransactionalHelperTest {

  @Autowired
  private TransactionalHelper transactionalHelper;

  @Test
  void doInNewTransaction_은_제공된_Runnable을_실행한다() {
    // given
    Runnable mockRunnable = mock(Runnable.class);

    // when
    transactionalHelper.doInNewTransaction(mockRunnable);

    // then
    verify(mockRunnable, times(1)).run();
  }
}