package table.eat.now.payment.payment.application.event;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MapperUtilTest {

  @Test
  void toJsonNode_는_BigDecimal을_일반숫자로_변환할_수_있다() {
    // given
    TestPayload dto = new TestPayload();
    dto.setAmount(new BigDecimal("45000"));

    // when
    JsonNode jsonNode = MapperUtil.toJsonNode(dto);

    // then
    assertNotNull(jsonNode);
    assertEquals("45000", jsonNode.get("amount").asText());
  }

  @Test
  void toJsonNode_는_날짜를_ISO_문자열로_변환할_수_있다() {
    // given
    TestPayload dto = new TestPayload();
    dto.setCreatedAt(LocalDateTime.of(2025, 4, 15, 1, 49, 51));

    // when
    JsonNode jsonNode = MapperUtil.toJsonNode(dto);

    // then
    assertNotNull(jsonNode);
    assertEquals("2025-04-15T01:49:51", jsonNode.get("createdAt").asText());
  }

  @Test
  void toJsonNode_는_변환할수없는객체에_예외를던진다() {
    // given
    Object invalidObject = new Object();

    // when & then
    Exception exception = assertThrows(RuntimeException.class, () -> {
      MapperUtil.toJsonNode(invalidObject);
    });

    assertThat(exception.getMessage()).contains("Failed to convert object to JsonNode");
  }

  static class TestPayload {

    private BigDecimal amount;
    private LocalDateTime createdAt;

    public BigDecimal getAmount() {
      return amount;
    }

    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
      return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
    }
  }
}