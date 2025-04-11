package table.eat.now.review.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class ReviewContent {

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "rating", nullable = false)
  private Integer rating;

  public static ReviewContent create(String content, Integer rating) {
    validateNull(content, rating);
    validateContent(content);
    validateRating(rating);
    return new ReviewContent(content, rating);
  }

  private static void validateNull(String content, Integer rating) {
    if (content == null || rating == null) {
      throw new IllegalArgumentException("내용과 평점은 null이 될 수 없습니다.");
    }
  }

  private static void validateContent(String content) {
    if (content.isBlank()) {
      throw new IllegalArgumentException("내용은 비어있을 수 없습니다.");
    }
  }

  private static void validateRating(Integer rating) {
    if (rating < 0 || rating > 5) {
      throw new IllegalArgumentException("평점은 0에서 5 사이의 값이어야 합니다.");
    }
  }

  private ReviewContent(String content, Integer rating) {
    this.content = content;
    this.rating = rating;
  }

  protected ReviewContent() {
  }
}
