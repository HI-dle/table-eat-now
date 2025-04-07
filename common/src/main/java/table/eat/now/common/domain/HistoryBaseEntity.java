package table.eat.now.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class HistoryBaseEntity implements Serializable {

  @CreatedBy
  @Column(updatable = false)
  private Long createdBy;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;
}
