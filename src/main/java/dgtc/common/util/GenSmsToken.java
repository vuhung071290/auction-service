package dgtc.common.util;

import java.util.Random;

public class GenSmsToken {
  public static String genToken() {
    Random random = new Random();
    int otp = 100000 + random.nextInt(900000);
    return String.valueOf(otp);
  }
}
