/** Package => */
package workshop.network.websocket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/*************************************************************************************/
/**
 * This is a simple websocket server.
 * 
 * @author MK
 * @version 2018-07-02
 */
/**************************************************************************************/
public class HttpWebSocketServer_R {
	/** log4j logger reference for class Start_T */
	private static final Logger logger = LogManager.getFormatterLogger(HttpWebSocketServer_R.class);

	/** Modul global parameters */
	private static String PROP_FILE;
	private static Properties propFile;

	/** The http server object */
	private static Server jettyServer;

	/** Optional, only use to configure the server */
	private static ServerConnector connector;

	/** Thread pool to start the notifivation thread */
	private static ExecutorService threadPool = null;


	/**
	 * Socket Adresse mit hostname/ip:port erzeugt aus InetSocketAddress
	 */
	private static InetSocketAddress isaSrcAddress = null;

	/** JAVA, S. 290: Reading Strings */
	final private static BufferedReader brConsole = new BufferedReader(new InputStreamReader(System.in));

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
				/** Creates a new Hook Thread */
				new Thread(new Runnable() {
					@Override
					public void run() {
						/** Shutdown the http server */
						if (jettyServer != null)
							jettyServer.destroy();

						threadPool.shutdown();

						/** Trace info */
						logger.info("! Hook thread - shutdown the application !");
					}
				}));
	}// End of startShutDownHook

	/**
	 * Description ...
	 * 
	 * @param args
	 *            Uebergabeparameter.
	 * @return -.
	 * @exception Throwable.
	 */
	public static void main(String[] args) {
		try {
			/** First starts the hook thread */
			startShutDownHook();

			/**
			 * Aufgabe 2) Checks the arguments
			 */
			switch (args.length) {
				case 1:
					/** Sets the propFile */
					PROP_FILE = args[0];
					File file = new File(PROP_FILE);
	
					/** File exists ? */
					if (file.exists() == false)
						throw new IllegalArgumentException(String.format("BAD_PARAM: PROP_FILE does not exists"));
	
					/** Creating PROPERTIES object */
					propFile = new Properties();
	
					/** READING PROPERTIES from FILE */
					propFile.load(new FileInputStream(PROP_FILE));
					break;
				default:
					logger.info(String.format("\nArgs.length '%d', " + "required 1 parameter (CONFIG FILE) !!!\n",
							args.length));
	
					/** Shutdown the application */
					System.exit(1);
			}

			/** Checks the number of parameters in properties file */
			switch (propFile.size()) {
				case 3:
					/** Displays the properties info */
					logger.info(String.format("\nProperty file values: srcAddress '%s', srcPort '%s', contextPath '%s'",
							propFile.getProperty("srcAddress"), propFile.getProperty("srcPort"),
							propFile.getProperty("contextPath")));
	
					break;
				default:
					/** Invalid number of prop parameters */
					throw new IllegalArgumentException(String.format(
							"BAD_PARAM: number of properties parameter '%d', required 3 parameters", propFile.size()));
			}

			/** Initializes the source address to install */
			isaSrcAddress = new InetSocketAddress(propFile.getProperty("srcAddress"),
					Integer.parseInt(propFile.getProperty("srcPort")));

			/** Uses the saSrcAddress */
			jettyServer = new Server(isaSrcAddress);

			/**
			 * Connector is only used to configure the server basic properties
			 * e.g. the port .
			 */
			
			connector = new ServerConnector(jettyServer);


			/**
			 * Setup the basic application "context" for this application at.
			 * This is also known as the handler tree (in jetty speak)
			 */
			ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
			handler.setContextPath("/");
			jettyServer.setHandler(handler);

			/** Adds the servlet and extends the context path */
			handler.addServlet(EventWebSocketServlet_R.class, propFile.getProperty("contextPath"));

			logger.info("Starting the jetty server ...");

			/** Initializes and starts the jetty server **/
			jettyServer.start();

			/** Enables the dump logging of the jetty server */
			// jettyServer.dump(System.err);

			logger.info("Starting the notification thread ...");

			/** Creates a thread pool */
			threadPool = Executors.newCachedThreadPool();

			/**
			 * Executes a new thread to handle the notification
			 */
			threadPool.execute(new EventTask_R());

			logger.info("Calling the jetty server join function ...");

			/**
			 * The function join() is blocking until server is ready. It behaves
			 * like Thread.join() and indeed calls join() of Jetty's thread
			 * pool. Everything works without this because jetty starts very
			 * quickly. However the application is heavy enough the start might
			 * take some time. Call of join() guarantees that after it the
			 * server is indeed ready.
			 */
			jettyServer.join();
		} catch (Throwable T) {
			/** Trace error */
			logger.catching(T);
		} finally {
			/** Trace info */
			logger.info("Application terminates ...");

			/** Shutdown the notification thread */
			threadPool.shutdown();

			/** Shutdown the HTTP server */
			jettyServer.destroy();
		}
	}// End of function main
}// End of class HttpWebSocket_T
