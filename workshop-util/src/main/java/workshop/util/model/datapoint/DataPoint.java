package workshop.util.model.datapoint;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import workshop.util.Tool;

/**
 * The DataPoint class
 */
@XmlRootElement(name = "datapoint")
@XmlType(propOrder = { "id", "name", "description", "timestamp", "value" })
public class DataPoint<T> extends Name {
	/*
	 * log4j set logging for actual class, use config of RootLogger
	 */
	private static final Logger LOGGER = LogManager
			.getFormatterLogger(DataPoint.class);

	/* Necessary for serialization */
	final private static long serialVersionUID = Tool.dataPoint_serial;

	/* The description of the datapoint */
	private String description;

	/* The value */
	private T value;

	/* The time stamp */
	private String timestamp;

	/**
	 * Description set/get functions
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Value set/get functions
	 */
	public T getValue() {
		return value;
	}

	public void setValue(T t) {
		this.value = t;
	}

	/**
	 * Time stamp set/get functions
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestamp() {
		return timestamp;
	}

	/** Default constructor not allowed !!! */
	private DataPoint() {
		/*
		 * Einfacher Konstruktor wird verboten, Benutzer muss sinvollen
		 * DataPoint aufbauen
		 */
	}

	/**
	 * Allows constructor with standard arguments !!!
	 * 
	 * @param id
	 *            The unique id
	 * @param description
	 *            The symbolic name of the datapoint
	 * @param t
	 *            The generic value of the datapoint
	 * @param timestamp
	 *            The time stamp when the value was set
	 * @exception Non
	 */
	public DataPoint(int id, String name, String description, T t,
			String timestamp) {
		/* Set id */
		setId(id < 0 ? id = 0 : id);

		/* Set name */
		this.setName(name == null ? (String) null : name);

		/* Set description */
		this.description = description == null ? (String) null : description;

		/* Set value */
		this.value = t;

		/* Set timestamp */
		this.timestamp = timestamp;
	}

	/**
	 * Allows constructor with standard arguments !!!
	 * 
	 * @param id
	 *            The unique id
	 * @param description
	 *            The symbolic name of the datapoint
	 * @param t
	 *            The generic value of the datapoint
	 * @exception Non
	 */
	public DataPoint(int id, String name, String description, T t) {
		/* Set id */
		setId(id < 0 ? id = 0 : id);

		/* Set name */
		this.setName(name == null ? (String) null : name);

		/* Set description */
		this.description = description == null ? (String) null : description;

		/* Set value */
		this.value = t;
	}

	/**
	 * Allows the copy constructor with standard arguments !!!
	 * 
	 * @param dp
	 *            The datapoint
	 * @exception Non
	 */
	public DataPoint(DataPoint<T> dp) {
		/* If null throw an exception */
		if (dp == null) {
			throw new IllegalArgumentException(String.format(
					"Class %s => First param == null (not supported in Copy Constructor)",
					this.getClass().getName()));
		} else {
			/* Set id */
			setId(dp.getId());

			/* Set name */
			this.setName(dp.getName());

			/* Set description */
			this.description = dp.getDescription();

			/* Set value */
			this.value = dp.getValue();
		}
	}

	/**
	 * This is an example to show how to use a builder (pattern) to create an
	 * datapoint
	 * 
	 * @param datapointBuilder
	 *            The builder object
	 * @exception Non
	 */
	public DataPoint(DatapointBuilder datapointBuilder) {
		/* Set id */
		setId(datapointBuilder.getId());

		/* Set name */
		this.setName(datapointBuilder.getName());

		/* Set description */
		this.description = datapointBuilder.getDescription();

		/* Set value */
		this.value = (T) datapointBuilder.getValue();

		/* Set timestamp */
		this.timestamp = datapointBuilder.getTimestamp();
	}

	/** Overriding equals */
	public boolean equals(Object o) {
		LOGGER.info(String.format("Function equals !!!"));

		/* Cast object => ClassCastException moeglich */
		DataPoint tDataPoint = (DataPoint) o;

		/* Controls only the reference */
		if (this == tDataPoint) {
			LOGGER.info(String
					.format("Function equals true => identische Referenzen."));
			return true;
		}

		/* Controls id, name, description, value */
		if (this.getId() == tDataPoint.getId()
				&& Tool.STRING_COMP.compare(getName(),
						tDataPoint.getName()) == 0
				&& Tool.STRING_COMP.compare(getDescription(),
						tDataPoint.getDescription()) == 0
				&& value == tDataPoint.getValue())
			return true;
		else
			return false;
	}

	/** Overriding the function close */
	public void close() {
		LOGGER.info(String.format("Close datapoint Id '%d', Description '%s'\n",
				getId(), getDescription()));
	}

	/** Overriding the function toString */
	public String toString() {
		if (value.getClass().isArray() == true)
			/* toString with type Array */
			return String.format(
					"id '%d', name '%s', description '%s', value '%s', timestamp '%s'",
					getId(), getName(), getDescription(),
					Arrays.toString((Object[]) value), getTimestamp());
		else
			/* toString with primitive type inc. tpye String */
			return String.format(
					"id '%d', name '%s', description '%s', value '%s', timestamp '%s'",
					getId(), getName(), getDescription(), value,
					getTimestamp());
	}
	
	/*************************************************************************************/
	/**
	 * This is a datapoint builder to create a datapoint (builder pattern).
	 * 
	 * @author MK
	 * @version 2017-07-13
	 */
	/**************************************************************************************/
	public static class DatapointBuilder<T> {
		/* The unique id */
		private int id = 0;

		/* The symbolic name */
		private String name = "";

		/* The description of the datapoint */
		private String description = "";

		/* The value */
		private T value;

		/* The time stamp */
		private String timestamp = "";

		/**
		 * <pre>
		 * Id set/get functions
		 * </pre>
		 */
		public DatapointBuilder<T> setId(int id) {
			this.id = id;
			return this;
		}

		public int getId() {
			return id;
		}

		/**
		 * <pre>
		 * Name set/get functions
		 * </pre>
		 */
		public DatapointBuilder<T> setName(String name) {
			this.name = name;
			return this;
		}

		public String getName() {
			return name;
		}

		/**
		 * <pre>
		 * Description set/get functions
		 * </pre>
		 */
		public DatapointBuilder<T> setDescription(String description) {
			this.description = description;
			return this;
		}

		public String getDescription() {
			return description;
		}

		/**
		 * <pre>
		 * Value set/get functions
		 * </pre>
		 */
		public DatapointBuilder<T> setValue(T value) {
			this.value = value;
			return this;
		}

		public T getValue() {
			return value;
		}

		/**
		 * <pre>
		 * Timestamp set/get functions
		 * </pre>
		 */
		public DatapointBuilder<T> setTimestamp(String timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public String getTimestamp() {
			return timestamp;
		}

		/**
		 * <pre>
		 * Build a new Datapoint
		 * </pre>
		 */
		public DataPoint<T> build() {
			return new DataPoint<T>(this);
		}
	}
}// End of class DataPoint