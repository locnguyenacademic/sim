/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

import java.io.Serializable;

/**
 * This class represents attribute of profile.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Attribute implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * This enum represents data type of this attribute.
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public static enum Type {
		
		/**
		 * Binary.
		 */
		bit,
		
		/**
		 * Integer number.
		 */
		integer,
		
		/**
		 * Real number.
		 */
		real,
		
		/**
		 * Text.
		 */
		string,
		
		/**
		 * Date.
		 */
		date,
		
		/**
		 * Time.
		 */
		time,
		
		/**
		 * Any type.
		 */
		object
	};

	
	/**
	 * Name of attribute.
	 */
	protected String name = "";
	
	
	/**
	 * Internal data type of this attribute.
	 */
	protected Type type = Type.object;

	
	/**
	 * Default constructor.
	 */
	public Attribute() {

	}
	
	
	/**
	 * Constructor with specified attribute.
	 * @param att Specified attribute
	 */
	public Attribute(Attribute att) {
		this();
		this.name = att.name;
		this.type = att.type;
	}
	
	
	/**
	 * Constructor with specified name and specified type.
	 * @param name Specified name.
	 * @param type Specified type.
	 */
	public Attribute(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	
	/**
	 * Getting the type of this attribute.
	 * @return The type of this attribute represented by {@link Type} enum.
	 */
	public Type getType() {
		return type;
	}

	
	/**
	 * Getting the name of this attribute.
	 * @return Name of this attribute.
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Setting the name of this attribute.
	 * @param name specified name.
	 */
	public void setName(String name) {
		this.name = name;
	}


}
