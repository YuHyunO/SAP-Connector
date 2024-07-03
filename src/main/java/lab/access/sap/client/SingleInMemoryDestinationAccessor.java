package lab.access.sap.client;

import java.util.Properties;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
public class SingleInMemoryDestinationAccessor implements JCoDestinationAccessor {

    private static Properties properties;
    @Override
    public Properties getDesitinationProperties(String name) {
        return properties;
    }

    public void setDestination(SAPJCoDestinationFactory destination) {
        properties = destination.toProperties();
    }
}
