package table.eat.now.review.presentation.dto.response;

import java.util.List;
import table.eat.now.review.application.service.dto.response.PaginatedInfo;
import table.eat.now.review.application.service.dto.response.SearchReviewInfo;

public record PaginatedResponse<T>(
		List<T> content,
		int page,
		int size,
		Long totalElements,
		int totalPages) {

	public static PaginatedResponse<SearchReviewResponse> from(
			PaginatedInfo<SearchReviewInfo> info) {

		return new PaginatedResponse<>(
				info.content().stream()
						.map(SearchReviewResponse::from)
						.toList(),
				info.page(),
				info.size(),
				info.totalElements(),
				info.totalPages()
		);
	}
}