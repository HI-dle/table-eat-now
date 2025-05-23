package table.eat.now.waiting.helper;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestContainerExtension implements BeforeAllCallback {

  private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
  private static final int REDIS_PORT = 6379;
  private GenericContainer REDIS_CONTAINER;

  @Override
  public void beforeAll(ExtensionContext context) {
    REDIS_CONTAINER = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
        .withExposedPorts(REDIS_PORT);
        //.withCommand("redis-server --requirepass systempass");
    REDIS_CONTAINER.start();

    System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
    System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
  }
}