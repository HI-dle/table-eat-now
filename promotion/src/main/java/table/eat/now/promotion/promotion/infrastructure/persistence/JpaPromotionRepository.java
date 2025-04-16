package table.eat.now.promotion.promotion.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotion.promotion.domain.entity.Promotion;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaPromotionRepository extends
    JpaRepository<Promotion, Long>, JpaPromotionRepositoryCustom {

  Optional<Promotion> findByPromotionUuidAndDeletedByIsNull(String promotionUuid);
  List<Promotion> findAllByPromotionUuidInAndDeletedByIsNull(Set<String> promotionUuids);


}
