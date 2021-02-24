/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * This class represents normal configuration of particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PSOConfig implements Serializable, Cloneable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Built-in properties map.
	 */
	protected Map<String, Serializable> properties = Util.newMap(0);

	
	/**
	 * Default constructor.
	 */
	public PSOConfig() {

	}

	
	/**
	 * Clearing all entries.
	 */
	public void clear() {
		properties.clear();
	}
	
	
	/**
	 * Getting the size of configuration.
	 * @return Size of this configuration
	 */
	public int size() {
		return properties.size();
	}
	
	
	/**
	 * Extracting set of keys over all entries.
	 * @return Set of keys
	 */
	public Set<String> keySet() {
		return properties.keySet();
	}
	
	
	/**
	 * Testing whether the configuration contains the specific key.
	 * @param key The specific key
	 * @return Whether contains this key
	 */
	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}
	
	
	/**
	 * Putting the pair of key and value to the configuration.
	 * @param key Specific key
	 * @param value Associated value with the key. The value must be serializable.
	 * @return the previous value.
	 */
	public Serializable put(String key, Serializable value) {
		return properties.put(key, value); 
	}
	
	
	/**
	 * Putting entire other configuration into this list.
	 * @param config Other configuration
	 */
	public void putAll(PSOConfig config) {
		Set<String> keys = config.keySet();
		for (String key : keys) {
			put(key, config.get(key));
		}
	}


	/**
	 * Getting value associated with specific key.
	 * @param key Specific key
	 * @return Value by key
	 */
	public Serializable get(String key) {
		return properties.get(key);
	}
	
	
	/**
	 * Removing entry identified by the specific key.
	 * @param key Specific key.
	 * @return removed value.
	 */
	public Serializable remove(String key) {
		if (!properties.containsKey(key)) return null;

		return properties.remove(key);
	}
	
	
	/**
	 * Getting value associated with the specified key as a string.
	 * @param key Specified key
	 * @return Value as string
	 */
	public String getAsString(String key) {
		if (!containsKey(key)) return null;

		Serializable value = properties.get(key);
		if (value instanceof String)
			return (String)value;
		else
			return value.toString();
	}
	
	
	/**
	 * Getting value associated with the specified key as a real number.
	 * @param key Specified key
	 * @return Value as real number
	 */
	public double getAsReal(String key) {
		if (!containsKey(key))
			return Double.NaN;
		
		if (isRealValue(key) || isLongValue(key) || isIntValue(key))
			return ((Number) get(key)).doubleValue();
		else
			return Double.parseDouble(get(key).toString());
	}

	
	/**
	 * Getting value associated with the specified key as a long number. In Hudup framework, long number is also time in mili-seconds.
	 * @param key Specified key
	 * @return Value as a long number.
	 */
	public long getAsLong(String key) {
		if (!containsKey(key))
			return -1;
		
		if (isIntValue(key) || isLongValue(key) || isRealValue(key))
			return ((Number) get(key)).longValue();
		else
			return Long.parseLong(get(key).toString());
	}

	
	/**
	 * Getting value associated with the specified key as an integer.
	 * @param key Specified key
	 * @return Value as integer
	 */
	public int getAsInt(String key) {
		if (!containsKey(key))
			return -1;
		
		if (isIntValue(key) || isLongValue(key) || isRealValue(key))
			return ((Number) get(key)).intValue();
		else
			return Integer.parseInt(get(key).toString());
	}

	
	/**
	 * Getting value associated with the specified key as a boolean value.
	 * @param key Specified key
	 * @return Value as boolean
	 */
	public boolean getAsBoolean(String key) {
		if (!containsKey(key))
			return false;
		
		if (isBooleanValue(key))
			return (Boolean) get(key);
		else
			return Boolean.parseBoolean(get(key).toString());
	}
	
	
	/**
	 * Testing whether value associated with the specified key is real number.
	 * @param key Specified key
	 * @return whether is real value
	 */
	public boolean isRealValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof Double) || (value instanceof Float);
	}

	
	/**
	 * Testing whether value associated with the specified key is long number. In Hudup framework, long number is also time in mili-seconds.
	 * @param key Specified key
	 * @return whether is long value.
	 */
	public boolean isLongValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return value instanceof Long;
	}

	
	/**
	 * Testing whether value associated with the specified key is integer.
	 * @param key Specified key
	 * @return whether is integer value
	 */
	public boolean isIntValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return  (value instanceof Integer) || 
				(value instanceof Short) || 
				(value instanceof Byte);
	}

	
	/**
	 * Testing whether value associated with the specified key is boolean value.
	 * @param key Specified key
	 * @return whether is boolean value
	 */
	public boolean isBooleanValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof Boolean);
	}

	
}
