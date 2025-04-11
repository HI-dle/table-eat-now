package table.eat.now.coupon.user_coupon.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.repository.UserCouponRepository;

@RequiredArgsConstructor
@Service
public class UserCouponServiceImpl implements UserCouponService {
  private final UserCouponRepository userCouponRepository;

  @Transactional
  @Override
  public void createUserCoupon(IssueUserCouponCommand command) {
    UserCoupon userCoupon = command.toEntity();
    userCouponRepository.save(userCoupon);
  }
}
