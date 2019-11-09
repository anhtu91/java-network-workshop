/** Package => */
package workshop.util;

/** Imports => */
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Comparator;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * @author MK
 * @version 04.12.2013
 * 
 *          <pre>
 * - Nuetzliche Konstantanten
 * - UDP/TCP Port Bereiche
 * - LOCAL_ADDR (erzeuge aus Port und IP eine InetSocketAddress)
 * - Dialog (Benutzereingabe ueber Konsole)
 * - Konverterfunktionen:
 *   + WRITE  => primitiver Datentyp zu Byte-Array
 *   + BTO<?> => Byte-Array zu primitiver Datentyp
 * - CLOSE
 *          </pre>
 */
public class Tool {
	/** String NULL = "null"; */
	final static String NULL = "null";
	/** String NL = "\n"; */
	final static String NL = "\n";
	/** String BLANK = " "; */
	final static String BLANK = " ";
	/** String LEER = ""; */
	final static String LEER = "";

	/** UDP/TCP: Start von registered ports */
	final public static int MIN_REGISTERED_PORT = 1024;
	/** UDP/TCP: Ende von registered ports */
	final public static int MAX_REGISTERED_PORT = 49151;
	/** UDP/TCP: Start von dynamic ports */
	final public static int MIN_DYNAMIC_PORT = 49152;
	/** UDP/TCP: Ende von dynamic ports */
	final public static int MAX_DYNAMIC_PORT = 65534;

	/**
	 * <pre>
	 * erzeuge aus port gueltige local Address
	 * &#64;param port
	 *        (0 --> system pick up an ephemeral port 
	 *        usual: MIN_REGISTERED_PORT ... MAX_DYNAMIC_PORT
	 * &#64;param allow_registered 
	 *   true  --> MIN_REGISTERED_PORT ... MAX_DYNAMIC_PORT
	 *   false --> MIN_DYNAMIC_PORT    ... MAX_DYNAMIC_PORT
	 * &#64;return InetSocketAddress
	 * &#64;throws IT.EX
	 * </pre>
	 */
	public static InetSocketAddress LOCAL_ADDR(int port, boolean allow_registered) throws Throwable {
		if (port != 0) {
			int min_port = allow_registered == true ? MIN_REGISTERED_PORT : MIN_DYNAMIC_PORT;
			if (port < min_port || port > MAX_DYNAMIC_PORT)
				throw new Exception(String.format("Port(%d): NICHT in %d ... %d", port, min_port, MAX_DYNAMIC_PORT));
		}
		try {
			return new InetSocketAddress(InetAddress.getLocalHost(), port);
		} catch (Throwable T) {
			throw T;
		}
	} // Ende von LOCAL_ADDR

	/** return LOCAL_ADDR(port,false); */
	public static InetSocketAddress LOCAL_ADDR(int port) throws Throwable {
		return LOCAL_ADDR(port, false);
	}

	/**
	 * return LOCAL_ADDR(Integer.decode(str,allow_registered));
	 */
	public static InetSocketAddress LOCAL_ADDR(String str, boolean allow_registered) throws Throwable {
		return LOCAL_ADDR(Integer.decode(str), allow_registered);
	}

	/** return LOCAL_ADDR(Integer.decode(str,false)); */
	public static InetSocketAddress LOCAL_ADDR(String str) throws Throwable {
		return LOCAL_ADDR(Integer.decode(str), false);
	}

	// JAVA, S. 290: Reading Strings
	final private static BufferedReader brConsole = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * DIALOG: Ausgabe von formatter/args ueber System.out warte beliebig lange
	 * auf System.in auf Input --> speichere in String wenn IT.CLOSE(System.in);
	 * --> Return-Wert: null !!
	 * 
	 * @param s
	 *            String formatiert
	 * @return Input Benutzereingabe
	 */
	public static String Dialog(String s) {
		System.out.printf(s);
		try {
			return brConsole.readLine();
		} catch (Throwable T) {
			return null;
		}
	} // Ende von DIALOG

	/** ermittle Anzahl der Elemente in t fuer String */
	public static int LENGTH(String t) {
		return t == null ? 0 : t.length();
	}

	/** ermittle Anzahl der Elemente in t fuer byte[] */
	public static int LENGTH(byte[] t) {
		return t == null ? 0 : t.length;
	}

	/** ermittle Anzahl der Elemente in t fuer int[] */
	public static int LENGTH(int[] t) {
		return t == null ? 0 : t.length;
	}

	/** ermittle Anzahl der Elemente in t fuer double[] */
	public static int LENGTH(double[] t) {
		return t == null ? 0 : t.length;
	}

	/** ermittle Anzahl der Elemente fuer generic T[] */
	public static <T> int LENGTH(T[] t) {
		return t == null ? 0 : t.length;
	}

	/**
	 * <pre>
	 *  Serial-Ids wichtig fuer Serialisierung !!!
	 *  
	 *  Komperatoren werden nicht serialisiert, da 
	 *  diese static deklariert werden => transient
	 *  nicht erforderlich.
	 *  
	 *  BASE wird auch nicht serialisiert bzw. uebertragen.
	 *  
	 *  Serialiserung erfolgt nur fuer DataPoint,
	 *  d.h. inklusiv Member-Funktionen (transient hier nicht
	 *  moeglich - ctr) und Member-Variablen.
	 * </pre>
	 */
	final private static long ex_serial = 1234567890123456789L;
	final private static long string_cmp_serial = ex_serial + 1;
	final private static long string_cmp_ignore_serial = string_cmp_serial + 1;
	final public static long id_serial = string_cmp_ignore_serial + 1;
	final public static long name_serial = id_serial + 1;
	final public static long dataPoint_serial = name_serial + 1;

	/**
	 * <pre>
	 * Definition von Comparator fuer String mit compareTo
	 * </pre>
	 */
	final public static class StringCmp implements Comparator<String>, Serializable {
		/**
		 * <pre>
		 * UNBEDINGT notwendig fuer Serializable
		 * </pre>
		 */
		final private static long serialVersionUID = string_cmp_serial;

		/**
		 * <pre>
		 * vergleicht String's (null: erlaubt) mit compareTo
		 * </pre>
		 */
		public int compare(String s1, String s2) {
			if (s1 == s2)
				return 0; // vergleicht NUR Reference's
			if (s1 == null)
				return 1;
			if (s2 == null)
				return -1;

			// s1 UND s2 != null
			int l1 = s1.length(), l2 = s2.length();
			if (l1 != l2)
				return l1 - l2;
			return s1.compareTo(s2);
		} // Ende von compare
	} // Ende von StringCmp

	/**
	 * <pre>
	 * final public static StringCmp STRING_COMP = new StringCmp();
	 * </pre>
	 */
	final public static StringCmp STRING_COMP = new StringCmp();

	/**
	 * <pre>
	 * Definition von Comparator fuer String mit compareToIgnoreCase
	 * </pre>
	 */
	final public static class StringCmpIgnore implements Comparator<String>, Serializable {
		/**
		 * <pre>
		 * UNBEDINGT notwendig fuer Serializable
		 * </pre>
		 */
		final private static long serialVersionUID = string_cmp_ignore_serial;

		/**
		 * <pre>
		 * compare: vergleicht String's (null: erlaubt) mit compareToIgnoreCase
		 * </pre>
		 */
		public int compare(String s1, String s2) {
			if (s1 == s2)
				return 0; // vergleicht NUR Reference's
			if (s1 == null)
				return 1;
			if (s2 == null)
				return -1;

			// s1 UND s2 != null
			int l1 = s1.length(), l2 = s2.length();
			if (l1 != l2)
				return l1 - l2;
			return s1.compareToIgnoreCase(s2);
		} // Ende von compare
	} // Ende von StringCmpIgnore

	/**
	 * <pre>
	 * schliesse Objecte mit Closeable --> return null 
	 * NO Exception 
	 * &#64;param t
	 * &#64;return null
	 * </pre>
	 */
	public static <T extends Closeable> T CLOSE(T t) {
		if (t == null)
			return null;
		try {
			t.close();
		} catch (Throwable T) {
		}
		return null;
	} // Ende von CLOSE

	/**
	 * <pre>
	 * schliesse in tab ab offset number Elemente --> return tab
	 * NO Exception 
	 * 
	 * &#64;param tab
	 * &#64;param offset
	 * &#64;param number
	 * &#64;return tab
	 * </pre>
	 */
	public static <T extends Closeable> T[] CLOSE(T[] tab, int offset, int number) {
		if (tab == null)
			return tab;
		int length = tab.length;
		if (offset < 0 || offset >= length || number <= 0 || number > length - offset)
			// FEHLER
			return tab;
		for (int i = 0; i < number; ++i, ++offset) {
			// Optimierung
			if (tab[offset] == null)
				continue;
			try {
				tab[offset].close();
			} catch (Throwable T) {
			}
			tab[offset] = null;
		}
		return tab;
	} // Ende von CLOSE

	/** return CLOSE(tab,0,number); */
	public static <T extends Closeable> T[] CLOSE(T[] tab, int number) {
		return CLOSE(tab, 0, number);
	}

	/**
	 * <pre>
	 * schliesse in tab ALLE Elemente --> return null
	 * NO Exception 
	 * &#64;param tab
	 * &#64;return null
	 * </pre>
	 */
	public static <T extends Closeable> T[] CLOSE(T[] tab) {
		CLOSE(tab, 0, LENGTH(tab));
		return null;
	}

	/**
	 * <pre>
	 * schliesse Socket --> return null (NO Exception)
	 * &#64;param t (null: erlaubt)
	 * &#64;param wait (in sec) disables/enables immediate return from a close.
	 * 0: forceful close immediately
	 * >0: close will block pending the transmission and
	 *     acknowledgement of all data written to the peer,
	 *     Upon reaching the <wait> timeout, the socket
	 *     is closed forcefully, with a TCP RST.
	 * &#64;return null
	 * </pre>
	 */
	public static Socket CLOSE(Socket t, int wait) {
		if (t == null)
			return null;
		if (wait < 0)
			wait = 0;
		try {
			t.setSoLinger(wait > 0, wait);
		} catch (Throwable T) {
		}
		try {
			t.close();
		} catch (Throwable T) {
		}
		return null;
	} // Ende von CLOSE

	// ******************************************************************************
	/**
	 * <pre>
	 * schreibe String --> byte[]
	 * aus Performance: KEINE Parameter Pruefung --> Exceptions moeglich 
	 * &#64;param str
	 * &#64;param tab
	 * &#64;param offset
	 * &#64;return Anzahl der geschriebenen Byte's
	 * </pre>
	 */
	public static int WRITE(String str, byte[] tab, int offset) {
		int length = LENGTH(str);
		if (length != 0)
			// OPTIMIERUNG: --> warning: [deprecation]
			// getBytes
			str.getBytes(0, length, tab, offset);
		// LANGSAM:
		// System.arraycopy(str.getBytes(),0,tab,offset,length);
		return length;
	}

	/** return WRITE(str,t,0); */
	public static int WRITE(String str, byte[] t) {
		return WRITE(str, t, 0);
	}

	/**
	 * <pre>
	 * schreibe short --> byte[] 
	 * aus Performance: KEINE Parameter Pruefung --> Exceptions moeglich
	 * 
	 * WICHTIG: Daten werden in Big Endian (Network) Byte Order gespeichert
	 * 
	 * &#64;param z
	 * &#64;param tab
	 * &#64;param offset
	 * &#64;return Anzahl der geschriebenen Byte's
	 * </pre>
	 */
	public static int WRITE(short z, byte[] tab, int offset) {
		tab[offset + 1] = (byte) z;
		// >>> : unsigned right shift: Java, S. 72
		tab[offset] = (byte) (z >>> 8);
		return 2;
	}

	/** return WRITE(z,t,0); */
	public static int WRITE(short z, byte[] t) {
		return WRITE(z, t, 0);
	}

	// @formatter:off
	/**
	 * Converts int --> byte[]
	 * 
	 * !!!: Save data in Big Endian (Network) Byte Order
	 * 
	 * @param z Input in int format
	 * @param tab Output in byte array format
	 * @param offset Writes the content of z into the tab - starts on index offset
	 * @return Number of written bytes
	 */
	// @formatter:on
	public static int WRITE(int z, byte[] tab, int offset) {
		tab[offset + 3] = (byte) z;
		for (int i = 1; i < 4; ++i) {
			// >>> : unsigned right shift: Java, S. 72
			tab[offset + 3 - i] = (byte) (z >>> i * 8);
		}
		return 4;
	}

	/** return WRITE(z,t,0); */
	public static int WRITE(int z, byte[] t) {
		return WRITE(z, t, 0);
	}

	// @formatter:off
	/**
	 * Converts long --> byte[]
	 * 
	 * !!!: Save data in Big Endian (Network) Byte Order
	 * 
	 * @param z Input in long format
	 * @param tab Output in byte array format
	 * @param offset Writes the content of z into the tab - starts on index offset
	 * @return Number of written bytes
	 */
	// @formatter:on
	public static int WRITE(long z, byte[] tab, int offset) {
		tab[offset + 7] = (byte) z;
		for (int i = 1; i < 8; ++i) {
			// >>> : unsigned right shift: Java, S. 72
			tab[offset + 7 - i] = (byte) (z >>> i * 8);
		}
		return 8;
	}

	/** return WRITE(z,t,0); */
	public static int WRITE(long z, byte[] t) {
		return WRITE(z, t, 0);
	}

	// ******************************************************************************
	// @formatter:off
	/**
	 * 4 Byte's ab tab[offset] --> int
	 * aus Performance: KEINE Parameter Pruefung --> Exceptions moeglich 
	 * 
	 * HERLEITUNG:
	 * ==============		
	 * => in: 0000 0001 | 0000 0010 | 0000 0011 | 0000 0100
	 * => hex: 0x 01 02 03 04
	 * int number = 16909060;
	 * LOGGER.info(String
	 * 	.format("\tnumber(dez) '%d', number(bin) '%s'\n",
	 * 	number,Integer.toBinaryString(number)));
	 * 
	 * => Verteilung auf 4 Byte
	 * byte b_0 = (byte) 0x01; // MSB
	 * byte b_1 = (byte) 0x02;
	 * byte b_2 = (byte) 0x03;
	 * byte b_3 = (byte) 0x04; // LSB
	 * 
	 * LOGGER.info(String
	 * 	.format("\tDezimal: b_0 '%d', b_0 '%d', b_0 '%d', b_0 '%d'\n",
	 * 	b_0, b_1, b_2, b_3));
	 * 
	 * LOGGER.info(String
	 * 	.format("\tBinaer: b_0 '%s', b_0 '%s', b_0 '%s', b_0 '%s'\n",
	 * 	Integer.toBinaryString(b_0),
	 * 	Integer.toBinaryString(b_1),
	 * 	Integer.toBinaryString(b_2),
	 * 	Integer.toBinaryString(b_3)));
	 * 
	 * => Konvertiere zurueck nach int !!!
	 * => 1. Immer 24 Stellen nach links, da negative Byte-Werte beim
	 *    promoten zu einem int die hoeherwertigen Bit immmer mit 1
	 *    aufgefuellt werden, diese muessen entfernt werden.  
	 * => 2. Danach wird das entsprechende Byte auf die richtige Position
	 *    des int geschoben, d.h. das
	 *    - MSB auf Bit 25-32
	 *    -     auf Bit 17-24
	 *    -     auf Bit  9-16
	 *    - LSB auf Bit  1-8
	 * => 3. Danach wird += eine Binar-Addidtion durchgefuehrt
	 *    Beispiel fuer b_0 (MSB), b_1, b_2 und b_3 (LSB):
	 *    0000 0001 0000 0000 0000 0000 0000 0000 (b_0 MSB)
	 *    0000 0000 0000 0010 0000 0000 0000 0000 (b_1)
	 *    0000 0000 0000 0000 0000 0011 0000 0000 (b_2)
	 *    0000 0000 0000 0000 0000 0000 0000 0100 (b_3 LSB)
	 *    --------------------------------------- +
	 *    0000 0001 0000 0010 0000 0011 0000 0100 (16909060)
	 *    
	 * int number_c = 0;
	 * number_c = b_0 << 24;
	 * number_c += (b_1 << 24) >>> 8;
	 * number_c += (b_2 << 24) >>> 16;
	 * number_c += (b_3 << 24) >>> 24;
	 * 
	 * LOGGER.info(String
	 * 	.format("\tnumber_c(dez) '%d', number_c(bin) '%s'\n",
	 * 	number_c,
	 * 	Integer.toBinaryString(number_c)));
	 * 
	 * WICHTIG (Java, S. 50)
	 * ========================
	 * All byte/short/char values are promoted to int.
	 * Then, if one operand is a long, the whole expression is 
	 * promoted to long.
	 * A negative byte will be sign-extended when it is promoted to int.
	 * Thus, the high-order bits will be filled with 1's. 
	 * --> zuerst um 24 Stellen NACH links 
	 * --> unsigned right shift an korrekte Position
	 * <<:           left  shift: Java, S. 69
	 * >>>: unsigned right shift: Java, S. 72
	 * 
	 * @param tab The byte array
	 * @param offset Writes the content of tab into the return value - starts on tab index offset
	 * @param little_endian
	 *   true:  Little Endian           Byte Order
	 *   false: Big    Endian (Network) Byte Order (Java VM)
	 * @return int
	 * @see also see BigInteger(byte).intValue()
	 */
	// @formatter:on
	public static int BTOI(byte[] tab, int offset, boolean little_endian) {
		if (little_endian == false) {
			int z = tab[offset] << 24;
			for (int i = 1; i < 4; ++i) {
				z += (tab[offset + i] << 24) >>> i * 8;
			}
			return z;
		}
		int z = tab[offset + 3] << 24;
		for (int i = 1; i < 4; ++i) {
			z += (tab[offset + 3 - i] << 24) >>> i * 8;
		}
		return z;
	} // Ende von BTOI

	/** return BTOI(tab,offset,false); */
	public static int BTOI(byte[] tab, int offset) {
		return BTOI(tab, offset, false);
	}

	/** return BTOI(tab,0,little_endian); */
	public static int BTOI(byte[] tab, boolean little_endian) {
		return BTOI(tab, 0, little_endian);
	}

	/** return BTOI(tab,0,false); */
	public static int BTOI(byte[] tab) {
		return BTOI(tab, 0, false);
	}

	/**
	 * <pre>
	 * 2 Byte's ab tab[offset] --> short
	 * <p>
	 * aus Performance: KEINE Parameter Pruefung -> Exceptions moeglich
	 * 
	 * WICHTIG (Java, S. 50)
	 * ========================
	 * All byte values are promoted to int.
	 * 
	 * &#64;param tab
	 * &#64;param offset
	 * &#64;param little_endian
	 *   true:  Little Endian           Byte Order
	 *   false: Big    Endian (Network) Byte Order (Java VM)
	 * &#64;return short
	 * </pre>
	 */
	/*
	 * Langschrift: int i = tab[offset]; --> Bit 0 ... 7: von tab[offset] -->
	 * Bit 8 ... 31: IMMER Bit 7 von tab[offset] ! Java, S. 63 =========== A &
	 * NULL_S --> setzt Bit 0 - 7: Bit's von A Bit 8 - 15: IMMER 0
	 */
	final private static short NULL_S = (short) 0x00ff;

	public static short BTOS(byte[] tab, int offset, boolean little_endian) {
		if (little_endian == false) {
			short z0 = (short) (tab[offset] << 8), z1 = (short) (tab[offset + 1] & NULL_S);
			return (short) (z0 + z1);
		}
		short z0 = (short) (tab[offset + 1] << 8), z1 = (short) (tab[offset] & NULL_S);
		return (short) (z0 + z1);
	} // Ende von BTOS

	/** return BTOS(tab,offset,false); */
	public static short BTOS(byte[] tab, int offset) {
		return BTOS(tab, offset, false);
	}

	/** return BTOS(tab,0,little_endian); */
	public static short BTOS(byte[] tab, boolean little_endian) {
		return BTOS(tab, 0, little_endian);
	}

	/** return BTOS(tab,0,false); */
	public static short BTOS(byte[] tab) {
		return BTOS(tab, 0, false);
	}

	// @formatter:off
	/**
	 * <pre>
	 * 8 Byte's ab tab[offset] --> long
	 * <p>
	 * 
	 * @param tab
	 * @param offset
	 * @param little_endian
	 *   true:  Little Endian           Byte Order
	 *   false: Big    Endian (Network) Byte Order (Java VM)
	 * @return long
	 * </pre>
	 */
	// @formatter:on
	public static long BTOL(byte[] tab, int offset, boolean little_endian) {
		if (little_endian == false) {
			long z = (long) tab[offset] << 56;
			for (int i = 1; i < 8; ++i) {
				z += ((long) tab[offset + i] << 56) >>> i * 8;
			}
			return z;
		}
		long z = (long) tab[offset + 7] << 56;
		for (int i = 1; i < 8; ++i) {
			z += ((long) tab[offset + 7 - i] << 56) >>> i * 8;
		}
		return z;
	} // Ende von BTOL

	/** return BTOL(tab,offset,false); */
	public static long BTOL(byte[] tab, int offset) {
		return BTOL(tab, offset, false);
	}

	/** return BTOL(tab,0,little_endian); */
	public static long BTOL(byte[] tab, boolean little_endian) {
		return BTOL(tab, 0, little_endian);
	}

	/** return BTOL(tab,0,false); */
	public static long BTOL(byte[] tab) {
		return BTOL(tab, 0, false);
	}

	/**
	 * <pre>
	 * Converts an array of bytes into a hex string.
	 * 
	 * &#64;param buf byte array to convert.
	 * &#64;param format 
	 * &#64;return hex string.
	 * </pre>
	 */
	public static String convertByteArrayToHexString(byte buf[]) {
		/**
		 * Generiere String Buffer fuer Hex String, pro Byte 2 Zeichen.
		 */
		StringBuffer stringBuffer = new StringBuffer(buf.length * 2);

		/** Iteration ueber buf, Byte-weise. */
		for (int i = 0; i < buf.length; i++) {
			/** Falls Wert bit 4-7 0x0 ist. */
			if ((buf[i]) < 0x10) {
				stringBuffer.append("0");
			}

			/**
			 * Konvertiere 1 Byte zu Hex String mit Wrapper Class Integer.
			 */
			stringBuffer.append(Integer.toString(buf[i], 16));

			/**
			 * Fuege Leerzeichen ein, nicht aber hinter dem letzten Byte.
			 */
			// if (i != buf.length - 1) {
			// stringBuffer.append(" ");
			// }
		}
		/** Return Hex String. */
		return stringBuffer.toString();
	}// End of function convertByteArrayToHexString

	/**
	 * Turns one byte into a hex string
	 * 
	 * @param buf
	 *            byte to convert to a hex-coded string.
	 * @return Generated hex-coded string.
	 */
	public static String convertByteToHexString(byte buf) {
		StringBuffer strbuf = new StringBuffer(2);
		if (((int) buf & 0xff) < 0x10)
			return strbuf.append("0").toString();
		else
			return strbuf.append(Long.toString((int) buf & 0xff, 16)).toString();
	}

	/**
	 * Convert hex string to byte array, use HexBinaryAdapter from javax.xml.
	 * 
	 * @param hexString
	 *            Hex-coded string.
	 * @return Byte array (converted hex-coded string).
	 */
	public static byte[] convertHexStringToByteArray(String hexString) {
		HexBinaryAdapter adapter = new HexBinaryAdapter();
		byte[] bytes = adapter.unmarshal(hexString);
		return bytes;
	}
}