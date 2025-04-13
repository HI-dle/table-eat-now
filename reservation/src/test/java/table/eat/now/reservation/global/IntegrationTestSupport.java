/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.global;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import table.eat.now.reservation.global.config.TearDownExecutor;
import table.eat.now.reservation.reservation.application.client.CouponClient;
import table.eat.now.reservation.reservation.application.client.PaymentClient;
import table.eat.now.reservation.reservation.application.client.PromotionClient;
import table.eat.now.reservation.reservation.application.client.RestaurantClient;


@Import(TearDownExecutor.class)
@ActiveProfiles("test")
@SpringBootTest
@TestConstructor(autowireMode = AutowireMode.ALL)
public abstract class IntegrationTestSupport {

  @Autowired
  private TearDownExecutor tearDownExecutor;

  @MockitoBean
  protected CouponClient couponClient;

  @MockitoBean
  protected PaymentClient paymentClient;

  @MockitoBean
  protected PromotionClient promotionClient;

  @MockitoBean
  protected RestaurantClient restaurantClient;

  @AfterEach
  void tearDown() {
    tearDownExecutor.execute();
  }
}
