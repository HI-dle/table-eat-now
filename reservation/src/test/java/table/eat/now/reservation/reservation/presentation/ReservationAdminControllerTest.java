/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 16.
 */
package table.eat.now.reservation.reservation.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import table.eat.now.reservation.reservation.application.service.dto.request.GetReservationCriteria;
import table.eat.now.reservation.reservation.application.service.dto.response.GetReservationInfo;

class ReservationAdminControllerTest extends ControllerTestSupport {

  private static final String urlPrefix = "/admin/v1/reservations";

  @DisplayName("예약 단건 조회 admin 컨트롤러")
  @Nested
  class GetReservationAdmin {

    public static Stream<Arguments> provideUserRoleForCheckingGetReservationPermission() {
      return Stream.of(
          Arguments.of(UserRole.MASTER),
          Arguments.of(UserRole.OWNER),
          Arguments.of(UserRole.STAFF)
      );
    }

    @DisplayName("MASTER, OWNER, STAFF는 예약을 단건 조회할 수 있다.")
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
          .andExpect(jsonPath("$.paymentDetails[0].reservationPaymentDetailUuid").value(paymentDetail.reservationPaymentDetailUuid()))
          .andExpect(jsonPath("$.paymentDetails[0].type").value("PAYMENT"))
          .andExpect(jsonPath("$.paymentDetails[0].amount").value(15000))
          .andExpect(jsonPath("$.paymentDetails[0].detailReferenceId").value("카카오페이1234"));
    }

    @DisplayName("CUSTOMER는 admin api를 사용할 수 없어 403 예외가 발생한다.")
    @Test
    void fail_isForbidden_customer() throws Exception {
      // given
      String reservationUuid = UUID.randomUUID().toString();
      Long userId = 99L;
      UserRole role = UserRole.CUSTOMER;

      // when & then
      mockMvc.perform(get(urlPrefix + "/{reservationUuid}", reservationUuid)
              .header(USER_ID_HEADER, userId)
              .header(USER_ROLE_HEADER, role)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
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

}