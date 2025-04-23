package table.eat.now.coupon.user_coupon.application.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import table.eat.now.coupon.user_coupon.application.dto.request.IssueUserCouponCommand;
import table.eat.now.coupon.user_coupon.domain.entity.UserCoupon;
import table.eat.now.coupon.user_coupon.domain.store.UserCouponStore;

@RequiredArgsConstructor
@Service
public class IssuePromotionUserCouponUsecase {

  private final UserCouponStore store;

  public void execute(List<IssueUserCouponCommand> commands) {
    List<UserCoupon> userCoupons = commands.stream()
        .map(IssueUserCouponCommand::toEntity)
        .toList();

    store.batchInsert(userCoupons);
  }

  public void execute2(List<IssueUserCouponCommand> commands) {
    List<UserCoupon> userCoupons = commands.stream()
        .map(IssueUserCouponCommand::toEntity)
        .toList();

    store.optimizedSaveAll(userCoupons);
  }
}
