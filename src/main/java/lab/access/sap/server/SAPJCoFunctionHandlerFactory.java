package lab.access.sap.server;

import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
@Slf4j
public class SAPJCoFunctionHandlerFactory extends DefaultServerHandlerFactory {


    @Override
    public void sessionClosed(JCoServerContext jCoServerContext, String msg, boolean flag) {
        log.info("Session '{}'", jCoServerContext.getSessionID() + " was closed" + (flag?msg:" by SAP system"));
    }
}
