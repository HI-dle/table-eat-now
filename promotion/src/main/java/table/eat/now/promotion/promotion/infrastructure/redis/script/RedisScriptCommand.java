package table.eat.now.promotion.promotion.infrastructure.redis.script;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 19.
 */
public interface RedisScriptCommand<T> {
  T execute();
}
