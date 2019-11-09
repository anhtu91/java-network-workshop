package workshop.util.model.datapoint;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import workshop.util.Tool;

/**
 * The Name class
 */
@XmlTransient
abstract public class Name extends Id implements Serializable {
	/* Necessary for serialization */
	final private static long serialVersionUID = Tool.name_serial;

	/*
	 * Symbolic name of object
	 */
	private String name;

	/**
	 * Name set/get functions
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
} // End of Name
