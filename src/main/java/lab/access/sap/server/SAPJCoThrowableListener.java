package lab.access.sap.server;

import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
@Slf4j
public class SAPJCoThrowableListener implements JCoServerErrorListener, JCoServerExceptionListener {

	public void serverExceptionOccurred(JCoServer arg0, String arg1, JCoServerContextInfo arg2, Exception arg3) {
		log.error((">>> Error occured on " + arg0.getProgramID().replaceAll("\r|\n","")	+ " connection " + arg2.toString().replaceAll("\r|\n","")));
		log.error(arg3.toString().replaceAll("\r|\n",""));
	}

	public void serverErrorOccurred(JCoServer arg0, String arg1, JCoServerContextInfo arg2, Error arg3) {
		log.error(">>> Error occured on " + arg0.getProgramID().replaceAll("\r|\n","") + " connection " + arg1.replaceAll("\r|\n",""));
		log.error(arg3.toString().replaceAll("\r|\n",""));
	}
}
