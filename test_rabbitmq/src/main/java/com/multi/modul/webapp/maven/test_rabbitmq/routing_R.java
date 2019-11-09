package com.multi.modul.webapp.maven.test_rabbitmq;
import com.rabbitmq.client.*;

public class routing_R {

	private static final String EXCHANGE_NAME = "direct_logs";
	private static final String []choice = {"info", "warning", "error"};
	
	public static void main(String[] argv) throws Exception {
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("192.168.1.2");
	    factory.setUsername("admin1");
		factory.setPassword("admin1");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    
	    channel.exchangeDeclare(EXCHANGE_NAME, "direct");
	    String queueName = channel.queueDeclare().getQueue();
	    
	    for (String severity : choice) {
	        channel.queueBind(queueName, EXCHANGE_NAME, severity);
	    }
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	        String message = new String(delivery.getBody(), "UTF-8");
	        System.out.println(" [x] Received '" +
	            delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
	    };
	    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
	}
}
