package dgtc.entity.datasource.admin;

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
@Table(name = "account_activity")
@NoArgsConstructor
public class AccountActivity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id private long id;

  @Column(name = "domain_name")
  private String domainName;

  @Column(name = "timestamp")
  private Long timestamp;

  @Column(name = "action_code")
  private Integer actionCode;

  @Column(name = "description")
  private String description;
}
