package table.eat.now.payment.payment.infrastructure.persistence;

import table.eat.now.payment.payment.domain.repository.search.PaginatedResult;
import table.eat.now.payment.payment.domain.repository.search.SearchPaymentsCriteria;
import table.eat.now.payment.payment.domain.repository.search.SearchPaymentsResult;

public interface JpaPaymentRepositoryCustom {

  PaginatedResult<SearchPaymentsResult> searchPayments(SearchPaymentsCriteria criteria);

}
