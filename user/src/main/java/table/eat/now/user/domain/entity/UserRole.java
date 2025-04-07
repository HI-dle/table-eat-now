package table.eat.now.user.domain.entity;

/**
 * @author : hanjihoon
 * @Date : 2025. 04. 07.
 */
public enum UserRole {

  CUSTOMER(Authority.CUSTOMER),
  OWNER(Authority.OWNER),
  STAFF(Authority.STAFF),
  MASTER(Authority.MASTER),
  ADMIN(Authority.ADMIN);

  private final String authority;

  UserRole(String authority) {
    this.authority = authority;
  }

  public String getAuthority() {
    return this.authority;
  }

  public static class Authority {
    public static final String CUSTOMER = "CUSTOMER";
    public static final String OWNER = "OWNER";
    public static final String STAFF = "STAFF";
    public static final String MASTER = "MASTER";
    public static final String ADMIN = "ADMIN";
  }
}
