package table.eat.now.waiting.waiting_request.application.usecase;

import table.eat.now.waiting.waiting_request.application.usecase.dto.query.Query;

public interface QueryUsecase<Q extends Query, R> {
  R execute(Q query);
  Class<? extends Query> getQueryClass();
}
