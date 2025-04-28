package table.eat.now.review.helper;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
@RequiredArgsConstructor
public class DatabaseCleanUp {

  private final EntityManager entityManager;
  private List<String> tableNames;

  @PostConstruct
  public void init() {
    tableNames = entityManager.getMetamodel()
        .getEntities()
        .stream()
        .filter(entityType -> entityType
            .getJavaType()
            .getAnnotation(Entity.class) != null)
        .map(entityType -> {
          Table table = entityType
              .getJavaType()
              .getAnnotation(Table.class);

          return Objects.nonNull(table) && Objects.nonNull(table.name()) ?
              table.name() :
              convertToLowerUnderscore(entityType.getName());
        })
        .toList();
  }

  @Transactional
  public void execute() {
    // 쓰기 지연 저장소에 남은 SQL을 마저 수행
    entityManager.flush();
    // 연관 관계 매핑된 테이블이 있는 경우 참조 무결성을 해제해주고, TRUNCATE 수행
    withH2(() ->
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()
    );

    for (String tableName : tableNames) {
      // 테이블 이름을 순회하면서, TRUNCATE 수행
      entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
    }

    withH2(() ->
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()
    );
  }

  public void withH2(Runnable task) {
    try {
      task.run();
    } catch (Exception e) {
      //H2 가 아닌경우 무시합니다.
    }
  }

  private String convertToLowerUnderscore(String camelCase) {
    return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
  }
}
