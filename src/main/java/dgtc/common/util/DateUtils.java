package dgtc.common.util;

import java.text.SimpleDateFormat;

/** @author hunglv */
public class DateUtils {

  public static String currentTimeStampToString(long timeStamp, String format) {
    SimpleDateFormat sdfDate = new SimpleDateFormat(format);
    String strDate = sdfDate.format(timeStamp);
    return strDate;
  }
}
