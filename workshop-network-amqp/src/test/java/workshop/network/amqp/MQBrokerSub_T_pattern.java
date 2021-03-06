package workshop.network.amqp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

//*******************************************************************************
/**
 * This application demonstrates the subscriber functionality. This program
 * receives messages via the broker from different producer.
 * 
 * The consumer exchanges are defined in the EXCHANGE. Also this program needs a
 * property file to create a connection to the broker.
 * 
 * This application supports an demonstrates five different MQ Broker
 * communication pattern.
 * 
 * <pre>
 * mode: queue
 * See {@link <a href="http://https://www.rabbitmq.com/tutorials/tutorial-one-java.html"}
 * See {@link <a href="http://https://www.rabbitmq.com/tutorials/tutorial-two-java.html"}
 * 
 * mode: ps
 * See {@link <a href="http://https://www.rabbitmq.com/tutorials/tutorial-three-java.html"}
 * 
 * mode: routing
 * See {@link <a href="http://https://www.rabbitmq.com/tutorials/tutorial-four-java.html"}
 * 
 * mode: topic
 * See {@link <a href="http://https://www.rabbitmq.com/tutorials/tutorial-five-java.html"}
 * </pre>
 * 
 * @author IKT MK
 * @version 2017-01-10
 */
// *******************************************************************************
public class MQBrokerSub_T_pattern {
	/* log4j logger reference */
	private static final Logger logger = LogManager
			.getLogger(MQBrokerSub_T_pattern.class);

	/* The path of the connection properties file */
	private static String PROP_FILE;
	private static Properties propFile;

	/* Broker connection factory */
	private static ConnectionFactory connectionFactory = null;

	/* Broker connection */
	private static Connection connection = null;

	/* Broker channel */
	private static Channel channel = null;

	/* The consumer to receive messages */
	private static Consumer consumer = null;

	/* The consumer to receive messages */
	private static ConsumerRouting consumerRouting = null;

	/* The name of the queue */
	private static String QUEUE = "test/message";

	/* The name of the exchange */
	private static String EXCHANGE = "test/message";

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
	 * @param mode The mode - supports simple "queue", publish/subscribe "ps",
	 *            "routing", "topic"
	 */
	public static void startConnection(String mode) throws Exception {
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

			/* Creates a new channel */
			channel = connection.createChannel();

			// TODO
			
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

	/** The default consumer */
	private static class Consumer extends DefaultConsumer {
		Consumer(Channel channel) {
			super(channel);
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body)
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
		public void handleDelivery(String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			String message = new String(body, "UTF-8");
			logger.info(String.format("[x-Consumer] received '%s'", message));
		}
	}

	/** JAVA, S. 290: Reading Strings */
	final private static BufferedReader brConsole = new BufferedReader(
			new InputStreamReader(System.in));

	/**
	 * DIALOG: Ausgabe von formatter/args ueber System.out warte beliebig lange
	 * auf System.in auf Input.
	 * 
	 * @param s String formatiert.
	 * @return Input Benutzereingabe.
	 */
	public static String Dialog(String s) {
		logger.info(s);
		try {
			return brConsole.readLine();
		} catch (Throwable T) {
			return null;
		}
	} // Ende von DIALOG

	/**
	 * The main function
	 * 
	 * @param args[0] properties file
	 * @param args[1] mode queue, ps
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
				logger.info(String.format(
						"Args.length '%d', "
								+ "required 2 parameters config file, mode !!!",
						args.length));
				/*
				 * End of the program with System.exit()
				 */
				System.exit(1);
			}

			/* Trace info */
			logger.info(String.format(
					"Starting the application '%s' ... with mode '%s'",
					MQBrokerSub_T_pattern.class.getName(), args[1]));

			// TODO
			
			/*
			 * Waiting for user input => end of program with 'q'
			 */
			String ret = Dialog(String.format(
					"%tT: Exit program - press terminate button in Eclipse IDE or press key 'q'",
					Calendar.getInstance()));

			/*
			 * Beende Programm mit "q" fuer Quit, da CTRL-C nicht in Eclipse
			 * Console unterstuetzt wird.
			 */
			if (ret.compareTo("q") == 0) {
				logger.info(
						String.format("The user shutdowns the application."));
				/* End of the program */
			}
		} catch (Throwable T) {
			/* Trace error */
			logger.catching(T);
		} finally {
			try {
				/* Deletes the resources */
				if (connection != null)
					connection.close();
				if (channel != null)
					channel.close();
			} catch (Throwable T) {
				/* Trace error */
				logger.catching(T);
			}
			/* Trace info */
			logger.info(String.format("Shutdown application ..."));
		}
	}// End of main
}// End of PsServer