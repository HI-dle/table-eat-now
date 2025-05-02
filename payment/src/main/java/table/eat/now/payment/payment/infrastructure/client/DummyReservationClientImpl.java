//package table.eat.now.payment.payment.infrastructure.client;
//
//import static table.eat.now.payment.payment.application.exception.PaymentErrorCode.RESERVATION_NOT_FOUND;
//
//import java.math.BigDecimal;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//import table.eat.now.common.exception.CustomException;
//import table.eat.now.payment.payment.application.client.ReservationClient;
//import table.eat.now.payment.payment.application.client.dto.GetReservationInfo;
//
//@Primary
//@Component
//public class DummyReservationClientImpl implements ReservationClient {
//
//  @Override
//  public GetReservationInfo getReservation(String reservationUuid) {
//    String nonValidUUID  = "00000000-0000-0000-0000-000000000000";
//
//    if (nonValidUUID.equals(reservationUuid)) {
//      throw CustomException.from(RESERVATION_NOT_FOUND);
//    }
//
//    return GetReservationInfo.builder()
//        .status("PENDING_PAYMENT")
//        .totalAmount(new BigDecimal("45000"))
//        .build();
//  }
//}
