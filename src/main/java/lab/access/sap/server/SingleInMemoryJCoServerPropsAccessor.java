package lab.access.sap.server;

import java.util.Properties;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
public class SingleInMemoryJCoServerPropsAccessor implements JCoServerPropsAccessor {
    private static Properties properties;

    @Override
    public Properties getServerProperties(String name) {
        return properties;
    }

    public void setServerProperties(SAPJCoServerFactory serverProps) {
        properties = serverProps.toProperties();
    }
}
