package table.eat.now.review.application.service.dto.request;

import table.eat.now.common.resolver.dto.CurrentUserInfoDto;
import table.eat.now.review.domain.entity.ReviewContent;
import table.eat.now.review.domain.entity.UpdateContent;

public record UpdateReviewCommand (
		String content, Integer rating, CurrentUserInfoDto userInfo){

	public UpdateContent toEntity() {
		return new UpdateContent(
				ReviewContent.create(content, rating), userInfo.userId(), userInfo.role().name()
		);
	}
}
