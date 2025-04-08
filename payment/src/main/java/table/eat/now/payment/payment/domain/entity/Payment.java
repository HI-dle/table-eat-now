package table.eat.now.payment.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import table.eat.now.common.domain.BaseEntity;

@Getter
@Entity
@Table(name = "p_payment")
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private PaymentReference reference;

	@Embedded
	private PaymentIdentifier identifier;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Column(unique = true)
	private String paymentKey;

	@Column(nullable = false, precision = 8)
	private BigDecimal originalAmount;

	@Column(precision = 8)
	private BigDecimal discountAmount;

	@Column(precision = 8)
	private BigDecimal totalAmount;

	private LocalDateTime requestedAt;

	private LocalDateTime approvedAt;

	protected Payment() {
	}
}
