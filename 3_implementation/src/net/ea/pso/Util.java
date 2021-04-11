/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is utility class to provide static utility methods.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Util {

	
	/**
	 * Decimal format.
	 */
	public static String DECIMAL_FORMAT = "%.12f";;
	
	
	/**
	 * Default date format.
	 */
	public static String  DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";

	
	/**
	 * Static code.
	 */
	static {
		try {
			DECIMAL_FORMAT = "%." + net.ea.pso.adapter.Util.DECIMAL_PRECISION + "f";
		} catch (Throwable e) {}

		try {
			DATE_FORMAT = net.ea.pso.adapter.Util.DATE_FORMAT;
		} catch (Throwable e) {}
	}
	
	
	/**
	 * Creating a new array.
	 * @param <T> element type.
	 * @param tClass element type.
	 * @param length array length.
	 * @return new array
	 */
	public static <T> T[] newArray(Class<T> tClass, int length) {
		try {
		    return net.ea.pso.adapter.Util.newArray(tClass, length);
		}
		catch (Throwable e) {}
		
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(tClass, length);
		return array;
	}

	
	/**
	 * Creating a new list with initial capacity.
	 * @param <T> type of elements in list.
	 * @param initialCapacity initial capacity of this list.
	 * @return new list with initial capacity.
	 */
	public static <T> List<T> newList(int initialCapacity) {
		try {
		    return net.ea.pso.adapter.Util.newList(initialCapacity);
		}
		catch (Throwable e) {}
		
	    return new ArrayList<T>(initialCapacity);
	}
	
	
	/**
	 * Creating a new map.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @param initialCapacity initial capacity of this list.
	 * @return new map.
	 */
	public static <K, V> Map<K, V> newMap(int initialCapacity) {
		try {
		    return net.ea.pso.adapter.Util.newMap(initialCapacity);
		}
		catch (Throwable e) {}
		
	    return new HashMap<K, V>(initialCapacity);
	}

	
	/**
	 * Converting the specified number into a string.
	 * @param number specified number.
	 * @return text format of number of the specified number.
	 */
	public static String format(double number) {
		try {
		    return net.ea.pso.adapter.Util.format(number);
		}
		catch (Throwable e) {}

		return String.format(DECIMAL_FORMAT, number);
	}

	
	/**
	 * Converting a specified array of objects (any type) into a string in which each object is converted as a word in such string.
	 * @param <T> type of each object in the specified array.
	 * @param array Specified array of objects.
	 * @param sep The character that is used to connect words in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return Text form (string) of the specified array of objects, in which each object is converted as a word in such text form.
	 */
	public static <T extends Object> String toText(T[] array, String sep) {
		try {
		    return net.ea.pso.adapter.Util.toText(array, sep);
		}
		catch (Throwable e) {}

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if ( i > 0) buffer.append(sep + " ");
			buffer.append(array[i]);
		}
		return buffer.toString();
	}

	
	/**
	 * Converting a specified collection of objects (any type) into a string in which each object is converted as a word in such string.
	 * @param <T> type of each object in the specified collection.
	 * @param list Specified collection of objects.
	 * @param sep The character that is used to connect words in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return Text form (string) of the specified collection of objects, in which each object is converted as a word in such text form.
	 */
	public static <T extends Object> String toText(Collection<T> list, String sep) {
		try {
		    return net.ea.pso.adapter.Util.toText(list, sep);
		}
		catch (Throwable e) {}

		StringBuffer buffer = new StringBuffer();
		int i = 0;
		for (T value : list) {
			if (i > 0) buffer.append(sep + " ");
			buffer.append(value);
			i++;
		}
		return buffer.toString();
	}

	
	/**
	 * Splitting a specified string into many words (tokens).
	 * @param source Specified string.
	 * @param sep The character (string) that is used for separation.
	 * @param remove the string which is removed from each tokens.
	 * @return list of words (tokens) from splitting the specified string.
	 */
	public static List<String> split(String source, String sep, String remove) {
		try {
		    return net.ea.pso.adapter.Util.split(source, sep, remove);
		}
		catch (Throwable e) {}

		String[] array = source.split(sep);
		List<String> result = Util.newList(0);
		for (String str : array) {
			if (str == null) continue;
			if (remove != null && remove.length() > 0) str = str.replaceAll(remove, "");
			str = str.trim();
			if (str.length() > 0) result.add(str);
		}
		return result;
	}

	
	/**
	 * Splitting and parsing a specified string into many objects. The character (string) that is used for separation is specified by the parameter {@code sep} which follows Java regular expression.
	 * @param <T> type of returned objects.
	 * @param string specified string.
	 * @param type class of returned objects, which plays the role of template for parsing.
	 * @param sep character (string) that is used for separation.
	 * @return list of objects parsed from specified string.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> List<T> parseListByClass(String string, Class<T> type, String sep) {
		try {
		    return net.ea.pso.adapter.Util.parseListByClass(string, type, sep);
		}
		catch (Throwable e) {}

		string = string.trim();
		List<String> array = split(string, sep, null);
		List<T> list = Util.newList(0);
		for (String el : array) {
			T v = (T) parseObjectByClass(el, type);
			if (v != null) list.add(v);
		}
		return list;
	}

	
	/**
	 * Parsing (converting) a specified string into an object according to a class.
	 * @param string specified string.
	 * @param type class of the parsed object.
	 * @return parsed object.
	 */
	public static Object parseObjectByClass(String string, Class<?> type) {
		try {
		    return net.ea.pso.adapter.Util.parseObjectByClass(string, type);
		}
		catch (Throwable e) {}

		Object v = null;
		try {
			if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))
				v = Boolean.parseBoolean(string); 
			else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type))
				v = Byte.parseByte(string); 
			else if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type))
				v = Short.parseShort(string); 
			else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type))
				v = Integer.parseInt(string); 
			else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type))
				v = Long.parseLong(string); 
			else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type))
				v = Float.parseFloat(string);
			else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type))
				v = Double.parseDouble(string);
			else if (Character.class.isAssignableFrom(type) || char.class.isAssignableFrom(type))
				v = string.charAt(0); 
			else if (String.class.isAssignableFrom(type))
				v = string; 
			else if (Date.class.isAssignableFrom(type))
				v = new SimpleDateFormat(DATE_FORMAT).parse(string); 
			else if (File.class.isAssignableFrom(type))
				v = new File(string);
			else
				v = null;
		}
		catch (Throwable e) {
			v = null;
			Util.trace(e);
		} 
		return v;
	}

	
	/**
	 * Tracing error.
	 * @param e throwable error.
	 */
	public static void trace(Throwable e) {
		try {
			net.ea.pso.adapter.Util.trace(e);
		}
		catch (Throwable ex) {
			e.printStackTrace();
		}
	}
	
	
}
