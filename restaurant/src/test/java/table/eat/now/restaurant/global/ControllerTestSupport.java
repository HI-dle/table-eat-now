/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.restaurant.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import table.eat.now.restaurant.restaurant.application.service.RestaurantService;
import table.eat.now.restaurant.restaurant.presentation.RestaurantAdminController;

@WebMvcTest(controllers = {
    RestaurantAdminController.class
})
public abstract class ControllerTestSupport {
  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockitoBean
  protected RestaurantService restaurantService;
}
