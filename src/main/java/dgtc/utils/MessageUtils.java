package dgtc.utils;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

public class MessageUtils {
  private static final ReloadableResourceBundleMessageSource MESSAGE_SOURCE;

  static {
    MESSAGE_SOURCE = new ReloadableResourceBundleMessageSource();
    MESSAGE_SOURCE.setBasename("classpath:messages_vn");
    MESSAGE_SOURCE.setDefaultEncoding("UTF-8");
  }

  public static ReloadableResourceBundleMessageSource instance() {
    return MESSAGE_SOURCE;
  }

  public static String message(String resource) {
    return MESSAGE_SOURCE.getMessage(resource, null, "", Locale.getDefault());
  }

  public static String message(String resource, Object... args) {
    String mess = MESSAGE_SOURCE.getMessage(resource, null, "", Locale.getDefault());
    if (mess != null) {
      return String.format(mess, args);
    }
    return "";
  }
}
