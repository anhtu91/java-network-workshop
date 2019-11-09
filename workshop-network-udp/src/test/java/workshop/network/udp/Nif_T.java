package workshop.network.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//*******************************************************************************
/**
 * <pre>
 * Java network interfaces
 * 
 * An application to demonstrate how to read all network interfaces of the local
 * system.
 * </pre>
 * 
 * @author IKT MK
 * @version 2016-06-02
 */
// *******************************************************************************
public class Nif_T {
	/** log4j logger reference for class MBus_T */
	private static final Logger logger = LogManager.getLogger(Nif_T.class);

	/**
	 * This network interface name will be search in the network interface list
	 */
	private static String NETWORK_INTERFACE_NAME = "lo";
	// private static String NETWORK_INTERFACE_NAME = "eth3";

	/** Dummy port */
	private static int PORT = 60000;

	/**
	 * Simple main function
	 * 
	 * @param args
	 *            Non
	 */
	public static void main(String[] args) {
		try {
			/** Trace info */
			logger.info(String.format("\nStart application ...\n"));

			/**
			 * Gets all network interfaces of the local system
			 */
			Enumeration<NetworkInterface> nifs = NetworkInterface
					.getNetworkInterfaces();

			/**
			 * Lists all NetworkInterface(es) by name and display name
			 */
			for (NetworkInterface nif : Collections.list(nifs)) {
				logger.info(
						String.format("\nDisplay nif name: '%s' | nif name: '%s'.\n",
								nif.getDisplayName(), nif.getName()));

				/**
				 * Gets all InetAddress(es) of the actual network interface
				 */
				Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();

				/**
				 * Lists the InetAddress and MAC address of the actual network
				 * interface
				 */
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					logger.info(String.format(
							"\nInetAddress: '%s' of network interface '%s'\n",
							inetAddress.toString().replaceAll("[/]", ""),
							nif.getName()));
				}
			}

			/** Gets the network interface by name */
			NetworkInterface nifInstalled = NetworkInterface
					.getByName(NETWORK_INTERFACE_NAME);

			/**
			 * Gets the InetAdresses from the defined network interface
			 */
			Enumeration nifInetAddresses = nifInstalled.getInetAddresses();

			/**
			 * Creates a server socket address from the InetSocketAddress with
			 * host name, ip and port
			 */
			SocketAddress saSrcAddress = new InetSocketAddress(InetAddress.getByName(
					nifInetAddresses.nextElement().toString().replaceAll("[/]", "")),
					PORT);

			/** Trace info the socket address */
			logger.info(String.format("\nTry to bind on socket '%s'.\n",
					saSrcAddress.toString().replaceAll("[/]", "")));
		} catch (Throwable T) {
			/** Trace error */
			logger.catching(T);
		} finally {
			/** Trace info */
			logger.info(String.format("\nShutdown application ...\n"));
		}
	}// End of main
}// End of Nif_T