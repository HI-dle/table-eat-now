package table.eat.now.promotion.promotion.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotion.promotion.domain.entity.Promotion;
import table.eat.now.promotion.promotion.domain.entity.repository.PromotionRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaPromotionRepositoryRepository extends JpaRepository<Promotion, Long>,
    PromotionRepository, JpaPromotionRepositoryCustom {

  Optional<Promotion> findByPromotionUuidAndDeletedByIsNull(String promotionUuid);
  List<Promotion> findAllByPromotionUuidInAndDeletedByIsNull(Set<String> promotionUuids);


}
