package table.eat.now.promotion.promotionUser.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import table.eat.now.common.domain.BaseEntity;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Entity
@Table(name = "p_promotion_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionUser extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, unique = true, length = 100, name = "promotion_user_uuid")
  private String promotionUserUuid;

  @Column(nullable = false)
  private Long userId;


  private PromotionUser(Long userId) {
    this.promotionUserUuid = UUID.randomUUID().toString();
    this.userId = userId;
  }
  public static PromotionUser of(Long userId) {
    return new PromotionUser(userId);
  }
  public void modifyPromotionUser(Long userId) {
    this.userId = userId;
  }
}
