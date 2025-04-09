/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.restaurant.restaurant.presentation;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import jakarta.ws.rs.core.MediaType;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import table.eat.now.restaurant.global.ControllerTestSupport;
import table.eat.now.restaurant.global.util.UuidMaker;
import table.eat.now.restaurant.restaurant.application.service.dto.response.CreateRestaurantInfo;
import table.eat.now.restaurant.restaurant.presentation.dto.request.CreateRestaurantRequest;

class RestaurantAdminControllerTest extends ControllerTestSupport {

  @DisplayName("식당 생성 컨트롤러")
  @Nested
  class create{

    @DisplayName("마스터는 식당을 생성할 수 있다.")
    @Test
    void created() throws Exception {
      // given
      CreateRestaurantRequest request = new CreateRestaurantRequest(
          1L,
          "스시 오마카세",
          "신선한 해산물과 숙련된 셰프의 스시를 즐길 수 있는 오마카세 전문점",
          4,
          "010-1234-5678",
          "서울시 강남구 테헤란로 123",
          LocalTime.of(8, 0),
          LocalTime.of(18, 0)
      );

      // 반환할 UUID
      String restaurantUuid = UuidMaker.makeUuid().toString();
      given(restaurantService.createRestaurant(any()))
          .willReturn(CreateRestaurantInfo.builder().restaurantUuid(restaurantUuid).build());

      // when & then
      mockMvc.perform(post("/admin/v1/restaurants")
              // .header("Authorization", "Bearer {ACCESS_TOKEN}")
              .header(USER_ID_HEADER, "1")
              .header(USER_ROLE_HEADER, "MASTER")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location",
              containsString("/admin/v1/restaurants/" + restaurantUuid)));
    }
  }
}