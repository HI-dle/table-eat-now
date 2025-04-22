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
    return ((CommandUsecase<C, R>) commandHandlers.get(command.getClass())).execute(command);
  }

  public <Q extends Query, R> R execute(Q query) {
    return ((QueryUsecase<Q, R>) queryHandlers.get(query.getClass())).execute(query);
  }
}
