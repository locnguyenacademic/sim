/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.io.Serializable;
import java.util.List;

import net.ea.pso.Attribute.Type;

/**
 * This class represents list of attribute.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class AttributeList implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Built-in variable which is the list containing all attributes.
	 */
	protected List<Attribute> list = Util.newList(0);
	
	
	/**
	 * Default constructor.
	 */
	public AttributeList() {

	}


	/**
	 * Getting the size of attribute list.
	 * @return size of this list
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Getting the attribute at specified index.
	 * @param index Specified index.
	 * @return The attribute at specified index.
	 */
	public Attribute get(int index) {
		return list.get(index);
	}
	
	
	/**
	 * Adding the specified attribute into this attribute list.
	 * @param att Specified attribute added at the end of this attribute list.
	 * @return this list.
	 */
	public AttributeList add(Attribute att) {
		list.add(att);
		return this;
	}
	
	
	/**
	 * Looking up an attribute by the specified attribute name. The method returns the index of the attribute having such name.
	 * If no attribute is found, the method returns -1.
	 * @param attName Specified attribute name
	 * @return Index of the attribute having such name in this attribute list. If no attribute is found, the method returns -1.
	 */
	public int indexOf(String attName) {
		for (int i = 0; i < list.size(); i++) {
			Attribute att = list.get(i);
			if (att.getName().equals(attName)) return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Clearing this attribute list.
	 */
	public void clear() {
		list.clear();
	}


	/**
	 * Creating default attribute list with object variables.
	 * By default, all variables are real numbers.
	 * @param maxVarNumber maximum number of object variables.
	 * @param type variable type.
	 * @return default attribute list with object variables.
	 */
	public static AttributeList defaultVarAttributeList(int maxVarNumber, Type type) {
		AttributeList attList = new AttributeList();
		for (int i = 0; i < maxVarNumber; i++) {
			Attribute att = new Attribute("var" + i, type);
			attList.add(att);
		}
		
		return attList;
	}

	
	/**
	 * Creating default attribute list with real variables.
	 * By default, all variables are real numbers.
	 * @param maxVarNumber maximum number of real variables.
	 * @return default attribute list with real variables.
	 */
	public static AttributeList defaultRealVarAttributeList(int maxVarNumber) {
		AttributeList attList = new AttributeList();
		for (int i = 0; i < maxVarNumber; i++) {
			Attribute att = new Attribute("var" + i, Type.real);
			attList.add(att);
		}
		
		return attList;
	}


}
