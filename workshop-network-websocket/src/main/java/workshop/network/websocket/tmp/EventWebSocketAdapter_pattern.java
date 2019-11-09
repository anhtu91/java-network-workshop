package workshop.network.websocket.tmp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import workshop.network.amqp.MQBrokerSub_T;
import workshop.network.amqp.MQBrokerSub_T_pattern;


/**
 * This is a simple websocket resource implementation.
 * 
 * @author MK
 * @version 2018-07-02
 */
public class EventWebSocketAdapter_pattern extends WebSocketAdapter {
	
	/** log4j logger reference for class Start_T */
	private static final Logger logger = LogManager.getFormatterLogger(EventWebSocketAdapter_pattern.class);

	/** The data point map sorted by id */
	public static Map<Integer, Session> sessions;

	/** The local session instance */
	private Session session;

	/**
	 * Creates a thread safe map.
	 * 
	 * You should use ConcurrentHashMap when you need very high concurrency in
	 * your project. It is thread safe without synchronizing the whole map.
	 * Reads can happen very fast while write is done with a lock. There is no
	 * locking at the object level. The locking is at a much finer granularity
	 * at a hashmap bucket level. ConcurrentHashMap doesn't throw a
	 * ConcurrentModificationException if one thread tries to modify it while
	 * another is iterating over it. ConcurrentHashMap uses multitude of locks.
	 */
	static {
		sessions = new ConcurrentHashMap<Integer, Session>();
	}
	
	
	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		logger.info("Socket Connected with session: '%s'", session);

		/** Saves session in the map */
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {

		logger.info("Socket Closed with session : '%s', reason: '%s'", this.session, reason);

		/** Removes the session in the map */

		/** Sets the local session to null */

		/** Logs the reason to the console */
		super.onWebSocketClose(statusCode, reason);
	}

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
	    /** Logs only the received message and the client info */
		//logger.info("Received message: '%s' from client '%s'", message, session.getRemoteAddress());
		logger.info("Received message: '%s' from client unknown", message);
	}
	
}