package workshop.util.model.datapoint;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import workshop.util.Tool;

/**
 * The Id class
 */
@XmlTransient
abstract public class Id extends Base implements Serializable {
	/* UNBEDINGT notwendig fuer Serialization */
	final private static long serialVersionUID = Tool.id_serial;

	/*
	 * <pre>
	 * symbolic Id of object
	 * </pre>
	 */
	private int Id;

	/**
	 * <pre>
	 * Id set/get functions
	 * </pre>
	 */
	public void setId(int Id) {
		this.Id = Id;
	}

	public int getId() {
		return Id;
	}
} // End of Id

