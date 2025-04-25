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
    -- Lua 스크립트
    local key = KEYS[1]
    local now = tonumber(ARGV[1])
    local rawValues = redis.call('ZRANGEBYSCORE', key, 0, now)
    
    if #rawValues > 0 then
        redis.call('ZREM', key, unpack(rawValues))
    end
    
    -- 반환된 값에서 UUID만 추출 (':start'와 ':end'를 제거)
    local result = {}
    
    for i, raw in ipairs(rawValues) do
        local delim = string.find(raw, ":")
        if delim ~= nil then
            local uuid = string.sub(raw, 1, delim - 1)
            table.insert(result, uuid)
        end
    end
    
    return result
    """;
  }
}

