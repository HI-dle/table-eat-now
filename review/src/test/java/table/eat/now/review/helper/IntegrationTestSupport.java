package table.eat.now.review.helper;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(RedisTestContainerExtension.class)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public abstract class IntegrationTestSupport {

}