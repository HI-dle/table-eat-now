package table.eat.now.common.resolver.dto;

public record CurrentUserInfoDto(Long userId, UserRole role) {

  public static CurrentUserInfoDto of(Long userId, UserRole role) {
    return new CurrentUserInfoDto(userId, role);
  }
}
