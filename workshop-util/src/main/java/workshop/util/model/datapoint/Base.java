package workshop.util.model.datapoint;

import java.io.Closeable;

import javax.xml.bind.annotation.XmlTransient;

/**
 * <pre>
 * BASE: 
 * Exception bei equals/COPY Constructor
 * finalize() --> CLOSE(this)
 * </pre>
 */
@XmlTransient
abstract public class Base implements Closeable {
	/**
	 * <pre>
	 * MUST implemented
	 * </pre>
	 */
	abstract public void close();

	/**
	 * COPY Constructor not allowed User can overwrite
	 * 
	 * @exception UnsupportedOperationException
	 */
	public void Base(Base o) {
		throw new UnsupportedOperationException(String.format(
				"class %s extends BASE: VERBOT von Copy " + "Constructor",
				this.getClass().getName()));
	}

	/**
	 * Equals not allowed
	 *
	 * @exception UnsupportedOperationException
	 */
	public boolean equals(Object o) {
		throw new UnsupportedOperationException(
				String.format("\nclass %s extends BASE: VERBOT von equals",
						this.getClass().getName()));
	}

	/**
	 * <pre>
	 * Example of finalize
	 * CLOSE(this)
	 * </pre>
	 */
	protected void finalize() {
		try {
			/** Call only the function close if this object is not null */
			if (this != null)
				/** Calls the user implemented function close */
				this.close();
		} catch (Throwable T) {
			/** Never calls an exception */
		}
	}// End of function finalize
} // End of class Base
