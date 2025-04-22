package table.eat.now.waiting.waiting_request.application.router;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import table.eat.now.waiting.waiting_request.application.usecase.CommandUsecase;
import table.eat.now.waiting.waiting_request.application.usecase.QueryUsecase;
import table.eat.now.waiting.waiting_request.application.usecase.dto.command.Command;
import table.eat.now.waiting.waiting_request.application.usecase.dto.query.Query;

@Component
public class UsecaseRouter {

  private final Map<Class<? extends Command>, CommandUsecase<?, ?>> commandHandlers;
  private final Map<Class<? extends Query>, QueryUsecase<?, ?>> queryHandlers;

  public UsecaseRouter(List<CommandUsecase<?, ?>> commandUsecases, List<QueryUsecase<?, ?>> queryUsecases) {
    this.commandHandlers = commandUsecases.stream().collect(Collectors.toMap(
        CommandUsecase::getCommandClass,
        Function.identity()
    ));
    this.queryHandlers = queryUsecases.stream().collect(Collectors.toMap(
        QueryUsecase::getQueryClass,
        Function.identity()
    ));
  }

  public <C extends Command, R> R execute(C command) {
    CommandUsecase<C, R> handler = (CommandUsecase<C, R>) commandHandlers.get(command.getClass());
    if (handler == null) {
      throw new IllegalArgumentException("해당하는 Command 핸들러를 찾을 수 없습니다: " + command.getClass().getName());
    }
    return handler.execute(command);
  }

  public <Q extends Query, R> R execute(Q query) {
    QueryUsecase<Q, R> handler = (QueryUsecase<Q, R>) queryHandlers.get(query.getClass());
    if (handler == null) {
      throw new IllegalArgumentException("해당하는 Query 핸들러를 찾을 수 없습니다: " + query.getClass().getName());
    }
    return handler.execute(query);
  }
}
