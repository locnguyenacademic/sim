/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.ea.ann.Attribute.Type;

/**
 * This class represents profile.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Profile implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * This built-in variable is a list of attribute. Note that this variable is a reference, which implies that many profiled can share the same attribute list.
	 */
	protected AttributeList attRef = new AttributeList();
	
	
	/**
	 * This built-in variable is a list of values.
	 */
	protected List<Object> attValues = Util.newList(0);
	
	
	/**
	 * Default constructor
	 */
	public Profile() {
		init();
	}
	
	
	/**
	 * Constructor with specified attribute list.
	 * @param attRef specified attribute list as {@link AttributeList}.
	 */
	public Profile(AttributeList attRef) {
		this.attRef = attRef;
		init();
	}
	
	
	/**
	 * Initializing empty profile in which all its values are null.
	 */
	private void init() {
		attValues.clear();
		for (int i = 0; i < attRef.size(); i++) {
			attValues.add(null);
		}
	}


	/**
	 * Getting the attribute at specified index.
	 * @param index attribute at specified index.
	 * @return attribute at specified index.
	 */
	public Attribute getAtt(int index) {
		return attRef.get(index);
	}


	/**
	 * Testing the value at specified index is missing.
	 * @param index specified index.
	 * @return whether missing value at this index.
	 */
	public boolean isMissing(int index) {
		return attValues.get(index) == null;
	}


	/**
	 * Getting the number of attributes.
	 * @return count for {@link AttributeList}.
	 */
	public int getAttCount() {
		return attRef.size();
	}
	
	
	/**
	 * Replacing the current attribute list by reference to specified attribute list.
	 * @param attRef reference to specified attribute list.
	 */
	public void setAttRef(AttributeList attRef) {
		this.attRef = attRef;
		init();
	}
	
	
	/**
	 * Getting the internal attribute list.
	 * @return reference of attribute list.
	 */
	public AttributeList getAttRef() {
		return attRef;
	}


	/**
	 * Finding index of the attribute having specified name.
	 * @param attName specified name.
	 * @return index of the attribute having specified name.
	 */
	public int indexOf(String attName) {
		return attRef.indexOf(attName);
	}


	/**
	 * Creating the value for specified attribute and the initial value.
	 * @param att specified attribute.
	 * @param value initial value.
	 * @return new value for specified attribute and the initial value.
	 * @throws Exception if any error raises.
	 */
	public static Object createValue(Attribute att, Object value) throws Exception {
		if (value == null) return null;
		
		Object newValue = null;
		Type attType = att.getType();
		
		switch (attType) {
		
		case bit:
			byte number = 0;
			if (value instanceof Number)
				number = ((Number)value).byteValue();
			else if (value instanceof String) {
				String string = ((String)value).trim();
				if (string.isEmpty())
					number = 0;
				else
					number = Byte.parseByte(string);
			}
			else if (value instanceof Boolean) {
				if ((Boolean)value)
					number = 1;
				else
					number = 0;
			}
			
			if (number == 0)
				newValue = (byte)0;
			else
				newValue = (byte)1; 
			
			break;
		case integer:
			if (value instanceof Integer)
				newValue = (Integer)value;
			else if (value instanceof Number)
				newValue = ((Number)value).intValue();
			else if (value instanceof String) {
				String string = ((String)value).trim();
				if (!string.isEmpty()) newValue = Integer.parseInt(string);
			}
			
			break;
		case real:
			if (value instanceof Double)
				newValue = (Double)value;
			else if (value instanceof Number)
				newValue = ((Number)value).doubleValue();
			else if (value instanceof String) {
				String string = ((String)value).trim();
				if (!string.isEmpty()) newValue = Double.parseDouble(string);
			}
			
			break;
		case string:
			if (value instanceof String)
				newValue = (String)value;
			else
				newValue = value.toString();
			
			break;
		case date:
			if (value instanceof Date)
				newValue = value;
			else if (value instanceof java.sql.Date) 
				newValue = new Date(((java.sql.Date)value).getTime());
			else if (value instanceof Calendar)
				newValue = ((Calendar)value).getTime();
			else if (value instanceof String) {
				String string = ((String)value).trim();
				if (!string.isEmpty()) {
					try {
						SimpleDateFormat df = new SimpleDateFormat(Util.DATE_FORMAT);
						newValue = df.parse(string);
					} 
					catch (Throwable e) {Util.trace(e); newValue = null;}
				}
			}
			
			break;
		case time:
			if (value instanceof Number)
				newValue = ((Number)value).longValue();
			if (value instanceof Date)
				newValue = ((Date)value).getTime();
			else if (value instanceof java.sql.Date) 
				newValue = ((java.sql.Date)value).getTime();
			else if (value instanceof Calendar)
				newValue = ((Calendar)value).getTimeInMillis();
			else if (value instanceof String) {
				String string = ((String)value).trim();
				if (!string.isEmpty()) {
					try {newValue = Long.parseLong(string);}
					catch (Exception e) {newValue = null;}
					
					if (newValue == null) {
						Date date = null;
						try {
							SimpleDateFormat df = new SimpleDateFormat(Util.DATE_FORMAT);
							date = df.parse(string);
						}catch (Throwable e) {date = null;}
						if (date != null) newValue = date.getTime();
					}
				}
			}
			
			break;
		case object:
			newValue = value;
			break;
		}
		
		return newValue;
	}

	
	/**
	 * Creating the value for specified attribute and the initial value.
	 * If the value is date time object, it is formatted according to the specified date format.
	 * @param att specified attribute.
	 * @param value specified attribute.
	 * @param df specified date format.
	 * @return new value for specified attribute and the initial value.
	 * @throws Exception if any error raises.
	 */
	public static Object createValue(Attribute att, Object value, DateFormat df) throws Exception {
		if (value == null) return null;
		
		Object newValue = null;
		
		if (value instanceof Date)
			newValue = value;
		else if (value instanceof java.sql.Date) 
			newValue = new Date(((java.sql.Date)value).getTime());
		else if (value instanceof Calendar)
			newValue = ((Calendar)value).getTime();
		else if (df == null)
			newValue = createValue(att, value);
		else {
			String string = value.toString();
			if (string.isEmpty())
				newValue = createValue(att, value);
			else {
				try {
					newValue = df.parse(string);
				} 
				catch (ParseException e) {
					newValue = null;
					Util.trace(e);
				}
			}
		}
		
		return newValue;
	}
	
	
	/**
	 * Setting the value of profile at specified index by specified value.
	 * @param index specified index.
	 * @param value specified value.
	 */
	public void setValue(int index, Object value) {
		Attribute att = attRef.get(index);
		Object newValue = null;
		try {
			newValue = createValue(att, value);
		} 
		catch (Throwable e) {
			newValue = null;
			Util.trace(e);
		}
		
		attValues.set(index, newValue);
	}
	

	/**
	 * Setting the value of profile at specified index by specified value.
	 * If the value is date time object, the object is formatted according to the specified date format.
	 * @param index specified index.
	 * @param value specified value.
	 * @param df specified date format.
	 */
	public void setValue(int index, Object value, DateFormat df) {
		Attribute att = attRef.get(index);
		Object newValue = null;
		try {
			newValue = createValue(att, value, df);
		} 
		catch (Throwable e) {
			newValue = null;
			Util.trace(e);
		}
		
		attValues.set(index, newValue);
	}
	
	
	/**
	 * Setting the value of the attribute having specified name by specified value.
	 * @param attName specified name.
	 * @param value specified value.
	 */
	public void setValue(String attName, Object value) {
		int index = indexOf(attName);
		if (index != -1) setValue(index, value);
	}
	
	
	/**
	 * Setting missing value at specified index.
	 * @param index specified index.
	 */
	public void setMissing(int index) {
		attValues.set(index, null);
	}
	
	
	/**
	 * Getting the value at specified index.
	 * @param index specified index.
	 * @return value by index.
	 */
	public Object getValue(int index) {
		return attValues.get(index);
	}
	
	
	/**
	 * Getting the value of attribute having the specified name.
	 * @param attName specified attribute name.
	 * @return value by attribute name.
	 */
	public Object getValue(String attName) {
		int index = indexOf(attName);
		if (index != -1)
			return getValue(index);
		else
			return null;
	}


	/**
	 * This static method converts the specified value of specified attribute as real number.
	 * @param att specified attribute.
	 * @param value specified value.
	 * @return value as real number.
	 */
	public static double getValueAsReal(Attribute att, Object value) {
		if (value == null) return Double.NaN;
			
		Type attType = att.getType();
		switch (attType) {
		
		case bit:
			return ((Byte)value).doubleValue();
		case integer:
			return ((Integer)value).doubleValue();
		case real:
			return ((Double)value).doubleValue();
		case string:
			try {
				return Double.parseDouble((String)value);
			}
			catch (Throwable e) {
				Util.trace(e);
				return Double.NaN;
			}
		case date:
			return ((Date)value).getTime();
		case time:
			return ((Long)value).doubleValue();
		case object:
			return toDouble(value);
		}
		
		return Double.NaN;
	}

	
	/**
	 * Converting specified object into real number.
	 * @param object specified object.
	 * @return real number.
	 */
	public static double toDouble(Object object) {
		if (object == null)
			return Double.NaN;
		else if (object instanceof Double)
			return (double)object;
		else if (object instanceof Number)
			return ((Number)object).doubleValue();
		else if (object instanceof Boolean)
			return ((boolean)object) ? 1.0 : 0.0;
		else if (object instanceof Character)
			return Character.getNumericValue(((Character)object));
		else if (object instanceof Date)
			return ((Date)object).getTime();
		else {
			try {
				return Double.parseDouble(object.toString());
			}
			catch (Exception e) {}
			return Double.NaN;
		}
	}

	
	/**
	 * Getting value at specified index as string.
	 * @param index specified index.
	 * @return value as string by index
	 */
	public String getValueAsString(int index) {
		Object value = getValue(index);
		if (value == null)
			return null;
		else if (value instanceof String)
			return (String)value;
		else
			return value.toString();
	}
	
	
	/**
	 * Getting value of attribute having specified name as string.
	 * @param attName specified attribute name.
	 * @return value of attribute having specified name as string.
	 */
	public String getValueAsString(String attName) {
		int index = indexOf(attName);
		if (index != -1)
			return getValueAsString(index);
		else
			return null;
	}


	/**
	 * Getting value at specified index as real number.
	 * @param index specified index.
	 * @return value as real number by index.
	 */
	public double getValueAsReal(int index) {
		if (isMissing(index)) return Double.NaN;
		Attribute att = attRef.get(index);
		Object value = attValues.get(index);
		return getValueAsReal(att, value);
	}
	
	
	/**
	 * Getting value of attribute having specified name as real number.
	 * @param attName specified attribute name.
	 * @return value as real number by attribute name.
	 */
	public double getValueAsReal(String attName) {
		int index = indexOf(attName);
		if (index != -1)
			return getValueAsReal(index);
		else
			return Double.NaN;
	}


}
