package workshop.network.websocket.tmp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class WbSocketPubClient {

	
	// WebSocket 
	// ITF ( in the future) TODO
	// MessageBroker
	private static ConnectionFactory connectionFactory = null;

	/* Broker connection */
	private static Connection connection = null;

	/* Broker channels */
	private static Channel channel = null;

	/* The name of the queue */
	private static String QUEUE = "test/message";

	// Main
}
