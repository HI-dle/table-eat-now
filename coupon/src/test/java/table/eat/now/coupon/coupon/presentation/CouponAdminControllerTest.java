package table.eat.now.coupon.coupon.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.coupon.coupon.application.service.dto.response.GetCouponInfo;
import table.eat.now.coupon.coupon.application.service.dto.response.PageResponse;
import table.eat.now.coupon.coupon.application.service.dto.response.SearchCouponInfo;
import table.eat.now.coupon.coupon.fixture.CouponFixture;
import table.eat.now.coupon.coupon.presentation.dto.request.CreateCouponRequest;
import table.eat.now.coupon.coupon.presentation.dto.request.UpdateCouponRequest;
import table.eat.now.coupon.helper.ControllerTestSupport;

class CouponAdminControllerTest extends ControllerTestSupport {

  @BeforeEach
  void setUp() {
  }

  @DisplayName("쿠폰 생성 요청 검증 - 201 응답")
  @Test
  void createCoupon() throws Exception {
    // given
    CreateCouponRequest request = CreateCouponRequest.builder()
        .name("test")
        .type("FIXED_DISCOUNT")
        .label("HOT")
        .issueStartAt(LocalDateTime.now().plusDays(1))
        .issueEndAt(LocalDateTime.now().plusDays(5))
        .validDays(null)
        .expireAt(LocalDateTime.now().plusDays(8))
        .count(10000)
        .allowDuplicate(false)
        .minPurchaseAmount(50000)
        .amount(3000)
        .percent(null)
        .maxDiscountAmount(null)
        .build();

    String couponUuid = UUID.randomUUID().toString();
    given(couponService.createCoupon(request.toCommand())).willReturn(couponUuid);

    // when
    ResultActions resultActions = mockMvc.perform(post("/admin/v1/coupons")
        .header("Authorization", "Bearer {ACCESS_TOKEN}")
        .header(USER_ID_HEADER, "1")
        .header(USER_ROLE_HEADER, "MASTER")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(header().string(
            "Location", Matchers.endsWith(String.format("/admin/v1/coupons/%s", couponUuid))))
        .andDo(print());
  }

  @DisplayName("쿠폰 수정 요청 검증 - 200 응답")
  @Test
  void updateCoupon() throws Exception {
    // given
    UpdateCouponRequest request = UpdateCouponRequest.builder()
        .name("test")
        .label("GENERAL")
        .type("FIXED_DISCOUNT")
        .issueStartAt(LocalDateTime.now().plusDays(1))
        .issueEndAt(LocalDateTime.now().plusDays(5))
        .expireAt(LocalDateTime.now().plusDays(8))
        .count(10000)
        .allowDuplicate(false)
        .minPurchaseAmount(50000)
        .amount(3000)
        .percent(null)
        .maxDiscountAmount(null)
        .version(4L)
        .build();

    String couponUuid = UUID.randomUUID().toString();
    doNothing().when(couponService).updateCoupon(couponUuid, request.toCommand());

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/admin/v1/coupons/{couponUuid}", couponUuid)
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.couponUuid").value(couponUuid))
        .andDo(print());
  }

  @DisplayName("쿠폰 조회 요청 검증 - 200 응답")
  @Test
  void getCouponInfo() throws Exception {
    // given
    String couponUuid = UUID.randomUUID().toString();
    GetCouponInfo couponInfo = GetCouponInfo.builder()
        .couponId(1L)
        .couponUuid(couponUuid)
        .name("test")
        .type("FIXED_DISCOUNT")
        .issueStartAt(LocalDateTime.now().plusDays(1))
        .issueEndAt(LocalDateTime.now().plusDays(5))
        .expireAt(LocalDateTime.now().plusDays(8))
        .count(10000)
        .allowDuplicate(false)
        .minPurchaseAmount(50000)
        .amount(3000)
        .percent(null)
        .maxDiscountAmount(null)
        .createdAt(LocalDateTime.now().minusHours(1))
        .createdBy(1L)
        .version(3L)
        .build();

    given(couponService.getCouponInfo(couponUuid)).willReturn(couponInfo);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/admin/v1/coupons/{couponUuid}", couponUuid)
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER"));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.couponUuid").value(couponUuid))
        .andExpect(jsonPath("$.type").value("FIXED_DISCOUNT"))
        .andDo(print());
  }

  @DisplayName("쿠폰 삭제 요청 검증 - 204 응답")
  @Test
  void deleteCoupon() throws Exception {
    // given
    String couponUuid = UUID.randomUUID().toString();
    CurrentUserInfoDto userInfo = CurrentUserInfoDto.of(1L, UserRole.MASTER);

    doNothing().when(couponService).deleteCoupon(userInfo, couponUuid);

    // when
    ResultActions resultActions = mockMvc.perform(
        delete("/admin/v1/coupons/{couponUuid}", couponUuid)
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, userInfo.userId())
            .header(USER_ROLE_HEADER, userInfo.role()));

    // then
    resultActions.andExpect(status().isNoContent())
        .andDo(print());
  }

  @DisplayName("쿠폰 목록 조회 요청 검증 - 200 응답")
  @Test
  void searchCoupons() throws Exception {
    // given
    List<SearchCouponInfo> couponInfos = CouponFixture.createCouponInfos(20);
    PageResponse<SearchCouponInfo> couponInfoPage = PageResponse.of(
        couponInfos, 20, 2, 1, 10);

    given(couponService.searchCoupons(any(), any())).willReturn(couponInfoPage);

    // when
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("fromAt", LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS).toString());
    params.add("toAt", LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.DAYS).toString());
    params.add("type", "FIXED_DISCOUNT");

    ResultActions resultActions = mockMvc.perform(
        get("/admin/v1/coupons")
            .params(params)
            .header("Authorization", "Bearer {ACCESS_TOKEN}")
            .header(USER_ID_HEADER, "1")
            .header(USER_ROLE_HEADER, "MASTER"));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.coupons").isArray())
        .andExpect(jsonPath("$.coupons.length()").value(couponInfos.size()))
        .andExpect(jsonPath("$.totalElements").value(20))
        .andExpect(jsonPath("$.coupons[0].couponUuid").value(couponInfos.get(0).couponUuid()))
        .andDo(print());
  }
}