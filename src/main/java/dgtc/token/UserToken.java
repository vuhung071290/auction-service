package dgtc.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserToken {
  private String role;
  private Long userId;
  private Date expireTime;
  private Boolean isExpire;
  private Boolean isBlackList;

  public static UserToken create(Long userId, String role, boolean isExpire, boolean isBlackList) {
    UserToken newIns = new UserToken();
    newIns.setExpireTime(new Date());
    newIns.setUserId(userId);
    newIns.setRole(role);
    newIns.setIsExpire(isExpire);
    newIns.setIsBlackList(isBlackList);
    return newIns;
  }

  public boolean isInValid() {
    return isExpire || isBlackList;
  }
}
