/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ea.pso.PSOConfig;
import net.hudup.core.Constants;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.parser.TextParserUtil;

/**
 * This is utility class to provide static utility methods. It is also adapter to other libraries.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Util {
	
	
	/**
	 * The maximum number digits in decimal precision.
	 */
	public static int DECIMAL_PRECISION = 12;

	
	/**
	 * Default date format.
	 */
	public static String DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";

	
	/**
	 * Static code.
	 */
	static {
		try {
			DECIMAL_PRECISION = Constants.DECIMAL_PRECISION;
		}
		catch (Throwable e) {}
		
		try {
			DATE_FORMAT = Constants.DATE_FORMAT;
		}
		catch (Throwable e) {}
	}

	
	/**
	 * Creating a new array.
	 * @param <T> element type.
	 * @param tClass element type.
	 * @param length array length.
	 * @return new array
	 */
	public static <T> T[] newArray(Class<T> tClass, int length) {
		return net.hudup.core.Util.newArray(tClass, length);
	}

	
	/**
	 * Creating a new list with initial capacity.
	 * @param <T> type of elements in list.
	 * @param initialCapacity initial capacity of this list.
	 * @return new list with initial capacity.
	 */
	public static <T> List<T> newList(int initialCapacity) {
	    return net.hudup.core.Util.newList(initialCapacity);
	}
	
	
	/**
	 * Creating a new map.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @param initialCapacity initial capacity of this list.
	 * @return new map.
	 */
	public static <K, V> Map<K, V> newMap(int initialCapacity) {
	    return net.hudup.core.Util.newMap(initialCapacity);
	}

	
	/**
	 * Converting the specified number into a string. The number of decimal digits is specified by {@link Constants#DECIMAL_PRECISION}.
	 * @param number specified number.
	 * @return text format of number of the specified number.
	 */
	public static String format(double number) {
		return MathUtil.format(number);
	}

	
	/**
	 * Converting a specified array of objects (any type) into a string in which each object is converted as a word in such string.
	 * @param <T> type of each object in the specified array.
	 * @param array Specified array of objects.
	 * @param sep The character that is used to connect words in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return Text form (string) of the specified array of objects, in which each object is converted as a word in such text form.
	 */
	public static <T extends Object> String toText(T[] array, String sep) {
		return TextParserUtil.toText(array, sep);
	}

	
	/**
	 * Converting a specified collection of objects (any type) into a string in which each object is converted as a word in such string.
	 * @param <T> type of each object in the specified collection.
	 * @param list Specified collection of objects.
	 * @param sep The character that is used to connect words in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return Text form (string) of the specified collection of objects, in which each object is converted as a word in such text form.
	 */
	public static <T extends Object> String toText(Collection<T> list, String sep) {
		return TextParserUtil.toText(list, sep);
	}

	
	/**
	 * Splitting a specified string into many words (tokens).
	 * @param source Specified string.
	 * @param sep The character (string) that is used for separation.
	 * @param remove the string which is removed from each tokens.
	 * @return list of words (tokens) from splitting the specified string.
	 */
	public static List<String> split(String source, String sep, String remove) {
		return TextParserUtil.split(source, sep, remove);
	}

	
	/**
	 * Splitting and parsing a specified string into many objects. The character (string) that is used for separation is specified by the parameter {@code sep} which follows Java regular expression.
	 * @param <T> type of returned objects.
	 * @param string specified string.
	 * @param type class of returned objects, which plays the role of template for parsing.
	 * @param sep character (string) that is used for separation.
	 * @return list of objects parsed from specified string.
	 */
	public static <T extends Object> List<T> parseListByClass(String string, Class<T> type, String sep) {
		return TextParserUtil.parseListByClass(string, type, sep);
	}

	
	/**
	 * Parsing (converting) a specified string into an object according to a class.
	 * @param string specified string.
	 * @param type class of the parsed object.
	 * @return parsed object.
	 */
	public static Object parseObjectByClass(String string, Class<?> type) {
		return TextParserUtil.parseObjectByClass(string, type);
	}

	
	/**
	 * Tracing error.
	 * @param e throwable error.
	 */
	public static void trace(Throwable e) {
		net.hudup.core.logistic.LogUtil.trace(e);
	}


	/**
	 * Converting Hudup profile into PSO profile.
	 * @param newAttRef PSO attributes.
	 * @param profile Hudup profile.
	 * @return PSO profile.
	 */
	public static net.ea.pso.Profile toPSOProfile(net.ea.pso.AttributeList newAttRef, Profile profile) {
		if (newAttRef == null || profile == null) return null;
		
		net.ea.pso.Profile newProfile = new net.ea.pso.Profile(newAttRef);
		int n = Math.min(newProfile.getAttCount(), profile.getAttCount());
		for (int i = 0; i < n; i++) {
			newProfile.setValue(i, profile.getValue(i));
		}
		
		return newProfile;
	}
	
	
	/**
	 * Converting Hudup profile into PSO profile.
	 * @param profile Hudup profile.
	 * @return PSO profile.
	 */
	public static net.ea.pso.Profile toPSOProfile(Profile profile) {
		net.ea.pso.AttributeList newAttRef = extractPSOAttributes(profile);
		return toPSOProfile(newAttRef, profile);
	}

		
	/**
	 * Extracting PSO attributes from Hudup profile.
	 * @param profile Hudup profile.
	 * @return list of PSO attributes.
	 */
	public static net.ea.pso.AttributeList extractPSOAttributes(Profile profile) {
		if (profile == null) return new net.ea.pso.AttributeList();
		
		net.ea.pso.AttributeList newAttRef = new net.ea.pso.AttributeList();
		for (int i = 0; i < profile.getAttCount(); i++) {
			Type type = profile.getAtt(i).getType();
			String name = profile.getAtt(i).getName();
			net.ea.pso.Attribute.Type newType = net.ea.pso.Attribute.Type.real;
			switch (type) {
			case bit:
				newType = net.ea.pso.Attribute.Type.bit;
				break;
			case nominal:
				newType = net.ea.pso.Attribute.Type.integer;
				break;
			case integer:
				newType = net.ea.pso.Attribute.Type.integer;
				break;
			case real:
				newType = net.ea.pso.Attribute.Type.real;
				break;
			case string:
				newType = net.ea.pso.Attribute.Type.string;
				break;
			case date:
				newType = net.ea.pso.Attribute.Type.date;
				break;
			case time:
				newType = net.ea.pso.Attribute.Type.time;
				break;
			case object:
				newType = net.ea.pso.Attribute.Type.object;
				break;
			}
			
			newAttRef.add(new net.ea.pso.Attribute(name, newType));
		}
		
		return newAttRef;
	}
	
	
	/**
	 * Convert Hudup configuration to PSO configuration.
	 * @param config Hudup configuration.
	 * @return PSO configuration.
	 */
	public static PSOConfig transferToPSOConfig(DataConfig config) {
		if (config == null) return new PSOConfig();
		
		PSOConfig psoConfig = new PSOConfig();
		Set<String> keys = config.keySet();
		for (String key : keys) {
			if (psoConfig.containsKey(key)) psoConfig.put(key, config.get(key));
		}
		
		return psoConfig;
	}


	/**
	 * Convert PSO configuration to Hudup configuration.
	 * @param psoConfig PSO configuration.
	 * @return Hudup configuration.
	 */
	public static DataConfig toConfig(PSOConfig psoConfig) {
		if (psoConfig == null) return new DataConfig();
		
		DataConfig config = new DataConfig();
		Set<String> keys = psoConfig.keySet();
		for (String key : keys) {
			config.put(key, psoConfig.get(key));
		}
		
		return config;
	}


}
