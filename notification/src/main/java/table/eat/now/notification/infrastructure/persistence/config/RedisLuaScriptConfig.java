package table.eat.now.notification.infrastructure.persistence.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 23.
 */
@Configuration
public class RedisLuaScriptConfig {

  //KEYS[1] : 조회할 ZSET 키 (예: "notification:delay-queue")
  //ARGV[1] : 현재 시간 (millis 기준), 즉 이 시간 이하의 항목만 조회
  //ARGV[2] : 최대 몇 개까지 가져올지 설정 (LIMIT 개수)
  //결과는 현재 시간까지 도달한 알림들만 최대 ARGV[2]개 가져옴
  //위에서 가져온 results 안의 value들을 루프 돌면서
  //ZSET에서 제거(ZREM) —> 중복 발송 방지
  @Bean
  public DefaultRedisScript<List> addToDelayQueueScript() {
    DefaultRedisScript<List> script = new DefaultRedisScript<>();
    script.setScriptText(
        "local results = redis.call('ZRANGEBYSCORE', KEYS[1], 0, ARGV[1], 'LIMIT', 0, ARGV[2]) " +
            "for i, v in ipairs(results) do redis.call('ZREM', KEYS[1], v) end " +
            "return results"
    );
    script.setResultType(List.class);
    return script;
  }
}
