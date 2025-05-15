local userSetKey = KEYS[1]    -- 쿠폰 발급 유저 집합 키
local couponCountKey = KEYS[2]   -- 쿠폰 캐시 키
local userId = ARGV[1]

-- 2. 유저 중복 체크
if redis.call('SISMEMBER', userSetKey, userId) == 1 then
    return -1  -- 중복 발급 시도
end

-- 3. 재고 체크
local remainder = tonumber(redis.call('DECRBY', couponCountKey, 1))

if remainder < 0 then
    return -2  -- 재고 없음
end

-- 4. 발급 완료 처리
redis.call('SADD', userSetKey, userId)

return 1  -- 발급 성공