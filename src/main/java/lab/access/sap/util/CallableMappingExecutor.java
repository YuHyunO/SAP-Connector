package lab.access.sap.util;

import lab.access.sap.util.method.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
@Slf4j
public class CallableMappingExecutor {
    public static final int FROM = 100;
    public static final int TO = 101;
    public static final int BEGIN = 102;

    private static Map<String, Map<Class, Class[]>> callableMethods = new HashMap<>();

    static {
        callableMethods.put(StringUtil.SUB_STRING, mapOf(StringUtil.class, new Class[]{String.class, int.class, int.class}));
        callableMethods.put(StringUtil.REPLACE, mapOf(StringUtil.class, new Class[]{String.class, String.class, String.class}));
    }

    public static Object mapValue(String methodName, Object value, Map<Integer, String> args) {
        Map<Class, Class[]> map = callableMethods.get(methodName);
        if (map == null) {
            throw new IllegalStateException("Not supported mapper method '" + methodName + "'");
        }
        Class clazz = null;
        for (Class clazz1 : map.keySet()) {
            clazz = clazz1;
            break;
        }
        try {
            Method method = clazz.getDeclaredMethod(methodName, map.get(clazz));
            if (StringUtil.SUB_STRING.equals(methodName)) {
                String begin = args.get(BEGIN);
                String end = args.get(TO);
                int beginIdx = 0;
                int endIdx = 0;

                if (begin == null) {
                    beginIdx = 0;
                } else {
                    beginIdx = Integer.parseInt(args.get(BEGIN));
                }
                if (end == null) {
                    endIdx = value.toString().length();
                } else {
                    endIdx = Integer.parseInt(args.get(TO));
                }

                return method.invoke(null, value.toString(), beginIdx, endIdx);
            } else if (StringUtil.REPLACE.equals(methodName)) {
                String oldChar = args.get(FROM);
                String newChar = args.get(TO);
                return method.invoke(null, value.toString(), oldChar, newChar);
            }

        } catch (NoSuchMethodException nme) {
            log.error("", nme);
            throw new IllegalStateException("Not supported mapper method '" + methodName + "'");
        } catch (Exception e) {
            log.error("", e);
            throw new IllegalStateException(e.getMessage());
        }
        return null;
    }

    private static Map<Class, Class[]> mapOf(Class clazz, Class[] argsClass) {
        Map<Class, Class[]> map = new HashMap<>();
        map.put(clazz, argsClass);
        return map;
    }

}
