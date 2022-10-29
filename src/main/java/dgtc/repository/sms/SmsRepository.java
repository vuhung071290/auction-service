package dgtc.repository.sms;

import org.springframework.stereotype.Repository;

@Repository
public interface SmsRepository {
  public boolean sendSms(String phoneNumber, String message);
}
