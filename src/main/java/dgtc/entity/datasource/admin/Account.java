package dgtc.entity.datasource.admin;

import dgtc.entity.dto.handler.CreateAccountRequestData;
import dgtc.entity.dto.handler.EditAccountRequestData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/** @author hunglv */
@Getter
@Setter
@Entity
@Table(name = "account")
@NoArgsConstructor
public class Account implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "domain_name")
  private String domainName;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "email")
  private String email;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "ip_login")
  private String ipLogin;

  @Column(name = "last_login")
  private Long lastLogin;

  @Column(name = "updated_date")
  private Long updatedDate;

  public Account(CreateAccountRequestData request) {
    this.domainName = request.getDomainName();
    this.displayName = request.getDisplayName();
    this.email = request.getEmail();
    this.isActive = true;
    this.ipLogin = "";
    this.lastLogin = 0L;
    this.updatedDate = System.currentTimeMillis();
  }

  public Account(String domain, EditAccountRequestData request) {
    this.domainName = domain;
    this.displayName = request.getDisplayName();
    this.email = request.getEmail();
    this.isActive = request.isActive();
    this.ipLogin = "";
    this.lastLogin = 0L;
    this.updatedDate = System.currentTimeMillis();
  }
}
