package lab.access.sap.util.method;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtil {
    public static final String SUB_STRING = "subString";
    public static final String REPLACE = "replace";

    public static String subString(String str, int beginIndex, int endIndex) {
        try {
            return str.substring(beginIndex, endIndex);
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    public static String replace(String str, String oldChar, String newChar) {
        try {
            log.info("'{}':'{}'", oldChar, newChar);
            return str.replace(oldChar, newChar);
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

}
