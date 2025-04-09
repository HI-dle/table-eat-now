/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.restaurant.global;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import table.eat.now.restaurant.global.config.TearDownExecutor;


@Import(TearDownExecutor.class)
@ActiveProfiles("test")
@SpringBootTest
@TestConstructor(autowireMode = AutowireMode.ALL)
public abstract class IntegrationTestSupport {

  @Autowired
  private TearDownExecutor tearDownExecutor;

  @AfterEach
  void tearDown() {
    tearDownExecutor.execute();
  }
}
