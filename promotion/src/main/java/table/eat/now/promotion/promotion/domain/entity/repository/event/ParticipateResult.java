package table.eat.now.promotion.promotion.domain.entity.repository.event;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 17.
 */
public enum ParticipateResult {
  FAIL,
  SUCCESS,
  SUCCESS_SEND_BATCH,
  DUPLICATION,
  ;

  public static ParticipateResult from(Long result) {
    if (result == null || result == 0L) return FAIL;
    if (result == 2L) return SUCCESS_SEND_BATCH;
    if (result == 3L) return DUPLICATION;
    return SUCCESS;
  }
}
