package dgtc.utils;

import org.apache.http.util.TextUtils;

public class ValidUtils {
  public static boolean oneOfThemEmpty(String... args) {
    for (String arg : args) {
      if (TextUtils.isEmpty(arg)) {
        return true;
      }
    }

    return false;
  }

  public static boolean oneOfThemNull(Object... args) {
    for (Object arg : args) {
      if (arg == null) {
        return true;
      }
    }

    return false;
  }

  public static boolean shouldUpdate(Object newObj) {
    if (newObj == null) {
      return false;
    } else {
      return true;
    }
  }
}
