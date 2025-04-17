/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 11.
 */
package table.eat.now.reservation.reservation.presentation;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import table.eat.now.common.resolver.dto.UserRole;
import table.eat.now.reservation.global.ControllerTestSupport;
import table.eat.now.reservation.global.util.UuidMaker;
import table.eat.now.reservation.reservation.application.service.dto.request.CancelReservationCommand;
import table.eat.now.reservation.reservation.application.service.dto.request.GetReservationCriteria;
import table.eat.now.reservation.reservation.application.service.dto.response.CancelReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.CreateReservationInfo;
import table.eat.now.reservation.reservation.application.service.dto.response.GetReservationInfo;
import table.eat.now.reservation.reservation.presentation.dto.request.CancelReservationRequest;
import table.eat.now.reservation.reservation.presentation.dto.request.CreateReservationRequest;

class ReservationApiControllerTest extends ControllerTestSupport {

  @DisplayName("예약 신청 컨트롤러")
  @Nested
  class create {

    @DisplayName("고객이 예약을 신청할 수 있다.")
    @Test
    void createReservation() throws Exception {
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
          .andExpect(
              jsonPath("$.reservationUuid").value(reservationUuid)) // 응답 본문에서 restaurantUuid 값 확인
          .andExpect(jsonPath("$.paymentReferenceKey").value(
              paymentReferenceKey)); // 응답 본문에서 restaurantUuid 값 확인
    }
  }

  @DisplayName("예약 단건 조회 api 컨트롤러")
  @Nested
  class GetReservationApi {

    private static final String urlPrefix = "/api/v1/reservations";

    public static Stream<Arguments> provideUserRoleForCheckingGetReservationPermission() {
      return Stream.of(
          Arguments.of(UserRole.MASTER),
          Arguments.of(UserRole.OWNER),
          Arguments.of(UserRole.STAFF),
          Arguments.of(UserRole.CUSTOMER)
      );
    }

    @DisplayName("MASTER, OWNER, STAFF, CUSTOMER 는 예약을 단건 조회할 수 있다.")
    @MethodSource("provideUserRoleForCheckingGetReservationPermission")
    @ParameterizedTest(name = "{index}: ''{0}''는 예약을 단건 조회할 수 있다.")
    void success(UserRole role) throws Exception {
      // given
      String reservationUuid = UUID.randomUUID().toString();
      Long userId = 1L;

      GetReservationInfo.PaymentDetailInfo paymentDetail = GetReservationInfo.PaymentDetailInfo.builder()
          .reservationPaymentDetailUuid(UUID.randomUUID().toString())
          .type(GetReservationInfo.PaymentType.PAYMENT)
          .amount(BigDecimal.valueOf(15000))
          .detailReferenceId("카카오페이1234")
          .build();

      GetReservationInfo response = GetReservationInfo.builder()
          .reservationUuid(reservationUuid)
          .name("디너 예약")
          .reserverName("홍길동")
          .reserverContact("010-1234-5678")
          .guestCount(2)
          .restaurantName("한우명가")
          .restaurantContactNumber("02-5678-1234")
          .restaurantAddress("서울시 마포구")
          .reservationDate(LocalDate.of(2025, 4, 20))
          .reservationTime(LocalTime.of(19, 0))
          .menuName("한우 특선")
          .menuPrice(BigDecimal.valueOf(30000))
          .menuQuantity(2)
          .status(GetReservationInfo.ReservationStatus.CONFIRMED)
          .specialRequest("창가 자리 부탁드려요")
          .totalAmount(BigDecimal.valueOf(60000))
          .paymentDetails(List.of(paymentDetail))
          .build();

      given(reservationService.getReservation(any(GetReservationCriteria.class)))
          .willReturn(response);

      // when & then
      mockMvc.perform(get(urlPrefix + "/{reservationUuid}", reservationUuid)
              .header(USER_ID_HEADER, userId)
              .header(USER_ROLE_HEADER, role)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.reservationUuid").value(reservationUuid))
          .andExpect(jsonPath("$.name").value("디너 예약"))
          .andExpect(jsonPath("$.reserverName").value("홍길동"))
          .andExpect(jsonPath("$.reserverContact").value("010-1234-5678"))
          .andExpect(jsonPath("$.guestCount").value(2))
          .andExpect(jsonPath("$.restaurantName").value("한우명가"))
          .andExpect(jsonPath("$.restaurantContactNumber").value("02-5678-1234"))
          .andExpect(jsonPath("$.restaurantAddress").value("서울시 마포구"))
          .andExpect(jsonPath("$.reservationDate").value("2025-04-20"))
          .andExpect(jsonPath("$.reservationTime").value("19:00:00"))
          .andExpect(jsonPath("$.menuName").value("한우 특선"))
          .andExpect(jsonPath("$.menuPrice").value(30000))
          .andExpect(jsonPath("$.menuQuantity").value(2))
          .andExpect(jsonPath("$.status").value("CONFIRMED"))
          .andExpect(jsonPath("$.specialRequest").value("창가 자리 부탁드려요"))
          .andExpect(jsonPath("$.totalAmount").value(60000))
          .andExpect(jsonPath("$.paymentDetails[0].reservationPaymentDetailUuid").value(
              paymentDetail.reservationPaymentDetailUuid()))
          .andExpect(jsonPath("$.paymentDetails[0].type").value("PAYMENT"))
          .andExpect(jsonPath("$.paymentDetails[0].amount").value(15000))
          .andExpect(jsonPath("$.paymentDetails[0].detailReferenceId").value("카카오페이1234"));
    }

    @DisplayName("권한이 없으면 401 예외가 발생한다.")
    @Test
    void fail_isForbidden() throws Exception {
      // given
      String reservationUuid = UUID.randomUUID().toString();
      Long userId = 99L;
      UserRole role = UserRole.CUSTOMER;

      // when & then
      mockMvc.perform(get(urlPrefix + "/{reservationUuid}", reservationUuid)
              .header(USER_ID_HEADER, userId)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
    }
  }

  @DisplayName("예약 취소 api 컨트롤러")
  @Nested
  class CancelReservationApi {

    static Stream<Arguments> provideUserRoleForCancelReservation() {
      return Stream.of(
          Arguments.of(UserRole.MASTER),
          Arguments.of(UserRole.OWNER),
          Arguments.of(UserRole.STAFF),
          Arguments.of(UserRole.CUSTOMER)
      );
    }

    @DisplayName("예약을 취소할 수 있다")
    @MethodSource("provideUserRoleForCancelReservation")
    @ParameterizedTest(name = "{index}: ''{0}'' 은 예약을 취소할 수 있다.")
    void cancelReservation_success(UserRole role) throws Exception {
      // given
      String reservationUuid = UUID.randomUUID().toString();
      Long userId = 1L;
      String cancelReason = "일정 변경으로 인한 취소";

      CancelReservationInfo cancelReservationInfo = CancelReservationInfo.builder()
          .reservationUuid(reservationUuid)
          .status("CANCELLED")
          .build();

      given(reservationService.cancelReservation(any(CancelReservationCommand.class)))
          .willReturn(cancelReservationInfo);

      CancelReservationRequest request = new CancelReservationRequest(cancelReason);

      // when & then
      mockMvc.perform(patch("/api/v1/reservations/{reservationUuid}/cancel", reservationUuid)
              .header(USER_ID_HEADER, userId)
              .header(USER_ROLE_HEADER, role)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.reservationUuid").value(reservationUuid))
          .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
  }
}