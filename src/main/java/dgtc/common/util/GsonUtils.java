package dgtc.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {
  private static final Gson GSON;

  static {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    GSON = gsonBuilder.disableHtmlEscaping().create();
  }

  public static String toJson(Object obj) {
    return GSON.toJson(obj);
  }

  public static <T> T fromJson(String sJson, Class<T> t) {
    return GSON.fromJson(sJson, t);
  }
}
