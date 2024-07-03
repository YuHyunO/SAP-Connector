package lab.access.sap.server;

import java.util.Properties;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
public interface JCoServerPropsAccessor {
    public Properties getServerProperties(String name);
}
