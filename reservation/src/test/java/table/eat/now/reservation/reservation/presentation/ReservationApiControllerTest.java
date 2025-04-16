/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.presentation;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static table.eat.now.common.constant.UserInfoConstant.USER_ID_HEADER;
import static table.eat.now.common.constant.UserInfoConstant.USER_ROLE_HEADER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import table.eat.now.reservation.global.ControllerTestSupport;
import table.eat.now.reservation.global.util.UuidMaker;
import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;
import table.eat.now.reservation.reservation.presentation.dto.request.CreateReservationRequest;

class ReservationApiControllerTest extends ControllerTestSupport {

  @DisplayName("예약 신청 컨트롤러")
  @Nested
  class create{

    @DisplayName("고객이 예약을 신청할 수 있다.")
    @Test
    void createReservation() throws Exception{
      CreateReservationRequest request = new CreateReservationRequest(
          "홍길동", // 예약자 이름
          "010-9876-5432", // 예약자 연락처
          UUID.randomUUID().toString(), // 레스토랑 UUID
          UUID.randomUUID().toString(), // 레스토랑 타임슬롯 UUID
          UUID.randomUUID().toString(), // 레스토랑 메뉴 UUID
          2, // 예약 인원 수
          "특별 요청 사항", // 요청사항
          new BigDecimal("10000"), // 총 금액
          new CreateReservationRequest.RestaurantTimeSlotDetails(
              LocalDate.now(), // 예약 가능 날짜
              LocalTime.of(12, 0) // 타임슬롯
          ),
          new CreateReservationRequest.RestaurantDetails(
              "맛집 레스토랑", // 레스토랑 이름
              "서울시 강남구 테헤란로 123", // 레스토랑 주소
              "02-1234-5678", // 레스토랑 연락처
              LocalTime.of(10, 0), // 오픈 시간
              LocalTime.of(22, 0) // 마감 시간
          ),
          new CreateReservationRequest.RestaurantMenuDetails(
              "스시 세트", // 메뉴 이름
              new BigDecimal("20000"), // 메뉴 가격
              3 // 메뉴 수량
          ),
          List.of(
              new CreateReservationRequest.PaymentDetail(
                  CreateReservationRequest.PaymentType.PAYMENT,
                  "payment-ref-123",
                  new BigDecimal("10000")
              )
          )
      );

      // 반환할 UUID
      String reservationUuid = UuidMaker.makeUuid().toString();
      String paymentReferenceKey = UuidMaker.makeUuid().toString();
      given(reservationService.createReservation(any()))
          .willReturn(new CreateReservationInfo(
              reservationUuid, paymentReferenceKey
          ));

      // when & then
      mockMvc.perform(post("/api/v1/reservations")
              .header(USER_ID_HEADER, "1") // 사용자 ID 헤더
              .header(USER_ROLE_HEADER, "CUSTOMER") // 사용자 역할 헤더
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))) // 요청 바디 설정
          .andExpect(status().isCreated()) // 상태 코드 확인
          .andExpect(header().string("Location",
              containsString("/api/v1/reservations/" + reservationUuid))) // Location 헤더 확인
          .andExpect(jsonPath("$.reservationUuid").value(reservationUuid)) // 응답 본문에서 restaurantUuid 값 확인
          .andExpect(jsonPath("$.paymentReferenceKey").value(paymentReferenceKey)); // 응답 본문에서 restaurantUuid 값 확인
    }
  }
}