/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann.adapter;

import java.util.List;
import java.util.Map;

import net.hudup.core.Constants;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.Profile;

/**
 * This is utility class to provide static utility methods. It is also adapter to other libraries.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Util {

	
	/**
	 * Default date format.
	 */
	public static String DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";

	
	/**
	 * Static code.
	 */
	static {
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
	 * Tracing error.
	 * @param e throwable error.
	 */
	public static void trace(Throwable e) {
		net.hudup.core.logistic.LogUtil.trace(e);
	}


	/**
	 * Converting Hudup profile into ANN profile.
	 * @param newAttRef ANN attributes.
	 * @param profile Hudup profile.
	 * @return ANN profile.
	 */
	public static net.ea.ann.Profile toANNProfile(net.ea.ann.AttributeList newAttRef, Profile profile) {
		if (newAttRef == null || profile == null) return null;
		
		net.ea.ann.Profile newProfile = new net.ea.ann.Profile(newAttRef);
		int n = Math.min(newProfile.getAttCount(), profile.getAttCount());
		for (int i = 0; i < n; i++) {
			newProfile.setValue(i, profile.getValue(i));
		}
		
		return newProfile;
	}
	
	
	/**
	 * Converting Hudup profile into ANN profile.
	 * @param profile Hudup profile.
	 * @return ANN profile.
	 */
	public static net.ea.ann.Profile toANNProfile(Profile profile) {
		net.ea.ann.AttributeList newAttRef = extractANNAttributes(profile);
		return toANNProfile(newAttRef, profile);
	}

		
	/**
	 * Extracting ANN attributes from Hudup profile.
	 * @param profile Hudup profile.
	 * @return list of ANN attributes.
	 */
	public static net.ea.ann.AttributeList extractANNAttributes(Profile profile) {
		if (profile == null) return new net.ea.ann.AttributeList();
		
		net.ea.ann.AttributeList newAttRef = new net.ea.ann.AttributeList();
		for (int i = 0; i < profile.getAttCount(); i++) {
			Type type = profile.getAtt(i).getType();
			String name = profile.getAtt(i).getName();
			net.ea.ann.Attribute.Type newType = net.ea.ann.Attribute.Type.real;
			switch (type) {
			case bit:
				newType = net.ea.ann.Attribute.Type.bit;
				break;
			case nominal:
				newType = net.ea.ann.Attribute.Type.integer;
				break;
			case integer:
				newType = net.ea.ann.Attribute.Type.integer;
				break;
			case real:
				newType = net.ea.ann.Attribute.Type.real;
				break;
			case string:
				newType = net.ea.ann.Attribute.Type.string;
				break;
			case date:
				newType = net.ea.ann.Attribute.Type.date;
				break;
			case time:
				newType = net.ea.ann.Attribute.Type.time;
				break;
			case object:
				newType = net.ea.ann.Attribute.Type.object;
				break;
			}
			
			newAttRef.add(new net.ea.ann.Attribute(name, newType));
		}
		
		return newAttRef;
	}


}
