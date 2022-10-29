package dgtc.entity.datasource.user;

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
@Table(name = "user_registration")
@NoArgsConstructor
public class UserRegistration implements Serializable {
  @Id
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "resend_opt_times")
  private int resendOptTimes;

  @Column(name = "max_resend_opt_times")
  private int maxResendOptTimes;

  @Column(name = "resend_opt_date")
  private Long resendOptDate;

  @Column(name = "active_fail_times")
  private int activeFailTimes;

  @Column(name = "max_active_fail_times")
  private int maxActiveFailTimes;

  @Column(name = "active_fail_date")
  private Long activeFailDate;

  @Column(name = "login_fail_times")
  private int loginFailTimes;

  @Column(name = "max_login_fail_times")
  private int maxLoginFailTimes;

  @Column(name = "login_fail_date")
  private Long loginFailDate;

  @Column(name = "send_password_times")
  private int sendPasswordTimes;

  @Column(name = "send_password_date")
  private Long sendPasswordDate;

  @Column(name = "max_send_password_times")
  private int maxSendPasswordTimes;

  //    @OneToOne
  //    @PrimaryKeyJoinColumn
  //    private User user;

  public static UserRegistration create(User user) {
    UserRegistration userRegistration = new UserRegistration();
    userRegistration.setUserId(user.getUserId());

    userRegistration.setMaxActiveFailTimes(3);
    userRegistration.setActiveFailDate(0L);
    userRegistration.setActiveFailTimes(0);

    userRegistration.setMaxLoginFailTimes(10);
    userRegistration.setLoginFailDate(0L);
    userRegistration.setLoginFailTimes(0);

    userRegistration.setMaxResendOptTimes(3);
    userRegistration.setResendOptDate(0L);
    userRegistration.setResendOptTimes(0);

    userRegistration.setSendPasswordDate(0L);
    userRegistration.setSendPasswordTimes(0);
    userRegistration.setMaxSendPasswordTimes(3);

    return userRegistration;
  }
}
