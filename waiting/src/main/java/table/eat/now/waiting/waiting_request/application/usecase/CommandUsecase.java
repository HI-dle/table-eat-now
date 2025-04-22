package table.eat.now.waiting.waiting_request.application.usecase;

import table.eat.now.waiting.waiting_request.application.usecase.dto.command.Command;

public interface CommandUsecase<C extends Command, R> {
  R execute(C command);
  Class<? extends Command> getCommandClass();
}
