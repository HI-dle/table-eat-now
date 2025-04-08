package table.eat.now.payment.payment.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceImplTest {

	@Autowired
	private PaymentService paymentService;

	@Test
	public void 아무것도_안한다() {
		assertDoesNotThrow(() -> paymentService.doNothing());
	}
}