package dgtc.entity.datasource.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "bank_info")
@NoArgsConstructor
public class BankInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "account_number")
  private String accountNumber;

  @Column(name = "name_account")
  private String nameAccount;

  @Column(name = "branch")
  private String branch;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "updated_date")
  private Long updatedDate;

  @Column(name = "created_date")
  private Long createdDate;

  public static BankInfo create(String accountNumber, String nameAccount, String branch) {
    BankInfo bankInfo = new BankInfo();
    bankInfo.setAccountNumber(accountNumber);
    bankInfo.setNameAccount(nameAccount);
    bankInfo.setBranch(branch);
    bankInfo.setIsActive(false);
    Long now = System.currentTimeMillis();
    bankInfo.setUpdatedDate(now);
    bankInfo.setCreatedDate(now);
    return bankInfo;
  }
}
