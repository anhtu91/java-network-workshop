package workshop.network.amqp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

//*******************************************************************************
/**
 * This application demonstrates the publish functionality. This program
 * publishes messages via the broker to different consumer (e.g manager).
 * 
 * The consumer exchanges are defined in the EXCHANGE. Also this program needs a
 * property file to create a connection to the broker.
 * 
 * <pre>
 * mode: queue
 * See {@link <a href="https://www.rabbitmq.com/tutorials/tutorial-one-java.html"}
 * See {@link <a href="https://www.rabbitmq.com/tutorials/tutorial-two-java.html"}
 * 
 * mode: ps
 * See {@link <a href="https://www.rabbitmq.com/tutorials/tutorial-three-java.html"}
 * 
 * mode: routing
 * See {@link <a href="https://www.rabbitmq.com/tutorials/tutorial-four-java.html"}
 * 
 * mode: topic
 * See {@link <a href="https://www.rabbitmq.com/tutorials/tutorial-five-java.html"}
 *
 * TODO Implementing a header exchange
 * </pre>
 * 
 * @author IKT MK
 * @version 2017-01-10
 */
// *******************************************************************************
public class MQBrokerPub_T {
	/* log4j logger reference */
	private static final Logger logger = LogManager
			.getLogger(MQBrokerPub_T.class);

	/* The path of the connection properties file */
	private static String PROP_FILE;
	private static Properties propFile;

	/* Broker connection factory */
	private static ConnectionFactory connectionFactory = null;

	/* Broker connection */
	private static Connection connection = null;

	/* Broker channels */
	private static Channel channel = null;

	/* The name of the queue */
	private static String QUEUE = "test/message";

	/* The name of the exchange */
	private static String EXCHANGE = "test/message/testq";

	/* The routing key for routing selectively */
	private static String ROUTING_KEY_TYPE_ROUTING = "error";

	/* The routing key for routing on a pattern */
	private static String ROUTING_KEY_TYPE_TOPIC = "log.inf.*";

	/*
	 * Received message are routed to all queues that are bound to the exchange
	 * (ps mode).
	 */
	private static String EXCHANGE_TYPE_FANOUT = "fanout";

	/*
	 * A message goes to the queues whose binding key exactly matches the
	 * routing key of the message (routing mode).
	 */
	private static String EXCHANGE_TYPE_DIRECT = "direct";

	/*
	 * Messages are routed to one or many queues based on a matching between a
	 * message routing key the routing pattern (topic mode).
	 */
	private static String EXCHANGE_TYPE_TOPIC = "topic";

	/**
	 * Starts the connection to the broker
	 * 
	 * @param mode
	 *            The mode - supports simple "queue", publish/subscribe "ps",
	 *            "routing", "topic"
	 */
	private static void startConnection(String mode) throws Exception {
		try {
			/* Trace info */
			logger.info(String.format("Starting the connection ..."));

			/*
			 * Creating a new connection factory to connect a RabbitMQ broker
			 */
			connectionFactory = new ConnectionFactory();

			/* Setting the connection parameters */
			connectionFactory.setHost(propFile.getProperty("host"));
			connectionFactory
					.setPort(Integer.valueOf(propFile.getProperty("port")));
			connectionFactory
					.setVirtualHost(propFile.getProperty("virtualHost"));
			connectionFactory.setUsername(propFile.getProperty("userName"));
			connectionFactory.setPassword(propFile.getProperty("password"));

			/* Trace info */
			logger.info(String.format("Connecting to the broker '%s:%s'...",
					propFile.getProperty("host"),
					propFile.getProperty("port")));

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
				 * <pre> queue - the name of the queue durable - true if we are
				 * declaring a durable queue (the queue will survive a server
				 * restart) exclusive - true if we are declaring an exclusive
				 * queue (restricted to this connection) autoDelete - true if we
				 * are declaring an autodelete queue (server will delete it when
				 * no longer in use) arguments - other properties (construction
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
				 * Configures the exchange/routing => 1-to-M (pub/sub pattern
				 * with routing key)
				 */
				channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE_DIRECT);
				break;
			case "topic":
				/*
				 * Configures the exchange/routing => 1-to-M (topic pattern with
				 * routing key)
				 */
				channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE_TOPIC);
				break;
			default:
				throw new IllegalArgumentException(
						String.format("BAD_PARAM: Unsupported switch type"));
			}

			/* Trace info */
			logger.info(
					String.format("The connection was started successful ..."));
		} catch (Throwable T) {
			/* Trace error */
			logger.catching(T);

			/* Forwords the exception */
			throw T;
		}
	}// End of startConnection

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

	/**
	 * Definition of the hook thread to catch the Ctrl-C signal from the user
	 * and shutdown the application accurately.
	 * 
	 * @param -
	 * @return -
	 * @exception -
	 */
	private static void startShutDownHook() {
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
						logger.info(
								"! Hook thread - shutdown the application !");
					}
				}));
	}// End of startShutDownHook

	/**
	 * The main function
	 * 
	 * @param args[0]
	 *            properties file
	 */
	public static void main(String[] args) {
		try {
			/* First starts the hook thread */
			startShutDownHook();

			/*
			 * Checks arguments
			 */
			switch (args.length) {
				case 2:
					/*
					 * Initializes the property file from argument
					 */
					PROP_FILE = args[0];
					File file = new File(PROP_FILE);
	
					/* Checks if the property file exists */
					if (file.exists() == false)
						throw new IllegalArgumentException(String
								.format("BAD_PARAM: PROP_FILE does not exists"));
	
					/* Creates a new property file object */
					propFile = new Properties();
	
					/* Reads the property file parameters */
					propFile.load(new FileInputStream(PROP_FILE));
					break;
				default:
					/* End of the program */
					throw new IllegalArgumentException(String.format(
							"Args.length '%d', "
									+ "required 2 parameters config file, mode !!!",
							args.length));
			}

			/* Trace info */
			logger.info(String.format("Starting the application '%s' ...",
					MQBrokerPub_T.class.getName()));

			/* Initializes the connection */
			startConnection(args[1]);

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
				switch (args[1]) {
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
			/* Trace error */
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
	}// End of main
}// End of MQBrokerSub_T