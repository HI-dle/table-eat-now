package table.eat.now.notification.domain.entity;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public enum NotificationType {
  //예약 확인 요청 - 점주용
  CONFIRM_OWNER,
  //예약 확인 알림 - 고객용
  CONFIRM_CUSTOMER,
  //당일 예약 건들 9시 포괄알림
  REMINDER_9AM,
  //1시간 전 알림
  REMINDER_1HR,
  //방문 완료 알림
  COMPLETION,
  //노쇼 처리 알림
  NO_SHOW,
  //대기 신청 완료
  CONFIRM_WAITING,
  //대기 입장 안내
  INFO_WAITING
}
