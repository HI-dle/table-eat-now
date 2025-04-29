package table.eat.now.coupon.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import table.eat.now.coupon.coupon.application.service.CouponService;
import table.eat.now.coupon.coupon.presentation.CouponAdminController;
import table.eat.now.coupon.coupon.presentation.CouponApiController;
import table.eat.now.coupon.coupon.presentation.CouponInternalController;
import table.eat.now.coupon.user_coupon.application.service.UserCouponService;
import table.eat.now.coupon.user_coupon.presentation.UserCouponApiController;
import table.eat.now.coupon.user_coupon.presentation.UserCouponInternalController;

@WebMvcTest({
    CouponAdminController.class,
    CouponApiController.class,
    CouponInternalController.class,
    UserCouponApiController.class,
    UserCouponInternalController.class
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class ControllerTestSupport {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockitoBean
  protected CouponService couponService;

  @MockitoBean
  protected UserCouponService userCouponService;
}
