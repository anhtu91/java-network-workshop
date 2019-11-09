package workshop.network.tcp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//*******************************************************************************
/**
 * <pre>
 * Java Socket stream (TCP) Client
 * 
 * - Connecting the server
 * - Sending data
 * - Receiving echo from server
 * - Disconnecting the server
 * </pre>
 * 
 * @author MK IKT
 * @version 2018-11-21
 * @see <a href=
 *      "https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html">Javadoc
 *      Socket</a>
 * @see <a href=
 *      "https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html">Javadoc
 *      BufferedReader</a>
 * @see <a href=
 *      "https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html">Javadoc
 *      PrintWriter</a>
 * @see [Schildt 8. (C.21) Networking / S.682]
 *
 */
// *******************************************************************************
public class TcpEchoClt_T_pattern {
	/** log4j logger */
	private static final Logger LOGGER = LogManager.getLogger(TcpEchoClt_T_pattern.class);

	/** Modul global parameters */
	private static String PROP_FILE;
	private static Properties propFile;

	/** Socket connector */
	static private Socket socket = null;
	static private BufferedReader inFromServer = null;
	static private PrintWriter outToServer = null;

	/** The network interface to connect with */
	static private String NetworkInterfaceName;

	/** JAVA, S. 290: Reading Strings */
	final private static BufferedReader brConsole = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * <pre>
	 * DIALOG: Ausgabe von formatter/args ueber System.out 
	 * warte beliebig lange auf System.in auf Input.
	 * </pre>
	 * 
	 * @param s
	 *            String formatiert.
	 * @return Input Benutzereingabe.
	 */
	public static String Dialog(String s) {
		LOGGER.info(s);
		try {
			return brConsole.readLine();
		} catch (Throwable T) {
			return null;
		}
	} // End of DIALOG

	/**
	 * The main function
	 * 
	 * @param args
	 *            [0] Properties file
	 */
	public static void main(String[] args) {

		try { /** Aufgabe 3 b) */ // TODO

			/** Aufgabe 2 */
			// TODO

			/** Lists all network interfaces */
			Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface nif : Collections.list(nifs)) {
				LOGGER.info(String.format("\nDisplay nif name: '%s' | nif name: '%s'.\n", nif.getDisplayName(),
						nif.getName()));
				Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					LOGGER.info(String.format("\nInetAddress: '%s'\n", inetAddress.toString().replaceAll("[/]", "")));
				}
			}

			/** Aufgabe 3 a) */
			// TODO

			/** Aufgabe 3 c) */
			// TODO

			/** Aufgabe 3 d) */
			// TODO

			/** Aufgabe 3 e) */
			// TODO

			/** Aufgabe 3 g) */
			// TODO

			/** Try to send data to the tcp server */
			for (;;) {

				/** Aufgabe 4 */
				// TODO

				/** Aufgabe 5 */
				// TODO

				/** Aufgabe 6 */
				// TODO

			}
		} catch (Throwable T) {
			/** Trace error */
			LOGGER.error(T.getMessage(), T);
		} finally {
			try {
				/** Closes all resources */
				if (inFromServer != null) {
					/** Trace info */
					LOGGER.info(String.format("Closes the input stream inFromServer ...\n"));
					inFromServer.close();
				}
				if (outToServer != null) {
					/** Trace info */
					LOGGER.info(String.format("Closes the output stream outFromServer ...\n"));
					outToServer.close();
				}
				if (socket != null) {
					/** Trace info */
					LOGGER.info(String.format("Closes the socket ...\n"));
					socket.close();
					Thread.sleep(1000);
				}
			} catch (Throwable T) {
				/** Trace error */
				LOGGER.error(T.getMessage(), T);
			}
			/** Trace info */
			LOGGER.info(String.format("Program shutdown ... - socket isClosed() '%s'\n", socket.isClosed()));
		}
	}// End of main
}// End of TcpEchoClt_T