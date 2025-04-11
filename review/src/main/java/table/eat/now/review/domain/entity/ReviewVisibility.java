package table.eat.now.review.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class ReviewVisibility {

  @Column(name = "is_visible", nullable = false)
  @Getter(AccessLevel.NONE)
  private Boolean isVisible;

  @Column(name = "hidden_by")
  private Long hiddenBy;

  @Column(name = "hidden_at")
  private LocalDateTime hiddenAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "hidden_by_role")
  private HiddenByRole hiddenByRole;

  public static ReviewVisibility create(Boolean isVisible, Long hiddenBy, String hiddenByRole) {
    validateVisibility(isVisible);
    return isVisible ?
        createVisible() : createHidden(hiddenBy, hiddenByRole);
  }

  private static void validateVisibility(Boolean isVisible) {
    if (isVisible == null) {
      throw new IllegalArgumentException("공개 여부는 null이 될 수 없습니다.");
    }
  }

  private static ReviewVisibility createVisible() {
    return new ReviewVisibility(true, null, null, null);
  }

  private static ReviewVisibility createHidden(Long hiddenBy, String hiddenByRole) {
    validateUserInfo(hiddenBy, hiddenByRole);
    return new ReviewVisibility(false, hiddenBy, LocalDateTime.now(), hiddenByRole);
  }

  private static void validateUserInfo(Long hiddenBy, String hiddenByRole) {
    if (hiddenBy == null || hiddenByRole == null) {
      throw new IllegalArgumentException("사용자 ID와 권한은 null이 될 수 없습니다.");
    }
  }

  public ReviewVisibility hide(Long hiddenBy, String hiddenByRole) {
    return this.isVisible ?
        createHidden(hiddenBy, hiddenByRole) : this;
  }

  public ReviewVisibility show(String hiddenByRole) {
    if (this.isVisible) {
      return this;
    }
    validateShowAuthority(HiddenByRole.from(hiddenByRole));
    return createVisible();
  }

  private void validateShowAuthority(HiddenByRole requesterRole) {
    if (this.hiddenByRole.isAdmin() && !requesterRole.isAdmin()) {
      throw new IllegalArgumentException("관리자가 숨긴 리뷰는 일반 사용자가 공개할 수 없습니다.");
    }
  }

  private ReviewVisibility(
      Boolean isVisible, Long hiddenBy, LocalDateTime hiddenAt, String hiddenByRole) {
    this.isVisible = isVisible;
    this.hiddenBy = hiddenBy;
    this.hiddenAt = hiddenAt;
    this.hiddenByRole = HiddenByRole.from(hiddenByRole);
  }

  public boolean isVisible() {
    return this.isVisible;
  }

  protected ReviewVisibility() {
  }

  enum HiddenByRole {
    MASTER,
    OWNER,
    STAFF,
    CUSTOMER,
    ;

    private boolean isAdmin() {
      return this == MASTER || this == OWNER || this == STAFF;
    }

    private static HiddenByRole from(String name) {
      try {
        return name == null ? null : valueOf(name.toUpperCase());
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("유효하지 않은 권한 입니다: " + name);
      }
    }
  }
}
