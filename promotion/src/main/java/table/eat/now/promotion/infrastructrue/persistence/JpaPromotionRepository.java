package table.eat.now.promotion.infrastructrue.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import table.eat.now.promotion.domain.entity.Promotion;
import table.eat.now.promotion.domain.entity.repository.PromotionRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface JpaPromotionRepository extends JpaRepository<Promotion, Long>, PromotionRepository {


}
