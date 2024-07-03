package lab.access.sap.server;

import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yuhyun O
 * @since 2024-07-03
 * */
@Slf4j
public class SAPJCoServerStateChangedListener implements JCoServerStateChangedListener {

	public void serverStateChangeOccurred(JCoServer server, JCoServerState oldState, JCoServerState newState) {
		log.info("Server state changed from " + oldState.toString()
				+ " to " + newState.toString() + " on server with program id "
				+ server.getProgramID());
	}

}
