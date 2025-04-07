package table.eat.now.common.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import table.eat.now.common.resolver.dto.UserRole;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthCheck {
  UserRole[] roles()
      default {UserRole.MASTER, UserRole.OWNER, UserRole.STAFF, UserRole.CUSTOMER};
}