package workshop.network.tcp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//*******************************************************************************
/**
 * <pre>
 * Java Socket stream (TCP) Server
 * 
 * - Waiting for incoming connections
 * - Waiting for incoming messages
 * - Responding the incoming messages
 * - Shutdown the connection with the client
 * 
 * [Schildt 8. (C.21) Networking / S.682]
 * </pre>
 * 
 * @author MK IKT
 * @version 2016-11-21
 *
 */
// *******************************************************************************
public class TcpEchoSrv_T {
	/** log4j logger */
	private static final Logger LOGGER = LogManager.getLogger(TcpEchoSrv_T.class);

	/** Properties objects */
	private static String PROP_FILE;
	private static Properties propFile;

	/** Thread pool to response incoming requests */
	static private ExecutorService threadPool = null;

	/** The server socket */
	ServerSocket serverSocket = null;

	/** The source address */
	SocketAddress saSrcAddress = null;

	/** TCP Server thread to response incoming requests */
	private static class Task implements Runnable {
		/* Reference to the socket - unique connection to client */
		private Socket socket;

		/**
		 * The constructor
		 * 
		 * @param socket
		 *            The Socket handle which corresponded with the client
		 *            socket
		 */
		public Task(Socket socket) {
			this.socket = socket;
		}

		/** The run function - started as thread */
		public void run() {
			/** Contains the message from the client */
			String input = new String();

			/**
			 * Links the socket with an input and out stream to receive and send
			 * messages
			 */
			try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

				for (;;) {
					/** Trace info */
					LOGGER.info(String.format("Thread '%s' Waiting for incoming data from client '%s'\n",
							Thread.currentThread().getName(), socket.getInetAddress().toString()));

					/** Sets timeout waiting for incoming messages */
					socket.setSoTimeout(Integer.parseInt(propFile.getProperty("rcvTimeout")));

					/** Are there any more data to receive ? */
					while ((input = in.readLine()) != null) {
						/** Trace info */
						LOGGER.info(String.format(
								"Thread %s, receives data with content input '%s' => response delay (sendTimeout) '%d'\n",
								Thread.currentThread().getName(), input,
								Integer.parseInt(propFile.getProperty("sendTimeout"))));

						/** Process in work */
						Thread.sleep(Integer.parseInt(propFile.getProperty("sendTimeout")));

						/** Version 1: Simple echo */
						String output = input.toString();

						/** Version 2: Http response */
						// String output = "HTTP/1.1 200 OK\n" +
						// String.format("%s\n", new Date())
						// + "Server: TcpEchoSrv_T\n" + "Last-Modified: Wed, 21
						// Dec 2016 19:15:56 GMT\n"
						// + "Content-Length: 48\n" + "Content-Type:
						// text/html\n" + "Connection: Closed\n" + "\n"
						// + "<html>\n" + "<body>\n" + "<h1>Hello, World -
						// Goodby World!</h1>\n" + "</body>\n"
						// + "</html>\n";

						/** Trace info */
						LOGGER.info(String.format("Thread '%s', sends data with content \n'%s'",
								Thread.currentThread().getName(), output));

						/** Process finish - sending an echo to the client */
						out.println(output);
					}
					/** Sockets closed */
					break;
				}
			} catch (Throwable T) {
				/** Trace error */
				LOGGER.error(T.getMessage(), T);
			} finally {
				/** Trace info */
				LOGGER.info(String.format("Thread '%s' shutdown ... connection closed with client '%s'\n",
						Thread.currentThread().getName(), socket.getRemoteSocketAddress().toString()));
			}
		}
	}// End of class Task

	/**
	 * The main function
	 * 
	 * @param args
	 *            [0] Properties file
	 */
	public static void main(String[] args) {

		try {
			/**
			 * Checks the arguments
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
				LOGGER.info(String.format("\nArgs.length '%d', " + "required 1 parameter (CONFIG FILE) !!!\n",
						args.length));

				/** Shutdown the program */
				System.exit(1);
			}

			/** Checks the number of parameters in properties file */
			switch (propFile.size()) {
			case 8:
				/** Displays the properties info */
				LOGGER.info(String.format(
						"\nPROPERTIES FILE values:\nsrcAddress: \t\t'%s'\nsrcPort: \t\t'%s'\n"
								+ "dsRcvBufferSize: \t'%s' \tin byte\ndsSendBufferSize: \t'%s' \tin byte\ndataBufferSize: \t'%s' \tin byte\n"
								+ "rcvTimeout: \t\t'%s' \tin msec\nsendTimeout: \t\t'%s' \tin msec\nzeroByte: \t\t'%s'\n",
						propFile.getProperty("srcAddress"), propFile.getProperty("srcPort"),
						propFile.getProperty("dsRcvBufferSize"), propFile.getProperty("dsSendBufferSize"),
						propFile.getProperty("dataBufferSize"), propFile.getProperty("rcvTimeout"),
						propFile.getProperty("sendTimeout"), propFile.getProperty("zeroByte")));

				break;
			default:
				/** Invalid number of prop parameters */
				throw new IllegalArgumentException(String.format(
						"BAD_PARAM: number of properties parameter '%d', required 8 parameters", propFile.size()));
			}

			/** Creates the socket address */
			SocketAddress saSrcAddress = new InetSocketAddress(propFile.getProperty("srcAddress"),
					Integer.parseInt(propFile.getProperty("srcPort")));

			/** Installs the socket - Autoclosable */
			try (ServerSocket serverSocket = new ServerSocket()) {
				/** Binds the socket ... */
				serverSocket.bind(saSrcAddress);

				/** Trace info */
				LOGGER.info(String.format("ServerSocket successful binded .... on address '%s'\n",
						saSrcAddress.toString()));

				/** Creates a thread pool */
				threadPool = Executors.newCachedThreadPool();

				/** Handle incoming connections */
				for (;;) {
					/** Trace info */
					LOGGER.info(String.format("Waiting for an incomming connection from a tcp client ...\n"));

					/** Waiting for incoming connections */
					Socket socket = serverSocket.accept();

					/** Trace info */
					LOGGER.info(String.format("Incoming connection with client '%s'\n",
							socket.getRemoteSocketAddress().toString()));

					/**
					 * Executes a new thread to handle the communication with
					 * the client
					 */
					threadPool.execute(new Task(socket));
				} // End of for
			} catch (Throwable T) {
				/** Trace error */
				LOGGER.catching(T);
			}
		} catch (Throwable T) {
			/** Trace error */
			LOGGER.error(String.format("Exception: --> %s", T));
		} finally {
			/** Shutdown thread pool - Disable new tasks from being submitted */
			threadPool.shutdown();
			try {
				/** Wait a while for existing tasks to terminate */
				if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
					/** Cancel currently executing tasks */
					threadPool.shutdownNow();

					/** Wait a while for tasks to respond to being cancelled */
					if (!threadPool.awaitTermination(10, TimeUnit.SECONDS))
						System.err.println("Pool did not terminate");
				}
			} catch (Throwable T) {
				/** Trace error */
				LOGGER.error(T.getMessage(), T);
			}
			LOGGER.info(String.format("Program shutdown ... pool isShutdown '%s'!!!\n", threadPool.isShutdown()));
		}
	}// End of main
}// End of TcpEchoSrv_T