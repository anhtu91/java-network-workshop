package workshop.network.websocket.tmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import workshop.network.amqp.MQBrokerSub_T;


/**
 * Well, it's just a servlet declaration.
 * 
 * @author MK
 * @version 2018-07-02
 */
public class EventWebSocketServletBackup extends WebSocketServlet {

	/** A unique serial id */
	private static final long serialVersionUID = -8392610172228284350L;

	private static final Logger logger = LogManager.getFormatterLogger(EventWebSocketServletBackup.class);

	/* Broker connection */
	private static Connection connection = null;

	/* Broker channels */
	private static Channel channel = null;

	/* Broker connection factory */
	private static ConnectionFactory connectionFactory = null;

	/* The path of the connection properties file */
	/*
	 * private static String PROP_FILE; private static Properties propFile;
	 */

	/* The name of the queue */
	private static String QUEUE = "test/message";

	/*
	 * Received message are routed to all queues that are bound to the exchange (ps
	 * mode).
	 */
	private static String EXCHANGE_TYPE_FANOUT = "fanout";

	/*
	 * A message goes to the queues whose binding key exactly matches the routing
	 * key of the message (routing mode).
	 */
	private static String EXCHANGE_TYPE_DIRECT = "direct";

	/*
	 * Messages are routed to one or many queues based on a matching between a
	 * message routing key the routing pattern (topic mode).
	 */
	private static String EXCHANGE_TYPE_TOPIC = "topic";

	/* The name of the exchange */
	private static String EXCHANGE = "test/message/testq";

	/* The routing key for routing selectively */
	private static String ROUTING_KEY_TYPE_ROUTING = "error";

	/* The routing key for routing on a pattern */
	private static String ROUTING_KEY_TYPE_TOPIC = "log.inf.*";

	private static final String host = "10.3.0.75";
	private static final int port = 5672;
	private static final String virtualHost = "";
	private static final String userName = "mkuller";
	private static final String password = "mkuller";
	
	/******************************************************************/
	
	private static final boolean automaticRecoveryEnabled = true;
	private static final int networkRecoveryInterval = 1000;

	/* The consumer to receive messages */
	private static Consumer consumer = null;

	/* The consumer to receive messages */
	private static ConsumerRouting consumerRouting = null;

	/******************************************************************/
	
	@Override
	public void configure(WebSocketServletFactory factory) {
		/** Registers the websocket resource */
		factory.register(EventWebSocketAdapter_pattern.class);
	}
	
	/***********************************************/
	/** JAVA, S. 290: Reading Strings */
	final private static BufferedReader brConsole = new BufferedReader(
			new InputStreamReader(System.in));

	/**
	 * DIALOG: Ausgabe von formatter/args ueber System.out warte beliebig lange
	 * auf System.in auf Input.
	 * 
	 * @param s
	 *            String formatiert.
	 * @return Input Benutzereingabe.
	 */
	public static String Dialog(String s) {
		logger.info(s);
		try {
			return brConsole.readLine();
		} catch (Throwable T) {
			return null;
		}
	} // End of DIALOG
	
	/********************************* Publisher *********************************/

	/**
	 * Starts the connection to the broker
	 * 
	 * @param mode
	 *            The mode - supports simple "queue", publish/subscribe "ps",
	 *            "routing", "topic"
	 */
	private static void startConnectionPublisher(String mode) throws Exception {
		try {
			/* Trace info */
			logger.info(String.format("Starting the connection ..."));

			/*
			 * Creating a new connection factory to connect a RabbitMQ broker
			 */
			connectionFactory = new ConnectionFactory();

			/* Setting the connection parameters */
			connectionFactory.setHost(host);
			connectionFactory.setPort(port);
			connectionFactory.setVirtualHost(virtualHost);
			connectionFactory.setUsername(userName);
			connectionFactory.setPassword(password);

			/* Trace info */
			logger.info(String.format("Connecting to the broker '%s:%s'...", host, port));

			/* Connects the broker */
			connection = connectionFactory.newConnection();

			/* Creates a channel for the connection */
			channel = connection.createChannel();

			/* Configures the channels depends on the mode */
			switch (mode) {
			case "queue":
				/*
				 * Configures the queue => 1-to-1 and 1-to-M (worker pattern)
				 * 
				 * <pre> queue - the name of the queue durable - true if we are declaring a
				 * durable queue (the queue will survive a server restart) exclusive - true if
				 * we are declaring an exclusive queue (restricted to this connection)
				 * autoDelete - true if we are declaring an autodelete queue (server will delete
				 * it when no longer in use) arguments - other properties (construction
				 * arguments) for the queue </pre>
				 */
				channel.queueDeclare(QUEUE, false, false, false, null);
				break;
			case "ps":
				/* Configures the exchange => 1-to-M (pub/sub pattern) */
				channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE_FANOUT);
				break;
			case "routing":
				/*
				 * Configures the exchange/routing => 1-to-M (pub/sub pattern with routing key)
				 */
				channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE_DIRECT);
				break;
			case "topic":
				/*
				 * Configures the exchange/routing => 1-to-M (topic pattern with routing key)
				 */
				channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE_TOPIC);
				break;
			default:
				throw new IllegalArgumentException(String.format("BAD_PARAM: Unsupported switch type"));
			}

			/* Trace info */
			logger.info(String.format("The connection was started successful ..."));
		} catch (Throwable T) {
			/* Trace error */
			logger.catching(T);

			/* Forwords the exception */
			throw T;
		}
	}// End of startConnection

	/**
	 * Definition of the hook thread to catch the Ctrl-C signal from the user and
	 * shutdown the application accurately.
	 * 
	 * @param -
	 * @return -
	 * @exception -
	 */
	private static void startShutDownHookPublisher() {
		Runtime.getRuntime().addShutdownHook(
				/* Creates a new Hook Thread */
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							/* Deletes the connection */
							if (connection != null)
								connection.close();

							/* Deletes the channel */
							if (channel != null)
								channel.close();
						} catch (Throwable T) {
							/* Trace error */
							logger.catching(T);
						}
						/* Trace info */
						logger.info("! Hook thread - shutdown the application !");
					}
				}));
	}// End of startShutDownHook

	/************************* Start Publisher *************************/
	public static void mainPublisher(String mode, String message) {

		try {
			startShutDownHookPublisher();

			/* Trace info */
			logger.info(String.format("Starting the Publisher '%s' ...", EventWebSocketServletBackup.class.getName()));

			/* Initializes the connection */
			startConnectionPublisher(mode);

			/* Send loop */
			for (;;) {
				/*
				 * Waiting for user input => end of program with 'q'
				 */
				String ret = Dialog(String.format(
						"Exit program - press terminate button in Eclipse IDE or press key 'q'\n"
								+ "Define a string based payload",
						Calendar.getInstance()));

				/*
				 * Beende Programm mit "q" fuer Quit, da CTRL-C nicht in Eclipse
				 * Console unterstuetzt wird.
				 */
				if (ret.compareTo("q") == 0) {
					logger.info(String
							.format("The user shutdowns the application."));
					/* End of the program */
					break;
				}

				/* Checks the configured mode - communication pattern */
				switch (mode) {
					case "queue":
						/*
						 * Publishes the message to the queue directly
						 */
						channel.basicPublish("", QUEUE, null, ret.getBytes());
	
						/* Trace info */
						logger.info(String.format(
								"Publishes the message to queue '%s', message '%s':'%d' bytes successful.",
								QUEUE, ret, ret.length()));
						break;
					case "ps":
						/*
						 * Publishes the message to the exchange not to a queue
						 * directly
						 */
						channel.basicPublish(EXCHANGE, "", null, ret.getBytes());
	
						/* Trace info */
						logger.info(String.format(
								"Publishes the message to exchange '%s', message '%s':'%d' bytes successful.",
								EXCHANGE, ret, ret.length()));
						break;
					case "routing":
						/*
						 * Publishes the message to the exchange with severity
						 * directly - the severity in this case is the routing key
						 * 
						 * for example using routing key error or info
						 */
						channel.basicPublish(EXCHANGE, ROUTING_KEY_TYPE_ROUTING,
								null, ret.getBytes());
	
						/* Trace info */
						logger.info(String.format(
								"Publishes the message to exchange '%s', message '%s':'%d' bytes successful.",
								EXCHANGE, ret, ret.length()));
						break;
					case "topic":
						/*
						 * Publishes the message to the exchange with severity
						 * directly - the severity in this case is the topic key
						 */
						channel.basicPublish(EXCHANGE, ROUTING_KEY_TYPE_TOPIC, null,
								ret.getBytes());
	
						/* Trace info */
						logger.info(String.format(
								"Publishes the message to exchange '%s', message '%s':'%d' bytes successful.",
								EXCHANGE, ret, ret.length()));
						break;
					default:
						throw new IllegalArgumentException(String
								.format("BAD_PARAM: Unsupported switch type"));
				}
			}
		} catch (Throwable T) {
			logger.catching(T);
		}finally {
			try {
				/* Deletes the channel */
				if (channel != null)
					channel.close();

				/* Closes the connection */
				if (connection != null) {
					if (connection.isOpen() == true)
						connection.close();
				}
			} catch (Throwable T) {
				/* Trace error */
				logger.catching(T);
			}
			/* Trace info */
			logger.info(String.format("Shutdown application ..."));
		}
	}

	
	/*************************************************** Subscriber ***********************************************************/
	
	/** The default consumer */
	private static class Consumer extends DefaultConsumer {
		Consumer(Channel channel) {
			super(channel);
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			String message = new String(body, "UTF-8");
			logger.info(String.format("[x-Consumer] received '%s'", message));
		}
	}
	
	/** The consumer to demonstrate the routing mechanism */
	private static class ConsumerRouting extends DefaultConsumer {
		ConsumerRouting(Channel channel) {
			super(channel);
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			String message = new String(body, "UTF-8");
			logger.info(String.format("[x-Consumer] received '%s'", message));
		}
	}
	
	private static void startConnectionSubscriber(String mode) throws Exception {
		try {

			/* Trace info */
			logger.info(String.format("Starting the connection ... with '%s'", automaticRecoveryEnabled));

			/*
			 * Creating a new connection factory to connect a RabbitMQ broker
			 */
			connectionFactory = new ConnectionFactory();

			/* Setting the connection parameters */
			connectionFactory.setHost(host);
			connectionFactory.setPort(port);
			connectionFactory.setVirtualHost(virtualHost);
			connectionFactory.setUsername(userName);
			connectionFactory.setPassword(password);
			connectionFactory.setAutomaticRecoveryEnabled(automaticRecoveryEnabled);
			connectionFactory.setNetworkRecoveryInterval(networkRecoveryInterval);
			connectionFactory.setRequestedHeartbeat(10);

			/* Trace info */
			logger.info(String.format("Connecting to the broker '%s:%s'...", host, port));

			/* Connects the broker */
			connection = connectionFactory.newConnection();

			/* Creates a new channel */
			channel = connection.createChannel();

			/* Creating the channel/queue */
			switch (mode) {
				case "queue":
					/* Trace info */
					logger.info(String.format("Mode '%s', subscribing to the queue '%s'...", mode, QUEUE));
	
					/* Creates the mode=queue queue */
					channel.queueDeclare(QUEUE, false, false, false, null);
	
					/* Creates a consumer instance */
					consumer = new Consumer(channel);
	
					/* Binds the queue with the consumer */
					channel.basicConsume(QUEUE, true, consumer);
					break;
				case "ps":
					/* Trace info */
					logger.info(String.format("Mode '%s', subscribing to the exchange '%s' with exchange type '%s ...",
							mode, EXCHANGE, EXCHANGE_TYPE_FANOUT));
	
					/* Creates the mode=ps exchange */
					channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE_FANOUT);
	
					/* Creates a generic queue */
					String queueNameGeneric = channel.queueDeclare().getQueue();
	
					/* Trace info */
					logger.info(String.format("Mode '%s', binding without routing key ...", mode));
	
					/*
					 * Binds the generic queue to the exchange without routing key
					 */
					channel.queueBind(queueNameGeneric, EXCHANGE, "");
	
					/* Creates a consumer instance */
					consumer = new Consumer(channel);
	
					/* Binds the queue with the consumer */
					channel.basicConsume(queueNameGeneric, true, consumer);
					break;
				case "routing":
					/* Trace info */
					logger.info(String.format("Mode '%s', subscribing to the exchange '%s' with exchange type '%s ...",
							mode, EXCHANGE, EXCHANGE_TYPE_DIRECT));
	
					/* Creating the mode=routing exchange / queue */
					channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE_DIRECT);
	
					/***************************/
					/* Creates a generic queue */
					String queueNameGenericRouting_1 = channel.queueDeclare().getQueue();
	
					/* The routing keys */
					String BINDING_KEY_11 = "error";
					String BINDING_KEY_12 = "info";
	
					/* Trace info */
					logger.info(String.format("Mode '%s', binding queue '%s' with binding keys '%s', '%s' ...", mode,
							queueNameGenericRouting_1, BINDING_KEY_11, BINDING_KEY_12));
	
					/*
					 * Binds the generic queue to the exchange with routing keys error and info
					 */
					channel.queueBind(queueNameGenericRouting_1, EXCHANGE, BINDING_KEY_11);
					channel.queueBind(queueNameGenericRouting_1, EXCHANGE, BINDING_KEY_12);
	
					/* Creates a consumer instance */
					consumer = new Consumer(channel);
	
					/* Binds the queue with the consumer */
					channel.basicConsume(queueNameGenericRouting_1, true, consumer);
	
					/*********************************/
					/* Creates another generic queue */
					String queueNameGenericRouting_2 = channel.queueDeclare().getQueue();
	
					/* The routing keys */
					String BINDING_KEY_2 = "error";
	
					/* Trace info */
					logger.info(String.format("Mode '%s', binding queue '%s' with binding key '%s' ...", mode,
							queueNameGenericRouting_2, BINDING_KEY_2));
	
					/*
					 * Binds the generic queue to the exchange with routing key error
					 */
					channel.queueBind(queueNameGenericRouting_2, EXCHANGE, BINDING_KEY_2);
	
					/* Creates a consumer instance */
					consumerRouting = new ConsumerRouting(channel);
	
					/* Binds the queue with the consumer */
					channel.basicConsume(queueNameGenericRouting_2, true, consumerRouting);
					break;
				case "topic":
					/* Trace info */
					logger.info(String.format("Mode '%s', subscribing to the exchange '%s' with exchange type '%s ...",
							mode, EXCHANGE, EXCHANGE_TYPE_TOPIC));
	
					/* Creating the mode=routing exchange / queue */
					channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE_TOPIC);
	
					/* Creates a generic queue */
					String queueNameGenericTopic_1 = channel.queueDeclare().getQueue();
	
					/* The routing keys */
					String BINDING_KEY_3 = "*.info.*";
	
					/* Trace info */
					logger.info(String.format("Mode '%s', binding queue '%s' with binding key '%s' ...", mode,
							queueNameGenericTopic_1, BINDING_KEY_3));
	
					/*
					 * Binds the generic queue to the exchange with routing key *.info.*
					 */
					channel.queueBind(queueNameGenericTopic_1, EXCHANGE, BINDING_KEY_3);
	
					/* Creates a consumer instance */
					consumer = new Consumer(channel);
	
					/* Binds the queue with the consumer */
					channel.basicConsume(queueNameGenericTopic_1, true, consumer);
					break;
				default:
					throw new IllegalArgumentException(String.format("BAD_PARAM: Unsupported switch type"));
			}

			/* Trace info */
			logger.info(String.format("The connection was started successful ..."));
		} catch (Throwable T) {
			/* Trace error */
			logger.catching(T);

			/* Forwords the exception */
			throw T;
		}
	}// End of startConnection

	
	/**
	 * Definition of the hook thread to catch the Ctrl-C signal from the user
	 * and shutdown the application accurately.
	 * 
	 * @param -
	 * @return -
	 * @exception -
	 */
	private static void startShutDownHookSubscriber() {
		Runtime.getRuntime().addShutdownHook(
				/* Creates a new Hook Thread */
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							/* Deletes the channel */
							if (channel != null)
								channel.close();
							/* Deletes the connection */
							if (connection != null)
								connection.close();
						} catch (Throwable T) {
							/* Trace error */
							logger.catching(T);
						}
						/* Trace info */
						logger.info("! Hook thread - shutdown the application !");
					}
				}));
	}// End of startShutDownHook
	
	
	/** Start Subscriber **/
	public static void mainSubscriber(String mode) {

		try {
			startShutDownHookSubscriber();
			
			/* Trace info */
			logger.info(String.format("Starting the Subscriber ... with mode '%s'",
					mode));

			/* Initializes the connection */
			startConnectionSubscriber(mode);
			
			/*
			 * Waiting for user input => end of program with 'q'
			 */
			String ret = Dialog(
					String.format("%tT: Exit program - press terminate button in Eclipse IDE or press key 'q'",
							Calendar.getInstance()));

			/*
			 * Beende Programm mit "q" fuer Quit, da CTRL-C nicht in Eclipse
			 * Console unterstuetzt wird.
			 */
			if (ret.compareTo("q") == 0) {
				logger.info(String.format("The user shutdowns the application."));
				/* End of the program */
			}
			
		}catch(Throwable T) {
			logger.catching(T);
		} finally {
			try {
				/* Deletes the channel */
				if (channel != null)
					channel.close();

				/* Closes the connection */
				if (connection != null) {
					if (connection.isOpen() == true)
						connection.close();
				}
			} catch (Throwable T) {
				/* Trace error */
				logger.catching(T);
			}
			/* Trace info */
			logger.info(String.format("Shutdown application ..."));
		}
	}
}