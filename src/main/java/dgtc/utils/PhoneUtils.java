package dgtc.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneUtils {
  private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();

  public static String combinePhone(String phone) {
    try {
      Phonenumber.PhoneNumber phoneNumber = PHONE_NUMBER_UTIL.parse(phone, "VN");
      if (PHONE_NUMBER_UTIL.isValidNumber(phoneNumber)) {
        return String.format(
            "+%d%d", phoneNumber.getCountryCode(), phoneNumber.getNationalNumber());
      } else {
        return null;
      }
    } catch (NumberParseException e) {
      return null;
    }
  }
}
