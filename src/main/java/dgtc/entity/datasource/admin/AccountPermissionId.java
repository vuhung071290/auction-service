package dgtc.entity.datasource.admin;

import lombok.*;

import javax.persistence.Column;
import java.io.Serializable;

/** @author hunglv */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class AccountPermissionId implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "domain_name")
  private String domainName;

  @Column(name = "feature_action_id")
  private String featureActionId;
}
