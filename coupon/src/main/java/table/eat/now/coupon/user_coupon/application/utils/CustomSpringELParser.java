package table.eat.now.coupon.user_coupon.application.utils;

import java.util.Set;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {

  private CustomSpringELParser() {
    throw new IllegalStateException("Utility class");
  }

  public static Set<?> getDynamicValueSet(
      String spel, String[] parameterNames, Object[] args) {

    ExpressionParser parser = new SpelExpressionParser();
    StandardEvaluationContext context = getStandardEvaluationContext(parameterNames, args);

    return parser.parseExpression(spel).getValue(context, Set.class);
  }

  private static StandardEvaluationContext getStandardEvaluationContext(
      String[] parameterNames, Object[] args) {

    StandardEvaluationContext context = new StandardEvaluationContext();

    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }
    return context;
  }
}