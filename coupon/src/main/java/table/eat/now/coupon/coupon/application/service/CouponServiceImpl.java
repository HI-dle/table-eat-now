package table.eat.now.coupon.coupon.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.coupon.coupon.application.dto.request.CreateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.request.UpdateCouponCommand;
import table.eat.now.coupon.coupon.application.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.exception.CouponErrorCode;
import table.eat.now.coupon.coupon.domain.entity.Coupon;
import table.eat.now.coupon.coupon.domain.repository.CouponRepository;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
  private final CouponRepository couponRepository;

  @Override
  public String createCoupon(CreateCouponCommand command) {

    Coupon coupon = command.toEntity();
    couponRepository.save(coupon);
    return coupon.getCouponUuid();
  }

  @Transactional
  @Override
  public void updateCoupon(UUID couponUuid, UpdateCouponCommand command) {

    Coupon coupon = couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid.toString())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    coupon.modify(command.toDomainCommand());
  }

  @Transactional(readOnly = true)
  @Override
  public GetCouponInfo getCoupon(UUID couponUuid) {

    Coupon coupon = couponRepository.findByCouponUuidAndDeletedAtIsNullFetchJoin(couponUuid.toString())
        .orElseThrow(() -> CustomException.from(CouponErrorCode.INVALID_COUPON_UUID));
    return GetCouponInfo.from(coupon);
  }
}
