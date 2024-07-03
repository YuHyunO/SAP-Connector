package lab.access.sap.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
public class InMemoryJCoDestinationAccessor implements JCoDestinationAccessor {
    private static Map<String, Properties> destinationStorage;

    @Override
    public Properties getDesitinationProperties(String name) {
        return destinationStorage.get(name);
    }

    public void setDestinationStorage(Map<String, SAPJCoDestinationFactory> destinations) {
        if (destinationStorage == null) {
            destinationStorage = new HashMap<>();
            Set<String> names = destinations.keySet();
            for (String name : names) {
                destinationStorage.put(name, (destinations.get(name)).toProperties());
            }
        }
    }
}
