package lab.access.sap.client;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.Set;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
@Slf4j
public class SAPJCoDestinationProvider implements DestinationDataProvider {
    private static SAPJCoDestinationProvider destProvider;
    private static JCoDestinationAccessor destAccessor;

    public SAPJCoDestinationProvider() {
        destProvider = this;
    }

    public static SAPJCoDestinationProvider getInstance() {
        if (destProvider != null) {
            return destProvider;
        } else {
            return null;
        }
    }

    @Override
    public Properties getDestinationProperties(String name) throws DataProviderException {
        return destAccessor.getDesitinationProperties(name);
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {

    }

    public void setDestinationAccessor(JCoDestinationAccessor destAccessor) {
        this.destAccessor = destAccessor;
    }

    public void register() {
        Environment.registerDestinationDataProvider(destProvider);
    }

    public static String toString(String name) {
        StringBuilder builder = new StringBuilder();
        Properties props = destAccessor.getDesitinationProperties(name);
        Set<String> names = props.stringPropertyNames();
        for (String name0 : names) {
            builder.append("-" + name0 + "=" + props.get(name0) + "\n");
        }
        builder.setLength(builder.length() - "\n".length());
        return builder.toString();
    }
}
