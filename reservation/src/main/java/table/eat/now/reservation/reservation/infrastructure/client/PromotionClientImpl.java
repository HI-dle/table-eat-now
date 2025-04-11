/**
 * @author : jieun(je-pa)
 * @Date : 2025. 04. 10.
 */
package table.eat.now.reservation.reservation.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import table.eat.now.reservation.reservation.application.client.PromotionClient;
import table.eat.now.reservation.reservation.application.client.dto.request.GetPromotionsCriteria;
import table.eat.now.reservation.reservation.application.client.dto.response.GetPromotionsInfo;
import table.eat.now.reservation.reservation.infrastructure.client.feign.PromotionFeignClient;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.request.GetPromotionsRequest;
import table.eat.now.reservation.reservation.infrastructure.client.feign.dto.response.GetPromotionsResponse;

@Component
@RequiredArgsConstructor
public class PromotionClientImpl implements PromotionClient {
  private final PromotionFeignClient promotionFeignClient;

  @Override
  public GetPromotionsInfo getPromotions(GetPromotionsCriteria request) {
    GetPromotionsResponse response = promotionFeignClient.getPromotions(
        GetPromotionsRequest.from(request)).getBody();
    return response.toInfo();
  }
}
