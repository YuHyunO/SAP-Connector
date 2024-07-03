package lab.access.sap.server;

import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.ext.ServerDataEventListener;
import com.sap.conn.jco.ext.ServerDataProvider;

import java.util.Properties;
import java.util.Set;

public class MesimJCoServerProvider implements ServerDataProvider {
    private static MesimJCoServerProvider serverProvider;
    private static JCoServerPropsAccessor serverPropsAccessor;

    public MesimJCoServerProvider() {
        serverProvider = this;
    }

    public static MesimJCoServerProvider getInstance() {
        if (serverProvider != null) {
            return serverProvider;
        } else {
            return null;
        }
    }

    @Override
    public Properties getServerProperties(String name) {
        return serverPropsAccessor.getServerProperties(name);
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

    @Override
    public void setServerDataEventListener(ServerDataEventListener serverDataEventListener) {

    }

    public void setServerPropsAccessor(JCoServerPropsAccessor serverPropsAccessor) {
        this.serverPropsAccessor = serverPropsAccessor;
    }

    public void register() {
        Environment.registerServerDataProvider(serverProvider);
    }

    public static String toString(String name) {
        StringBuilder builder = new StringBuilder();
        Properties props = serverPropsAccessor.getServerProperties(name);
        Set<String> names = props.stringPropertyNames();
        for (String name0 : names) {
            builder.append("-" + name0 + "=" + props.get(name0) + "\n");
        }
        builder.setLength(builder.length() - "\n".length());
        return builder.toString();
    }

}
