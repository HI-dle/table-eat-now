package table.eat.now.promotion.promotion.infrastructure.redis.script;

import org.springframework.stereotype.Component;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 15.
 */
@Component
public class PromotionLuaScriptProvider {

  public String getAddUserScript() {
    return """
    local key = KEYS[1]
    local maxCount = tonumber(ARGV[1])
    local now = tonumber(ARGV[2])
    local userId = ARGV[3]
    local promotionUuid = ARGV[4]

    local userInfo = userId .. ":" .. promotionUuid

    local currentCount = redis.call('ZCARD', key)
    if currentCount >= maxCount then
    return 0
    end
    
    local uniqueScore = now * 1000 + userId

    redis.call('ZADD', key, uniqueScore, userInfo)
    
    currentCount = redis.call('ZCARD', key)
    if currentCount % 1000 == 0 then
      return 2
    end
            
    return 1

    """;
  }

  public String getPollScheduleQueueScript() {
    return """
    local key = KEYS[1]
    local now = tonumber(ARGV[1])
    local values = redis.call('ZRANGEBYSCORE', key, 0, now)
    if #values > 0 then
      redis.call('ZREM', key, unpack(values))
    end
    return values
  """;
  }
}

