package table.eat.now.coupon.user_coupon.application.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {

  private CustomSpringELParser() {
    throw new IllegalStateException("Utility class");
  }

  private static final ExpressionParser PARSER = new SpelExpressionParser();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static <T> T parseExpression(
      String spel, String[] parameterNames, Object[] args, TypeReference<T> typeReference) {

    try {
      StandardEvaluationContext context = getStandardEvaluationContext(parameterNames, args);
      Object rawValue = PARSER.parseExpression(spel).getValue(context, Object.class);
      return OBJECT_MAPPER.convertValue(rawValue, typeReference);
    } catch (Exception e) {
      throw new IllegalArgumentException("SpEL 표현식 평가 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  private static StandardEvaluationContext getStandardEvaluationContext(
      String[] parameterNames, Object[] args) {

    if (parameterNames == null || args == null) {
      throw new IllegalArgumentException("매개변수 이름이나 인자가 null일 수 없습니다");
    }
    if (parameterNames.length != args.length) {
      throw new IllegalArgumentException("매개변수 이름 배열과 인자 배열의 길이가 일치해야 합니다");
    }

    StandardEvaluationContext context = new StandardEvaluationContext();

    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }
    return context;
  }
}