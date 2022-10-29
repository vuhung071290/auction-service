package dgtc.entity.datasource.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/** @author hunglv */
@Getter
@Setter
@Entity
@Table(name = "account_permission")
@IdClass(AccountPermissionId.class)
@NoArgsConstructor
@AllArgsConstructor
public class AccountPermission implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "domain_name")
  private String domainName;

  @Id
  @Column(name = "feature_action_id")
  private String featureActionId;

  @Column(name = "feature_id")
  private String featureId;
}
