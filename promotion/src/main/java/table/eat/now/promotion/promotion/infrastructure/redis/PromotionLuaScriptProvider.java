package table.eat.now.promotion.promotion.infrastructure.redis;

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

    redis.call('ZADD', key, now, userInfo)
    return 1

    """;
  }
}

