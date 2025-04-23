package table.eat.now.review.testdata;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import table.eat.now.review.domain.entity.Review;
import table.eat.now.review.domain.entity.ReviewContent;
import table.eat.now.review.domain.entity.ReviewReference;
import table.eat.now.review.domain.entity.ReviewVisibility;
import table.eat.now.review.domain.entity.ServiceType;
import table.eat.now.review.domain.repository.ReviewRepository;

@SpringBootTest
@DisplayName("리뷰 테스트 데이터를 수동으로 생성합니다.")
@Disabled
public class ReviewDataGenerator {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private EntityManager entityManager;

  private static final int BATCH_SIZE = 1000;
  private static final int TOTAL_RECORDS = 1_000_000;
  private static final Random random = new Random();

  @DisplayName("리뷰 더미 데이터 생성")
  @Nested
  class CreateReviewTestData {

    @DisplayName("리뷰데이터 100만건 생성")
    @Test
    void generateMillionReviews() {
      List<String> restaurantIds = generateIds(500);
      List<String> serviceIds = generateIds(5000);

      List<String> reviewContents = List.of(
          "정말 맛있었어요. 다음에 또 방문하고 싶습니다.",
          "서비스가 친절하고 음식도 맛있었습니다.",
          "기대했던 것보다는 조금 실망스러웠습니다.",
          "가격 대비 맛과 양이 훌륭했습니다.",
          "분위기가 좋고 음식도 맛있어요.",
          "대기 시간이 조금 길었지만 음식은 맛있었습니다.",
          "직원들이 매우 친절하고 음식도 맛있었어요.",
          "음식이 나오는 시간이 조금 오래 걸렸습니다.",
          "가격이 조금 비싸지만 그만큼 가치가 있었어요.",
          "특별한 날에 방문하기 좋은 곳입니다."
      );

      for (int i = 0; i < TOTAL_RECORDS; i += BATCH_SIZE) {
        List<Review> reviewBatch = new ArrayList<>(BATCH_SIZE);
        int batchEnd = Math.min(i + BATCH_SIZE, TOTAL_RECORDS);

        for (int j = i; j < batchEnd; j++) {
          String restaurantId = restaurantIds.get(random.nextInt(restaurantIds.size()));
          String serviceId = serviceIds.get(random.nextInt(serviceIds.size()));
          Long customerId = 10000L + random.nextInt(10000);
          ServiceType serviceType = random.nextBoolean() ? ServiceType.WAITING : ServiceType.RESERVATION;
          String content = reviewContents.get(random.nextInt(reviewContents.size()));
          int rating = random.nextInt(5) + 1;
          boolean isVisible = random.nextBoolean();

          ReviewReference reference = ReviewReference.create(
              restaurantId, serviceId, customerId, serviceType);

          ReviewContent reviewContent = ReviewContent.create(
              content, rating);

          ReviewVisibility visibility = ReviewVisibility.create(
              isVisible,
              isVisible ? null : customerId,
              isVisible ? null : (random.nextBoolean() ? "CUSTOMER" : "MASTER")
          );

          Review review = Review.create(reference, reviewContent, visibility);
          reviewBatch.add(review);
        }

        reviewRepository.saveAllAndFlush(reviewBatch);
        entityManager.clear();
      }
    }
  }

  private List<String> generateIds(int count) {
    List<String> ids = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      ids.add(UUID.randomUUID().toString());
    }
    return ids;
  }
}
