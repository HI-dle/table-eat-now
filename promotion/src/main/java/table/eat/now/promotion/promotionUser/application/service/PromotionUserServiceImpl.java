package table.eat.now.promotion.promotionUser.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import table.eat.now.common.exception.CustomException;
import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.promotion.promotionUser.application.dto.PaginatedResultCommand;
import table.eat.now.promotion.promotionUser.application.dto.request.CreatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.request.SearchPromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.request.UpdatePromotionUserCommand;
import table.eat.now.promotion.promotionUser.application.dto.response.CreatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.dto.response.SearchPromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.dto.response.UpdatePromotionUserInfo;
import table.eat.now.promotion.promotionUser.application.exception.PromotionUserErrorCode;
import table.eat.now.promotion.promotionUser.domain.entity.PromotionUser;
import table.eat.now.promotion.promotionUser.domain.repository.PromotionUserRepository;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 08.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionUserServiceImpl implements PromotionUserService{

  private final PromotionUserRepository promotionUserRepository;


  @Override
  public CreatePromotionUserInfo createPromotionUser(CreatePromotionUserCommand command) {
    return CreatePromotionUserInfo.from(promotionUserRepository.save(command.toEntity()));
  }

  @Override
  public UpdatePromotionUserInfo updatePromotionUser(UpdatePromotionUserCommand command,
      String promotionUserUuid) {
    PromotionUser promotionUser = findByPromotionUser(promotionUserUuid);
    promotionUser.modifyPromotionUser(command.userId(), command.promotionUuid());
    return UpdatePromotionUserInfo.from(promotionUser);
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedResultCommand<SearchPromotionUserInfo> searchPromotionUser(
      SearchPromotionUserCommand command) {
    return PaginatedResultCommand.from(
        promotionUserRepository.searchPromotionUser(command.toCriteria()));
  }

  @Override
  @Transactional
  public void deletePromotionUser(Long userId, CurrentUserInfoDto userInfoDto) {
    PromotionUser promotionUser = findByPromotionUserFromUserId(userId);
    promotionUser.delete(userInfoDto.userId());
    log.info("삭제가 완료 되었습니다. 삭제한 userId: {}", promotionUser);
  }

  private PromotionUser findByPromotionUser(String promotionUserUuid) {
    return promotionUserRepository.findByPromotionUserUuidAndDeletedAtIsNull(
        promotionUserUuid).orElseThrow(() ->
        CustomException.from(PromotionUserErrorCode.INVALID_PROMOTION_USER_UUID));
  }

  private PromotionUser findByPromotionUserFromUserId(Long userId) {
    return promotionUserRepository.findByUserIdAndDeletedAtIsNull(userId)
        .orElseThrow(() ->
            CustomException.from(PromotionUserErrorCode.INVALID_PROMOTION_USER_UUID));
  }
}
