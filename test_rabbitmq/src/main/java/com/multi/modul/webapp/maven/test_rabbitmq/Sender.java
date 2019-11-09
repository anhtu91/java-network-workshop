package com.multi.modul.webapp.maven.test_rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * Recieve logs program 
 * @author Ramesh Fadatare
 *
 */
public class Sender {
	private final static String QUEUE_NAME = "Message_Queue";

	public static void main(String[] argv) throws Exception
	{
		ConnectionFactory factory = new ConnectionFactory();

		/*
		 * Here we connect to a broker on the local machine - hence
		 * the localhost. If we wanted to connect to a broker on a
		 * different machine we'd simply specify its name or IP
		 * address here.
		 */
		factory.setHost("192.168.1.2");
		//factory.setPort(1883);
		factory.setUsername("admin1");
		factory.setPassword("admin1");
		try (
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel())

		{
			channel.queueDeclare(QUEUE_NAME, false, false, false,
					null);
			String message = "Hello World!";
			channel.basicPublish("", QUEUE_NAME, null,
					message.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + message + "'");
		}
		catch (Exception exe)
		{
			exe.printStackTrace();
		}

	}
}