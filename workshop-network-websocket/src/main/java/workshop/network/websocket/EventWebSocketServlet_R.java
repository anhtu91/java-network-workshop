package workshop.network.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Well, it's just a servlet declaration.
 * 
 * @author MK
 * @version 2018-07-02
 */
public class EventWebSocketServlet_R extends WebSocketServlet {

	/** A unique serial id */
	private static final long serialVersionUID = -8392610172228284350L;

	@Override
	public void configure(WebSocketServletFactory factory) {
		/** Registers the websocket resource */
		factory.register(EventWebSocketAdapter_R.class);
	}
}