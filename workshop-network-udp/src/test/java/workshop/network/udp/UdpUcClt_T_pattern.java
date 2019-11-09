package workshop.network.udp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Calendar;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//*******************************************************************************
/**
 * <pre>
 * Java DatagrammSocket/Packet (Unicast UDP) Client
 * 
 * Client sendet UDP Datenpakete (Inhalt String, Eingabe ueber Dialog). Client
 * wartet auf Datenpaket (Timeout), welches der Server zuruecksendet.
 * </pre>
 * 
 * @author MK IKT
 * @version 2018-11-13
 * @see <a href=
 *      "https://docs.oracle.com/javase/8/docs/api/java/net/DatagramSocket.html">Javadoc
 *      DatagramSocket</a>
 * @see [Schildt 8. (C.21) Networking / S.682]
 *
 */
// *******************************************************************************
public class UdpUcClt_T_pattern {
	/** log4j logger reference */
	private static final Logger logger = LogManager
			.getLogger(UdpUcClt_T_pattern.class);

	/** Modul global parameters */
	private static String PROP_FILE;
	private static Properties propFile;

	/** The socket */
	private static DatagramSocket ds = null;

	/**
	 * Socket Adresse erzeugt aus DatagramSocket mit /ip:port
	 */
	private static SocketAddress saSrcAddressFromDS = null;

	/**
	 * Socket Adresse mit hostname/ip:port erzeugt aus InetSocketAddress
	 */
	private static SocketAddress saSrcAddress = null;

	/**
	 * Socket Adresse mit hostname/ip:port erzeugt aus InetSocketAddress
	 */
	private static SocketAddress saDstAddress = null;

	/** The receive timeout */
	private static int rcvTimeout = 0;

	/**
	 * Speicher (Byte-Array) zum Senden und Empfangen von Daten. (wird von
	 * DatagramPacket verwendet)
	 */
	private static byte[] data = null;

	/**
	 * UDP Paket welches mithilfe des Datagram Socket versendet und empfangen
	 * wird.
	 */
	private static DatagramPacket dp = null;

	/** JAVA, S. 290: Reading Strings */
	final private static BufferedReader brConsole = new BufferedReader(
			new InputStreamReader(System.in));

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
	} // Ende von DIALOG

	/**
	 * Main Funktion.
	 * 
	 * @param args
	 *            Uebergabeparameter Property File.
	 * @exception Standard.
	 */
	public static void main(String[] args) {

		try {
			/**
			 * Aufgabe 2 - Check arguments
			 */
			switch (args.length) {
			case 1:
				/**
				 * Initializes the property file from argument
				 */
				PROP_FILE = args[0];
				File file = new File(PROP_FILE);

				/** Checks if the property file exists */
				if (file.exists() == false)
					throw new IllegalArgumentException(
							String.format("BAD_PARAM: PROP_FILE does not exists"));

				/** Creates a new property file object */
				propFile = new Properties();

				/** Reads the property file parameters */
				propFile.load(new FileInputStream(PROP_FILE));
				break;
			default:
				logger.info(String.format(
						"\nArgs.length '%d', "
								+ "required 1 parameter (CONFIG FILE) !!!\n",
						args.length));
				/**
				 * End of the program with System.exit()
				 */
				System.exit(1);
			}

			/** Checks the number of arguments */
			switch (propFile.size()) {
			case 8:
				/** Trace info - Aufgabe 2 */
				logger.info(String.format(
						"\nPROPERTIES FILE values:\ndstAddress: \t\t'%s' "
								+ "hostname or ip (x.x.x.x)\ndstPort: \t\t'%s'\nsrcPort: \t\t'%s'\n"
								+ "dsRcvBufferSize: \t'%s' \tin byte\ndsSendBufferSize: \t'%s' \tin byte\n"
								+ "dataBufferSize: \t'%s' \tin byte\nrcvTimeout: \t\t'%s' \tin sec\n",
						propFile.getProperty("srcAddress"),
						propFile.getProperty("dstAddress"),
						propFile.getProperty("dstPort"),
						propFile.getProperty("srcPort"),
						propFile.getProperty("dsRcvBufferSize"),
						propFile.getProperty("dsSendBufferSize"),
						propFile.getProperty("dataBufferSize"),
						propFile.getProperty("rcvTimeout")));
				break;
			default:
				/** Invalid number of property parameters */
				throw new IllegalArgumentException(String.format(
						"BAD_PARAM: number of properties parameter '%d', required 7 parameters",
						propFile.size()));
			}

			/** Aufgabe 3 a) - h) */
			
			//a
			saSrcAddress = new InetSocketAddress(propFile.getProperty("srcAddress"), Integer.parseInt(propFile.getProperty("srcPort")));
			
			//b
			ds = new DatagramSocket(saSrcAddress);
			
			//c
			logger.info("Ihre IP Addresse/Port: ", ds.getLocalSocketAddress());
			
			//d
			ds.setSendBufferSize(Integer.parseInt(propFile.getProperty("dsSendBufferSize")));
			ds.setReceiveBufferSize(Integer.parseInt((propFile.getProperty("dsRcvBufferSize"))));

			//e
			rcvTimeout = Integer.parseInt(propFile.getProperty("rcvTimeout"));
			
			//f
			saDstAddress = new InetSocketAddress(propFile.getProperty("dstAddress"), Integer.parseInt(propFile.getProperty("dstPort")));
			
			logger.info("Inhalt des Objecktes saDstAddress: %s", saDstAddress.toString());
			
			//g
			data = new byte[Integer.parseInt(propFile.getProperty("dataBufferSize"))];
			
			//h
			dp = new DatagramPacket(data, 0);
			
			
			/**
			 * Try to send user text input to udp server - Aufgabe 6 => for(;;)
			 */
			for (;;) {
				/**
				 * Aufgabe 4 => OHNE for(;;) Waiting for user input => end of
				 * programm Halte Programmablauf an, Programm beenden mit 'q'
				 */
				String ret = Dialog(String.format(
						"%tT: Input text and send to address '%s:%s' - Exit program - press terminate button in Eclipse IDE or press key 'q': ",
						Calendar.getInstance(), propFile.getProperty("dstAddress"),
						propFile.getProperty("dstPort")));

				/**
				 * Beende Programm mit "q" fuer Quit, da CTRL-C nicht in Eclipse
				 * Console unterstuetzt wird - Aufgabe 6.
				 */
				if (ret.compareTo("q") == 0) {
					logger.info(String.format(
							"\nProgramm wurde durch den Benutzer beendet.\n"));
					/** End of the program */
					break;
				}

				/** Aufgabe 5 a) - d) */
				
				//a
				int length = data.length;
				ret.getBytes(0, length, data, 0);
				dp.setLength(length);
				
				//b
				dp.setSocketAddress(saDstAddress);
				
				//c
				logger.info("Inhalt data: %s", data.toString());
				logger.info("Data length: %d", data.length);
				logger.info("Data und die Länge von data des Objekts dp: %s, %d ", dp.toString(), dp.getLength());
				logger.info("Die Adresse des UDP-Servers: %s", saDstAddress.toString());
				
				//d
				ds.send(dp);
				/** Aufgabe 7 */
				// TODO

			} // End of for (;;) loop
			logger.info(String.format("\nMain Thread wird beendet.\n"));
		} catch (Throwable T) {
			/** Trace error */
			logger.error(T.getMessage(), T);
		} finally {
			logger.info(String
					.format("\nMain Thread finally Block - Ressourcenfreigabe.\n"));
			/** Close socket */
			if (ds != null)
				ds.close();

			/**
			 * Set object equal null for garbage collector
			 */
			data = null;
			dp = null;
			logger.info(String.format("\nProgramm Ende !!!.\n"));
		}
	}// End of main
}// End of class UdpUcClt_T_pattern
