local userSetKey = KEYS[1]    -- 쿠폰 발급 유저 집합 키
local couponKey = KEYS[2]   -- 쿠폰 캐시 키
local idempotencyKey = KEYS[3]  -- 이벤트별 idempotency 키
local dirtyCouponSetKey = KEYS[4] -- 쿠폰 캐시 변경 감지 집합 키

local userId = ARGV[1]
local currentTimestamp = tonumber(ARGV[2])

-- 1. idempotency 체크
if redis.call('EXISTS', idempotencyKey) == 1 then
    return 2  -- 이미 이벤트 완료 처리된 경우
end

-- 2. 유저 중복 체크
if redis.call('SISMEMBER', userSetKey, userId) == 1 then
    return -1  -- 중복 발급 시도
end

-- 3. 재고 체크
local stock = tonumber(redis.call('HGET', couponKey, 'count'))
local issued = tonumber(redis.call('HGET', couponKey, 'issuedCount'))

if not stock then
    return -3  -- 재고 정보 없음
end

if not issued then
    issued = 0
end

if issued >= stock then
    return -2  -- 재고 없음
end

-- 4. 발급 완료 처리
redis.call('HINCRBY', couponKey, 'issuedCount', 1)
redis.call('SADD', userSetKey, userId)
redis.call('ZADD', dirtyCouponSetKey, currentTimestamp, couponKey)
redis.call('SET', idempotencyKey, 1, 'EX', 86400)  -- idempotency 키 설정 (3600 *24 1일 TTL)

return 1  -- 발급 성공