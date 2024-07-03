package lab.access.sap.server;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
@Slf4j
public class InMemoryJCoServerPropsAccessor implements JCoServerPropsAccessor {
    private static Map<String, Properties> serverPropsStorage;
    @Override
    public Properties getServerProperties(String name) {
        return serverPropsStorage.get(name);
    }

    public void setServerPropsStorage(Map<String, SAPJCoServerFactory> serverProps) {
        if (serverPropsStorage == null) {
            serverPropsStorage = new HashMap<>();
            Set<String> names = serverProps.keySet();
            for (String name : names) {
                serverPropsStorage.put(name, (serverProps.get(name)).toProperties());
            }
        }
    }

}
