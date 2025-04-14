package table.eat.now.payment.payment.application.helper;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionalHelper {

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void doInNewTransaction(Runnable runnable) {
    runnable.run();
  }
}
