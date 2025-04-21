package table.eat.now.payment.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
@Embeddable
public class PaymentAmount {

  @Column(name = "original_amount", nullable = false, precision = 8)
  private BigDecimal originalAmount;

  @Column(name = "discount_amount", precision = 8)
  private BigDecimal discountAmount;

  @Column(name = "total_amount", precision = 8)
  private BigDecimal totalAmount;

  @Column(name = "cancel_amount", precision = 8)
  private BigDecimal cancelAmount;

  @Column(name = "balance_amount", precision = 8)
  private BigDecimal balanceAmount;

  public static PaymentAmount create(BigDecimal originalAmount) {
    validateAmount(originalAmount);
    return new PaymentAmount(originalAmount, null, null, null, null);
  }

  private static void validateAmount(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("금액은 null이 될 수 없습니다");
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("금액은 0보다 커야 합니다");
    }
    validatePrecision(amount);
  }

  private static void validatePrecision(BigDecimal amount) {
    if (amount.precision() - amount.scale() > 8) {
      throw new IllegalArgumentException("금액은 최대 8자리 정수를 초과할 수 없습니다");
    }

    if (amount.scale() > 0) {
      throw new IllegalArgumentException("금액은 정수여야 합니다");
    }
  }

  public PaymentAmount confirm(BigDecimal discountAmount, BigDecimal totalAmount) {
    validateAmount(totalAmount);
    discountAmount = discountAmount == null ? BigDecimal.ZERO : discountAmount;
    validateDiscountAmount(discountAmount);
    validateTotalAmount(discountAmount, totalAmount);

    return new PaymentAmount(
        this.originalAmount,
        discountAmount,
        totalAmount,
        BigDecimal.ZERO,
        totalAmount
    );
  }

  public PaymentAmount cancel(BigDecimal cancelAmount, BigDecimal balanceAmount) {
    return new PaymentAmount(
        this.originalAmount,
        discountAmount,
        totalAmount,
        cancelAmount,
        balanceAmount
    );
  }

  private void validateDiscountAmount(BigDecimal discountAmount) {
    if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
      validatePrecision(discountAmount);
      validateDiscountRange(discountAmount);
    }
  }

  private void validateDiscountRange(BigDecimal discountAmount) {
    if (discountAmount.compareTo(this.originalAmount) > 0) {
      throw new IllegalArgumentException("할인 금액은 원래 금액보다 클 수 없습니다");
    }
  }

  private void validateTotalAmount(BigDecimal discountAmount, BigDecimal totalAmount) {
    BigDecimal expectedTotal = this.originalAmount.subtract(discountAmount);
    if (!expectedTotal.equals(totalAmount)) {
      throw new IllegalArgumentException(
          String.format(
              "총 금액이 일치하지 않습니다. 예상: %s, 실제: %s", expectedTotal, totalAmount));
    }
  }

  private PaymentAmount(BigDecimal originalAmount, BigDecimal discountAmount,
      BigDecimal totalAmount, BigDecimal cancelAmount, BigDecimal balanceAmount) {
    this.originalAmount = originalAmount;
    this.discountAmount = discountAmount;
    this.totalAmount = totalAmount;
    this.cancelAmount = cancelAmount;
    this.balanceAmount = balanceAmount;
  }

  protected PaymentAmount() {
  }
}