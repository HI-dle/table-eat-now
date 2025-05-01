package table.eat.now.promotion.global.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import table.eat.now.promotion.promotion.application.service.PromotionService;
import table.eat.now.promotion.promotion.presentation.PromotionAdminController;
import table.eat.now.promotion.promotion.presentation.PromotionController;
import table.eat.now.promotion.promotion.presentation.PromotionInternalController;
import table.eat.now.promotion.promotionrestaurant.application.service.PromotionRestaurantService;
import table.eat.now.promotion.promotionrestaurant.presentation.PromotionRestaurantInternalController;
import table.eat.now.promotion.promotionuser.application.service.PromotionUserService;
import table.eat.now.promotion.promotionuser.presentation.PromotionUserAdminController;
import table.eat.now.promotion.promotionuser.presentation.PromotionUserController;

@WebMvcTest(controllers = {
    PromotionAdminController.class,
    PromotionController.class,
    PromotionInternalController.class,
    PromotionRestaurantInternalController.class,
    PromotionUserAdminController.class,
    PromotionUserController.class
})
public abstract class ControllerTestSupport {
  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockitoBean
  protected PromotionService promotionService;

  @MockitoBean
  protected PromotionRestaurantService promotionRestaurantService;

  @MockitoBean
  protected PromotionUserService promotionUserService;

}
