package workshop.network.amqp;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

//*******************************************************************************
/**
 * The RPC application demonstrates the broker request/response (RPC - remote
 * procedure call) communication pattern.
 * 
 * This is the client part (sending a request, waiting for a response).
 * 
 * @author IKT MK
 * @version 2017-01-11
 */
// *******************************************************************************
public class RpcClient_T {

	/* log4j logger reference */
	private static final Logger logger = LogManager
			.getLogger(RpcClient_T.class);

	/* Broker ip */
	//private static final String HOST = "10.3.1.75";
	private static final String HOST = "10.3.10.23";
	// private static final String HOST = "10.3.0.148";
	// private static final String HOST = "192.168.29.68";

	/* Broker username */
	private static final String USER_NAME = "mkuller";

	/* Broker password */
	private static final String PASSWORD = "mkuller";

	/* The receive queue (server side) */
	private static final String RPC_QUEUE_NAME = "test.rpc.3";
	// private static final String RPC_QUEUE_NAME = "manager.1.parameter";

	/* The message ttl in the broker queue */
	private static final String messageTtl = "1000";

	/* The receiveTimeout */
	private static final String receiveTimeout = "5000";

	/* The requestDelay */
	private static final String requestDelay = "2000";

	/* The requestLoops */
	private static final String requestLoops = "1";

	/** The main function ... */
	public static void main(String[] args) {
		/* The broker connection object */
		Connection connection = null;

		/* The broker channel object */
		Channel channel = null;

		try {
			/* Trace info */
			logger.info(String.format("Starting the application '%s' ...",
					RpcClient_T.class.getName()));

			/* Broker connection factory */
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(HOST);
			factory.setUsername(USER_NAME);
			factory.setPassword(PASSWORD);

			/* Trace info */
			logger.info(
					String.format("Connecting to the broker '%s' ...", HOST));

			/* Creating a new connection to the broker */
			connection = factory.newConnection();

			/* Creates a new channel (virtual) to the broker */
			channel = connection.createChannel();

			/* Trace info */
			logger.info(String.format("Creating a rpc queue ..."));

			/*
			 * Gets the reply queue name - we have 1:1 connection between the
			 * channel and the queue
			 */
			String replyQueueName = channel.queueDeclare().getQueue();

			/* Creates a consumer to receive a response on this queue */
			QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

			/* Registers the consumer by the reply queue */
			channel.basicConsume(replyQueueName, true, queueingConsumer);

			for (int i = 0; i < Integer.valueOf(requestLoops); i++) {
				/* Trace */
				logger.info(String.format("Building new rpc call ... !"));

				/*
				 * Creates a UUID based correlationId only for this
				 * request/response communication
				 */
				String correlationId = java.util.UUID.randomUUID().toString();

				/* Builds the header properties */
				BasicProperties props = new BasicProperties.Builder()
						.expiration(messageTtl).correlationId(correlationId)
						.replyTo(replyQueueName).build();

				/* Creates a dummy request V1 */
				// File fileIn = null;
				// byte[] fileByteArray = null;
				// String payload = "";
				//
				// try (FileInputStream inputStream = new FileInputStream(
				// args[0]);) {
				// /* Checks if data are availaible */
				// int fileNumber = inputStream.available();
				//
				// /* No data available */
				// if (fileNumber == 0)
				// throw new IllegalArgumentException(
				// String.format("File '%s' is empty !", args[0]));
				//
				// /* Creates the memory to read file data input */
				// fileByteArray = new byte[fileNumber];
				// logger.info(String.format("Available bytes in file '%d'",
				// fileNumber));
				//
				// /* Reads data from file */
				// int byteArrayNumber = inputStream.read(fileByteArray);
				// payload = new String(fileByteArray);
				//
				// /* Trace info */
				// logger.info("Available bytes in ByteArray '%d'",
				// byteArrayNumber);
				// } catch (Throwable t) {
				// throw t;
				// }

				/* Create a dummy request V2 */
				String payload = new String("hallo");

				/* Trace */
				logger.info(String.format(
						"Publishing a http request to queue '%s' with content  '%s'\n\n%s ... and corrID '%s'",
						RPC_QUEUE_NAME, payload, payload, correlationId));

				/* Publishing the request payload */
				channel.basicPublish("", RPC_QUEUE_NAME, props,
						payload.getBytes("UTF-8"));

				/* Waiting for response */
				QueueingConsumer.Delivery delivery = queueingConsumer
						.nextDelivery(Integer.valueOf(receiveTimeout));

				/* Is this a valid response */
				if (delivery == null)
					/* Trace */
					logger.info(String
							.format("No response received after timeout !"));
				else
					/* Trace error */
					logger.info(String.format(
							"Receiving a HTTP request from '%s' with body \n\n%s",
							delivery.getEnvelope().toString(),
							new String(delivery.getBody(), "UTF-8")));

				/* Delay */
				Thread.sleep(Integer.valueOf(requestDelay));

				/* Trace */
				logger.info(String.format("Next rpc call ... !"));
			}
		} catch (Throwable T) {
			/* Trace error */
			logger.catching(T);
		} finally {
			/* Closes the connection */
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ignore) {
				}
				/* Trace */
				logger.info(String.format("App schutdown ... !"));
			}
		}
	}
}