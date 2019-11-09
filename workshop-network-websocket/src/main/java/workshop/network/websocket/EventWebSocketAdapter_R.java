package workshop.network.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * This is a simple websocket resource implementation.
 * 
 * @author MK
 * @version 2018-07-02
 */
public class EventWebSocketAdapter_R extends WebSocketAdapter {
	/** log4j logger reference for class Start_T */
	private static final Logger logger = LogManager.getFormatterLogger(EventWebSocketAdapter_R.class);

	/** The data point map sorted by id */
	public static Map<Integer, Session> sessions;

	/** The local session instance */
	private Session session;

	// Connection to Broker - Object

	/* Broker connection factory */
	private static ConnectionFactory connectionFactory = null;

	/* Broker connection */
	private static Connection connection = null;

	/* Broker channel */
	private static Channel channel = null;

	private static Consumer consumer = null;

	/* The name of the queue */
	private static String QUEUE = "test/message";

	// Method to establish connection
	public static void establishConnection() {

		connectionFactory = new ConnectionFactory();

		connectionFactory.setHost("10.3.0.75");
		connectionFactory.setPort(5672);

		try {

			connection = connectionFactory.newConnection();
			channel = connection.createChannel();

			channel.queueDeclare(QUEUE, false, false, false, null);

			/* Creates a consumer instance */
			consumer = new Consumer(channel);

			/* Binds the queue with the consumer */
			channel.basicConsume(QUEUE, true, consumer);

		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			logger.catching(e);
		}

	}

	// create Consumer with handle delivery ---> send message session remote ...
	/** The default consumer **/
	private static class Consumer extends DefaultConsumer {
		Consumer(Channel channel) {
			super(channel);
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			String message = new String(body, "UTF-8");
			logger.info(String.format("[x-Consumer] received '%s'", message));

			// TODO

			for (Map.Entry<Integer, Session> entry : sessions.entrySet()) {

				Session session = entry.getValue();
				// session is static variable
				session.getRemote().sendString(message);

			}
		}
	}

	/**
	 * Creates a thread safe map.
	 * 
	 * You should use ConcurrentHashMap when you need very high concurrency in your
	 * project. It is thread safe without synchronizing the whole map. Reads can
	 * happen very fast while write is done with a lock. There is no locking at the
	 * object level. The locking is at a much finer granularity at a hashmap bucket
	 * level. ConcurrentHashMap doesn't throw a ConcurrentModificationException if
	 * one thread tries to modify it while another is iterating over it.
	 * ConcurrentHashMap uses multitude of locks.
	 */
	static {
		sessions = new ConcurrentHashMap<Integer, Session>();
	}

	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		logger.info("Socket Connected with session: '%s'", session);

		/**
		 * Saves session in the map, the unique id is the hash code of the session
		 * object
		 */
		sessions.put(session.hashCode(), this.session = session);

		establishConnection();

	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		try {
			logger.info("Socket Closed with session : '%s', reason: '%s'", this.session, reason);

			/** Removes the session in the map */
			sessions.remove(this.session.hashCode());

			/** Sets the local session to null */
			this.session = null;

			logger.info("Number of actual sessions '%d'", sessions.size());

			/** Logs the reason to the console */
			super.onWebSocketClose(statusCode, reason);
		} catch (Throwable T) {
			/** Trace error */
			logger.catching(T);
		}
	}

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		
		for (Map.Entry<Integer, Session> entry : sessions.entrySet()) {

			Session session = entry.getValue();
			if (this.session == session)
				continue;
			// session is static variable
			try {
				session.getRemote().sendString(message);
				/** Logs only the received message and the client info */
				logger.info("Received message: '%s' from client '%s'", message, session.getRemoteAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}