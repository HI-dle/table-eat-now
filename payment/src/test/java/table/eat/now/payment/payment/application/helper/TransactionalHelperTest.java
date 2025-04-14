package table.eat.now.payment.payment.application.helper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TransactionalHelperTest {

  @InjectMocks
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