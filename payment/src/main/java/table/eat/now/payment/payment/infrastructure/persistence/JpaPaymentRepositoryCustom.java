package table.eat.now.payment.payment.infrastructure.persistence;

import table.eat.now.payment.payment.domain.repository.search.PaginatedResult;
import table.eat.now.payment.payment.domain.repository.search.SearchMyPaymentsCriteria;
import table.eat.now.payment.payment.domain.repository.search.SearchMyPaymentsResult;

public interface JpaPaymentRepositoryCustom {

  PaginatedResult<SearchMyPaymentsResult> searchMyPayments(SearchMyPaymentsCriteria criteria);

}
