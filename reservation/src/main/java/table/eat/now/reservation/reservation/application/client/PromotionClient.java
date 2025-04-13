/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.application.client;

import org.springframework.web.bind.annotation.RequestBody;
import table.eat.now.reservation.reservation.application.client.dto.request.GetPromotionsCriteria;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;

public interface PromotionClient {
  GetPromotionsInfo getPromotions(@RequestBody GetPromotionsCriteria request);
}
