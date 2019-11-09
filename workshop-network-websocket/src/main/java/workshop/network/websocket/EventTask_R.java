package workshop.network.websocket;

import java.util.Calendar;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;


/**
 * This is a simple notification thread implementation, which sends a date/time
 * notification to all connected clients, at notification timeout intervals.
 * 
 * @author MK
 * @version 2018-07-03
 */
public class EventTask_R implements Runnable {
	
	/** log4j logger reference for class Start_T */
	private static final Logger logger = LogManager.getFormatterLogger(EventTask_R.class);

	/** The notification timeout in msec */
	private static final int NOTIFICATION_TIMEOUT = 5000;

	/**
	 * The constructor
	 */
	public EventTask_R() {
		// do nothing;
	}
	
	/** The run function - started as thread */
	public void run() {
		try {
			for (;;) {
				/**
				 * Sends notifications at timeout intervals every timeout
				 */
				Thread.sleep(NOTIFICATION_TIMEOUT);

				/** Do nothing if the session map is not initialized */
				if (EventWebSocketAdapter_R.sessions == null) {
					logger.info("The sessions map is 'null ... do nothing.\n");
					continue;
				}
				logger.info("The sessions map length is '%d'\n", EventWebSocketAdapter_R.sessions.size());

				/** Do nothing if there is no connection */
				if (EventWebSocketAdapter_R.sessions.size() == 0) {
					logger.info("The sessions map length is '%d' ... do nothing.\n",
							EventWebSocketAdapter_R.sessions.size());
					continue;
				}

				/** Sends notifications to all connected clients */
				for (Map.Entry<Integer, Session> entry : EventWebSocketAdapter_R.sessions.entrySet()) {
					entry.getValue().getRemote().sendString(String.format("'%tT' from server '%s'",
							Calendar.getInstance().getTime(), entry.getValue().getLocalAddress()));
				}
			}
		} catch (Throwable T) {
			/** Trace error */
			logger.catching(T);
		} finally {
			/** Trace info */
			logger.info(String.format("Thread '%s' shutdown ... \n", Thread.currentThread().getName()));
		}
	}
}// End of class Task
