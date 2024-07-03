package lab.access.sap.server;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

@Data
@Slf4j
public class SAPJCoServerFactory {
    protected Map<String, String> PROPERTY_MAP = new LinkedHashMap<>();

    public SAPJCoServerFactory() {

        PROPERTY_MAP.put("server_connection_count", "jco.server.connection_count");
        PROPERTY_MAP.put("server_gwhost", "jco.server.gwhost");
        PROPERTY_MAP.put("server_mshost", "jco.server.mshost"); //Needed for LB config
        PROPERTY_MAP.put("server_msserv", "jco.server.msserv"); //Needed for LB config
        PROPERTY_MAP.put("server_progid", "jco.server.progid"); //Needed for LB config
        PROPERTY_MAP.put("server_system_id", "jco.server.system_id"); //Needed for LB config
        PROPERTY_MAP.put("server_gwserv", "jco.server.gwserv");
        PROPERTY_MAP.put("server_repository_destination", "jco.server.repository_destination");
        PROPERTY_MAP.put("server_saprouter", "jco.server.saprouter");
        PROPERTY_MAP.put("server_max_startup_delay", "jco.server.max_startup_delay");
        PROPERTY_MAP.put("server_repository_map", "jco.server.repository_map");
        PROPERTY_MAP.put("server_trace", "jco.server.trace");
        PROPERTY_MAP.put("server_worker_thread_count", "jco.server.worker_thread_count");
        PROPERTY_MAP.put("server_worker_thread_min_count", "jco.server.worker_thread_min_count");
        PROPERTY_MAP.put("server_snc_mode", "jco.server.snc_mode"); // 1
        PROPERTY_MAP.put("server_snc_qop", "jco.server.snc_qop"); // Integer 1 ~ 9
        PROPERTY_MAP.put("server_snc_myname", "jco.server.snc_myname");
        PROPERTY_MAP.put("server_snc_lib", "jco.server.snc_lib");
        PROPERTY_MAP.put("server_lb_update_interval_millis", "jco.server.update_interval"); //default 300000
        PROPERTY_MAP.put("server_type", "jco.server.type"); //B:Load balance, R:Single gateway
        PROPERTY_MAP.put("server_group", "jco.server.group"); //Default when server type is B [application servers]
        PROPERTY_MAP.put("server_group_key", "jco.server.group_key");
        PROPERTY_MAP.put("server_tls_p12_passwd", "jco.server.tls_p12_passwd");
        //PROPERTY_MAP.put("server_unicode", "jco.server.unicode");// 1 : 0
        //PROPERTY_MAP.put("server_encoding", "jco.server.encoding");//

    }

    /**Gateway host on which the server should be registered*/
    protected String server_gwhost;
    /**Gateway service, i.e. the port on which a registration can be done*/
    protected String server_gwserv;
    protected String server_msserv;
    /**
     * @see com.sap.conn.jco.rt.CPICServer updateLBGateways()
     * server_type = B
     * */
    protected String server_mshost;
    /**The program ID with which the registration is done*/
    protected String server_progid;
    protected String server_system_id;
    protected String server_group;
    protected String server_group_key;
    /**The number of connections that should be registered at the gateway*/
    protected String server_connection_count;
    /**SAP router string to use for a system protected by a firewall*/
    protected String server_saprouter;
    /**The maximum time (in seconds) between two startup attempts in case of failures*/
    protected String server_max_startup_delay;
    /**Client destination from which to obtain the repository*/
    protected String server_repository_destination;
    /**repository map, if more than one repository should be used by JCoServer*/
    protected String server_repository_map;
    /**Enable/disable RFC trace (0 or 1)*/
    protected String server_trace;
    /**set the number of threads that can be used by the JCoServer instance*/
    protected String server_worker_thread_count;
    /**set the number of threads always kept running by JCoServer*/
    protected String server_worker_thread_min_count;
    /**Secure network connection (SNC) mode, 0 (off) or 1 (on)*/
    protected String server_snc_mode;
    /**SNC level of security, 1 to 9*/
    protected String server_snc_qop;
    /**SNC name of your server. Overrides the default SNC name. Typically something like p:CN=JCoServer, O=ACompany, C=EN*/
    protected String server_snc_myname;
    /**Path to library which provides SNC service.*/
    protected String server_snc_lib;
    protected String server_lb_update_interval_millis;
    protected String server_tls_p12_passwd;
    //protected String server_unicode;
    //protected String server_encoding;

    public Properties toProperties() {

        Properties props = new Properties();
        List<Field> allFields = Arrays.asList(SAPJCoServerFactory.class.getDeclaredFields());
        StringBuilder logBuilder = new StringBuilder("\nJCo server properties:");
        for (Field field : allFields) {
            String fieldName = field.getName();
            if (!"PROPERTY_MAP".equals(fieldName) && !"log".equals(fieldName)) {
                try {
                    String value = field.get(this).toString();
                    if (value != null) {
                        props.setProperty(PROPERTY_MAP.get(fieldName), value);
                    }
                    logBuilder.append("\n-" + fieldName + ": " + value);
                } catch (Exception e) {
                }
            }
        }
        log.info("{}", logBuilder);
        return props;
    }
}
