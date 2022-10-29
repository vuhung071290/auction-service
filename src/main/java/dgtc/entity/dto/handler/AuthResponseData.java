package dgtc.entity.dto.handler;

import dgtc.entity.datasource.admin.Account;
import dgtc.entity.datasource.admin.AccountPermission;
import dgtc.entity.dto.Token;
import lombok.Data;

import java.util.List;

/** @author hunglv */
@Data
public class AuthResponseData {
  private String domainName;
  private String displayName;
  private String email;
  private boolean isActive;
  private long updatedDate;
  private String token;
  private long expireTime;
  private List<AccountPermission> permissions;

  public AuthResponseData(Account accInfo, List<AccountPermission> permissions, Token token) {
    this.domainName = accInfo.getDomainName();
    this.displayName = accInfo.getDisplayName();
    this.email = accInfo.getEmail();
    this.isActive = accInfo.getIsActive();
    this.updatedDate = accInfo.getUpdatedDate();
    this.token = token.getToken();
    this.expireTime = token.getExpireTime();
    this.permissions = permissions;
  }
}
