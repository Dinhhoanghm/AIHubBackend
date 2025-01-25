package ongoing.backend.utils;

import org.apache.commons.text.StringEscapeUtils;

public class StringUtil {
  public static String decodeUnicode(String s) {
    return StringEscapeUtils.unescapeJava(s);
  }
}
