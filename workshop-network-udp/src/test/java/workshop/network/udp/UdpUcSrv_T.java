package workshop.network.udp;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.Phaser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import workshop.util.Tool;

//*************************************************************************************
/**
 * Test program
 * 
 * - Java DatagrammSocket/Packet (Unicast UDP) Server
 * 
 * - Server - Empfangen und spiegeln eines Datenpaketes
 * 
 * @author MK IKT
 * @version 2018-11-13
 * @see <a href=
 *      "https://docs.oracle.com/javase/8/docs/api/java/net/DatagramSocket.html">Javadoc
 *      DatagramSocket</a>
 * @see [Schildt 8. (C.21) Networking / S.682]
 *
 */
// *************************************************************************************
public class UdpUcSrv_T {
	/** log4j logger reference */
	private static final Logger logger = LogManager.getLogger(UdpUcSrv_T.class);

	/** Modul global parameters */
	private static final String VERSION = "Version 0.2";
	private static String PROP_FILE;
	private static final int WAIT_TIME = 100; // in msec
	private static Properties propFile;

	/** Tabelle mit closeable Objekten */
	final private static TreeMap<String, Closeable> tmClosable = new TreeMap<>(
			Tool.STRING_COMP); // mit Comparator fuer
								// String

	private static Phaser phaser = null;
	private static boolean abbruch = false;

	/** Implementierung von Thread.UncaughtExceptionHandler */
	final private static class Uncaught implements Thread.UncaughtExceptionHandler {
		/**
		 * Implementierung von UncaughtExceptionHandler: MUSS public sein
		 */
		public void uncaughtException(Thread t, Throwable T) {
			String name = t.getName(); // ermittle name vom
										// Thread
			logger.info(String.format("Thead name '%s' --> '%s'\n", name,
					T.getClass().getSimpleName()));

			/**
			 * UNBEDINGT: setze value fuer name: null UND schliesse OLD value
			 */
			try {
				Tool.CLOSE(tmClosable.put(name, null));
			} catch (Throwable TT) {
			}
			phaser.arrive();
			/** UNBEDINGT: Main wartet auf permit */
			if (phaser.getUnarrivedParties() == 1 && abbruch == false)
				/**
				 * ALLE Thread's beendet, ABER: Main Thread wartet mit IT.DIALOG
				 */
				Tool.CLOSE(System.in); // Abbruch von
										// IT.DIALOG
		}
	} // End of class Uncaught

	final private static Uncaught ueh = new Uncaught();

	/**
	 * <pre>
	 * SrvThread: Receive Thread **
	 * Implements Runnable, Closeable **
	 * </pre>
	 */
	final private static class SrvThread implements Runnable, Closeable {

		/** static: global Variable */
		static private int I = 0, rcv_buffer_size, send_buffer_size, rcvTimeout;

		/** final: const nach Constructor */
		final private int Index;
		final private String name;
		final private byte[] data;
		final private DatagramPacket dp;
		final private DatagramSocket ds;
		final private SocketAddress saSrcAddress;

		/** Socket Adresse erzeugt aus DatagramSocket mit ip:port */
		private static SocketAddress saSrcAddressFromDS = null;

		/**
		 * Merke length aus Prop-File, damit nach jedem Senden fuer den Empfang
		 * die max. length im Datagram Packet aud den Config Wert zurueckgesetzt
		 * wird. Max.length zu empfangene Daten.
		 */
		private int dataBufferSize = 0;

		/** Parameter fuer convert/close */
		private int length;
		private Thread t;
		private SocketAddress saDstAddress;

		public SrvThread(Properties srvProp) throws Throwable {
			/**
			 * initialisiere und starte Thread UNBEDINGT: speichere name/this in
			 * tm
			 */
			if (tmClosable.put(name = String.format("SrvThread[%d]", Index = I++),
					this) != null) {
				throw new IllegalArgumentException(String
						.format("FEHLER: Thread mit name %s existiert schon", name));
			}

			logger.info(String.format(
					"\nInitialize and start server thread with name '%s'\n",
					name.toString()));

			/** Initialize thread object */
			t = null;
			try {
				/** Sets local port from property file */
				saSrcAddress = new InetSocketAddress(
						srvProp.getProperty("srcAddress"),
						Integer.parseInt(srvProp.getProperty("srcPort")));
				ds = new DatagramSocket(saSrcAddress);

				/** Gets own IP address from localhost */
				saSrcAddressFromDS = ds.getLocalSocketAddress();

				/**
				 * Set send/receive data buffer size from property file
				 */
				ds.setReceiveBufferSize(
						Integer.parseInt(srvProp.getProperty("dsRcvBufferSize")));
				ds.setSendBufferSize(
						Integer.parseInt(srvProp.getProperty("dsSendBufferSize")));

				/** Get actual buffer size for send and receive */
				rcv_buffer_size = ds.getReceiveBufferSize();
				send_buffer_size = ds.getSendBufferSize();

				/** Set timeout in msec. 0 from property file */
				rcvTimeout = Integer.parseInt(srvProp.getProperty("rcvTimeout"));
				ds.setSoTimeout(rcvTimeout);

				/**
				 * Create DatagramPacket for dp.receive with data packet size in
				 * byte's from property file
				 */
				dp = new DatagramPacket(
						data = new byte[Integer
								.parseInt(srvProp.getProperty("dataBufferSize"))],
						Integer.parseInt(srvProp.getProperty("dataBufferSize")));

				/** Merke dataBufferSize aus Prop-File */
				dataBufferSize = Integer
						.parseInt(srvProp.getProperty("dataBufferSize"));

				logger.info(String.format(
						"\nInitialize UDP server with: saSrcAddress '%s', saSrcAddressFromDS '%s', InetAddress.getLocalHost() '%s', ds rcv_buffer_size '%d', "
								+ "ds send_buffer_size '%d', rcvTimeout '%d' msec, dp length '%d', data length '%d', getPort '%d'\n",
						saSrcAddress.toString(), saSrcAddressFromDS.toString(),
						InetAddress.getLocalHost(), rcv_buffer_size,
						send_buffer_size, rcvTimeout, dp.getLength(), data.length,
						ds.getLocalPort()));

				/**
				 * Only an example to get all ip addresses from network adapter
				 * of the localhost
				 */
				for (InetAddress ia : InetAddress
						.getAllByName(InetAddress.getLocalHost().getHostName()))
					logger.info(String.format(
							"\nGet all addresses from localHost '%s'\n", ia));

				/** Create thread with name */
				t = new Thread(this, name);

				/**
				 * UNBEDINGT: setze UncaughtExceptionHandler
				 */
				t.setUncaughtExceptionHandler(ueh);

				/** Call function run() */
				t.start();
			} catch (Throwable T) {
				/** Set resources free */
				close();
				/** Trace exception */
				logger.info(
						String.format("Thead name '%s' --> ExceptionType: '%s'\n",
								name, T.getClass().getSimpleName()));
				T.printStackTrace();
				/**
				 * Rethrow actual exception to function main...
				 */
				throw T;
			}
		}// End of constructor

		/**
		 * Implementierung von Closeable: MUSS public sein
		 */
		public void close() {
			if (t == null)
				return;
			/**
			 * Schliesse DatagramSocket --> SrvThread: Exception, Freigabe vom
			 * UDP Port
			 */
			ds.close();
			t = null;
		} // End of function close

		/** Implementation of Runnable: public */
		public void run() {
			/** warte auf Start der andern Thread's */
			if (phaser.arriveAndAwaitAdvance() < 0) {
				/** Abbruch durch Main - Freigabe der Resourcen */
				close();
				return;
			}

			logger.info(String.format("\nThread name '%s' wird gestartet.\n",
					name.toString()));
			int o = 0;
			for (;;) {
				try {
					o++;
					/** Falls Benutzer das Programm beendet */
					if (abbruch == true)
						break;

					/** Warte max rcvTimeout sec auf Daten */
					ds.receive(dp);
					length = dp.getLength();
					saDstAddress = dp.getSocketAddress();
					String sReceive = new String(dp.getData(), 0, dp.getLength());

					logger.info(String.format(
							"\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n"));
					logger.info(String.format(
							"\nReceived message '%d', UDP server address '%s' received data from client address '%s' <==> name '%s', data length '%d', data '%s'.\n",
							o, saSrcAddress.toString(), saDstAddress.toString(),
							dp.getAddress(), length, sReceive.toString()));

					/** Mirror received data */
					if (saDstAddress.equals(saSrcAddress) == false) {
						/**
						 * Modify String- create data byte array and copy ret
						 * --> data
						 */
						byte[] ret_data = (sReceive).getBytes();

						/**
						 * Initialize the DatagramPacket with new memory
						 */
						dp.setData(ret_data);

						/** versende ret_data.length byte */
						dp.setLength(ret_data.length);

						/**
						 * Demonstriere das Versenden von 0 byte
						 */
						if (Boolean.parseBoolean(
								propFile.getProperty("zeroByte")) == true) {
							dp.setLength(0);
						}

						logger.info(String.format(
								"\nSend data byte array: dp.getData '%s', dp.getLength '%d' => to ip address '%s', delay '%d' msec\n",
								new String(dp.getData()), dp.getLength(),
								saDstAddress.toString(), Integer.parseInt(
										propFile.getProperty("sendTimeout"))));

						/**
						 * Verzoegerung beim Zuruecksenden, Demonstration von
						 * Datenverlust
						 */
						Thread.sleep(Integer
								.parseInt(propFile.getProperty("sendTimeout")));

						/**
						 * sende empfangene Daten zurueck zu remote
						 */
						ds.send(dp);
						logger.info(String.format(
								"\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n"));

					}
				} catch (Throwable T) {
					if (T instanceof SocketTimeoutException) {
						logger.info(String.format(
								"\nUDP Server '%s' mit Adresse '%s' wartet max '%d' msec. auf Daten mit max Laenge '%d'.\n",
								name, saSrcAddress, rcvTimeout, data.length));
						continue;
					}
					if (!(T instanceof SocketException)) {
						logger.info(String.format("\nThead name '%s' --> '%s'\n",
								name, T.getClass().getSimpleName()));
						T.printStackTrace();
					}
					break;
				} finally {
					/**
					 * Setze rcv data (byte array) mit max. length auf Config
					 * Wert zurueck
					 */
					logger.info(String.format(
							"\nSetze byte array fuer rcv - data und data.length in dp - length '%s'\n",
							data.length));
					dp.setData(data);
					dp.setLength(data.length);
				}
			}
			logger.info(String.format("\nThread Ende '%s'\n", name));
			/** For function close() */
			t = null;
			phaser.arrive();

			/** IMPORTANT: Main waiting for permit */
			if (phaser.getUnarrivedParties() == 1 && abbruch == false)

				/**
				 * ALLE Thread's beendet, ABER: Main Thread wartet mit IT.DIALOG
				 */
				Tool.CLOSE(System.in); // Abbruch von
										// IT.DIALOG
		} // End of function run

		/** finalize(): MUSS protected sein */
		protected void finalize() {
			close();
		}
	} // End of class SrvThread

	public static void main(String[] args) {
		SrvThread[] srvThreadTab = null;

		try {
			logger.info(
					String.format("Start T_SIMPLE_UDP_SRV with '%s'!!!\n", VERSION));

			/** Checks arguments */
			switch (args.length) {
			case 1:
				/** Sets the propFile */
				PROP_FILE = args[0];
				File file = new File(PROP_FILE);

				/** File exists */
				if (file.exists() == false)
					throw new IllegalArgumentException(
							String.format("BAD_PARAM: PROP_FILE does not exists"));

				/** READING PROPERTIES FILE */
				propFile = new Properties();

				/** Loads the properties file */
				propFile.load(new FileInputStream(PROP_FILE));
				break;
			default:
				logger.info(String.format(
						"Args.length '%d', expect 1 parameter (CONFIG FILE) !!!\n",
						args.length));

				/** End of the program */
				System.exit(1);
			}

			/** Pruefe Anzahl der Parameter */
			switch (propFile.size()) {
			case 8:
				/** Displays prop Info */
				logger.info(String.format(
						"\nPROPERTIES FILE values:\nsrcAddress: \t\t'%s'\nsrcPort: \t\t'%s'\n"
								+ "dsRcvBufferSize: \t'%s' \tin byte\ndsSendBufferSize: \t'%s' \tin byte\ndataBufferSize: \t'%s' \tin byte\n"
								+ "rcvTimeout: \t\t'%s' \tin msec\nsendTimeout: \t\t'%s' \tin msec\nzeroByte: \t\t'%s'\n",
						propFile.getProperty("srcAddress"),
						propFile.getProperty("srcPort"),
						propFile.getProperty("dsRcvBufferSize"),
						propFile.getProperty("dsSendBufferSize"),
						propFile.getProperty("dataBufferSize"),
						propFile.getProperty("rcvTimeout"),
						propFile.getProperty("sendTimeout"),
						propFile.getProperty("zeroByte")));

				/** Creates the thread, only one channel */
				srvThreadTab = new SrvThread[1];

				/** Starts phaser => IMMER: Anzahl Thread's +1 */
				phaser = new Phaser(1 + srvThreadTab.length);
				int i = 0;
				try {
					for (i = 0; i < srvThreadTab.length; i++)
						srvThreadTab[i] = new SrvThread(propFile);
				} catch (Throwable T) {
					/** Abbruch der Thread's */
					phaser.forceTermination();
					throw new IllegalArgumentException(String.format(
							"Abbruch: srvThreadTab[%d] = new SrvThread()", i - 1));
				}
				/** warte auf Start der Thread's */
				phaser.arriveAndAwaitAdvance();

				/** fuer Ausgabe der Thread's */
				Thread.sleep(1000);
				break;
			default:
				/** Invalid number of prop param */
				throw new IllegalArgumentException(String.format(
						"\nBAD_PARAM: number of properties parameter '%d', required 8 parameters",
						propFile.size()));
			}

			/** Waiting for user input => end of program */
			String str = Tool.Dialog(
					String.format("%tT: Main Thread (Abbruch mit 'Return'): ",
							Calendar.getInstance()));

			/**
			 * kleiner delay, damit eventuell HOOK Thread startet !
			 */
			Thread.sleep(WAIT_TIME);
			logger.info(
					String.format("\nProgramm wurde durch den Benutzer beendet.\n"));

			/**
			 * Set abbruch, um Threads von Innen zu schliessen...
			 */
			abbruch = true;

			/** Close srv threads */
			if (Boolean.valueOf(propFile.getProperty("serverMode")) != true)
				/** schliesse Srv Thread's */
				srvThreadTab = Tool.CLOSE(srvThreadTab);

			/** Close phaser */
			if (phaser != null)
				/** warte auf das Ende der Thread's */
				phaser.arriveAndAwaitAdvance();

			logger.info(String.format("\nMain Thread wurde beendet.\n"));
		} catch (Throwable T) {
			/** Trace error */
			logger.error(T.getMessage(), T);

			/** End of the program */
			System.exit(1);
		}
	}// End of function main
}// End of class UdpUcSrv_T
