package table.eat.now.promotion.promotionuser.application.service;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotionuser.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionuser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionuser.application.dto.request.SearchPromotionUserCommand;
import table.eat.now.promotion.promotionuser.application.dto.request.UpdatePromotionUserCommand;
import table.eat.now.promotion.promotionuser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionuser.application.dto.response.SearchPromotionUserInfo;
import table.eat.now.promotion.promotionuser.application.dto.response.UpdatePromotionUserInfo;
import table.eat.now.promotion.promotionuser.application.event.dto.PromotionUserSaveEventInfo;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
public interface PromotionUserService {

  CreatePromotionUserInfo createPromotionUser(CreatePromotionUserCommand command);

  UpdatePromotionUserInfo updatePromotionUser(
      UpdatePromotionUserCommand command, String promotionUserUuid);

  PaginatedResultCommand<SearchPromotionUserInfo> searchPromotionUser(
      SearchPromotionUserCommand command);

  void deletePromotionUser(Long userId, CurrentUserInfoDto userInfoDto);

  void savePromotionUsers(PromotionUserSaveEventInfo promotionUserSaveEventInfos);
}
