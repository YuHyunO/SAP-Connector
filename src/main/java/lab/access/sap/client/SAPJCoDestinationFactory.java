package lab.access.sap.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
@Data
@Slf4j
public class SAPJCoDestinationFactory {
    protected Map<String, String> PROPERTY_MAP = new HashMap<>();

    public SAPJCoDestinationFactory() {
        PROPERTY_MAP.put("client_lang", "jco.client.lang");
        PROPERTY_MAP.put("client_client", "jco.client.client");
        PROPERTY_MAP.put("client_passwd", "jco.client.passwd");
        PROPERTY_MAP.put("client_user", "jco.client.user");
        PROPERTY_MAP.put("client_sysnr", "jco.client.sysnr");
        PROPERTY_MAP.put("client_ashost", "jco.client.ashost");
        PROPERTY_MAP.put("destination_peak_limit", "jco.destination.peak_limit");
        PROPERTY_MAP.put("destination_pool_capacity", "jco.destination.pool_capacity");
        PROPERTY_MAP.put("client_mshost", "jco.client.mshost");
        PROPERTY_MAP.put("client_msserv", "jco.client.msserv");
        PROPERTY_MAP.put("client_r3name", "jco.client.r3name");
        PROPERTY_MAP.put("client_group", "jco.client.group");
        PROPERTY_MAP.put("poolmgr_connectiontimeout", "jco.poolmgr.connectiontimeout");
        PROPERTY_MAP.put("pool_maxconn", "jco.pool.maxconn");
        PROPERTY_MAP.put("pool_maxpoolsize", "jco.pool.maxpoolsize");
        PROPERTY_MAP.put("expiration_time", "jco.destination.expiration_time");
        PROPERTY_MAP.put("expiration_check_period", "jco.destination.expiration_check_period");
        PROPERTY_MAP.put("max_get_client_time", "jco.destination.max_get_client_time");
        PROPERTY_MAP.put("pool_check_connection", "jco.destination.pool_check_connection");
        PROPERTY_MAP.put("client_codepage", "jco.client.codepage");
        //PROPERTY_MAP.put("client_unicode", "jco.client.unicode");// 1 : 0
        //PROPERTY_MAP.put("client_encoding", "jco.client.encoding");

    }

    /**Creates a JCo pool with the specified value for the SAP logon language.*/
    protected String client_lang;
    /**The maximum number of active connections you can create for a destination simultaneously*/
    protected String destination_peak_limit;
    /**SAP client. SAP client ('client') needs to be a three digit number string*/
    protected String client_client;
    /**Logon password*/
    protected String client_passwd;
    /**Logon user*/
    protected String client_user;
    /**SAP system number. SAP server uses the ports 3300 ~ 3399. ex) When client_sysnr is 00, the port number of 'client_ashost' is 3300.*/
    protected String client_sysnr;
    /**Host name or IP address of the SAP Application Server (or SAP Netweaver Gateway)*/
    protected String client_ashost;
    /**The maximum number of idle connections kept open by the destination*/
    protected String destination_pool_capacity;
    /**SAP message server IP address*/
    protected String client_mshost;
    /**Port number or logical name of the message server*/
    protected String client_msserv;
    /**SAP ERP name*/
    protected String client_r3name;
    /**Group of SAP application servers*/
    protected String client_group;
    /**The connection is considered to have timed out when there has been no activity on it for the specified time interval*/
    protected String poolmgr_connectiontimeout;
    /**The absolute maximum number of connections that can be simultaneously opened for a given pool*/
    protected String pool_maxconn;
    /**The maximum number of connections that can be kept open and managed in the pool*/
    protected String pool_maxpoolsize;
    /**Time, in milliseconds, after which the connections held by the internal pool may be closed*/
    protected String expiration_time;
    /**Interval, in milliseconds, with which the timeout checker thread checks the connections in the pool for expiration*/
    protected String expiration_check_period;
    /**Maximum time, in milliseconds, to wait for a connection, if the maximum allowed number of connections is allocated by the application*/
    protected String max_get_client_time;
    /**When setting this value to 1, a pooled connection will be checked for corruption before being used for the next function module execution. Thus, it is possible to recognize corrupted connections and avoid exceptions being passed to applications when connectivity is basically working (default value is 0).*/
    protected String pool_check_connection;
    protected String client_codepage;
    //protected String client_unicode;
    //protected String client_encoding;

    public Properties toProperties() {

        Properties props = new Properties();
        List<Field> allFields = Arrays.asList(SAPJCoDestinationFactory.class.getDeclaredFields());
        StringBuilder logBuilder = new StringBuilder("\nJCo destination properties:");
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
