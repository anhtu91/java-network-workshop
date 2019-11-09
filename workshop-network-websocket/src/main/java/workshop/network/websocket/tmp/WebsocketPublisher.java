package workshop.network.websocket.tmp;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import workshop.network.websocket.EventWebSocketAdapter_R;
import workshop.network.websocket.EventWebSocketServlet_R;


public class WebsocketPublisher {

	private static final Logger logger = LogManager.getFormatterLogger(EventWebSocketAdapter_R.class);
	private static String message = null;
	private static String mode = null;
	private static Scanner sc = new Scanner(System.in); 
	private static EventWebSocketServlet_R ew = null;
	private static WebSocketAdapter wsa = null;
	
	
	public static void main(String [] args) {
		
		ew = new EventWebSocketServlet_R();
		
		logger.info("Enter your message: ");
		message = sc.nextLine();
		
		if(message.isEmpty()) {
			logger.info("Input message is empty");
		}else {
			logger.info("Enter mode: ");
			mode = sc.nextLine();
			
			//Create subscriber
			//ew.mainSubscriber(mode);
			
			//Create publisher
			//ew.mainPublisher(mode, message);
			
			
			
		}
		
	}
	 
	
}
