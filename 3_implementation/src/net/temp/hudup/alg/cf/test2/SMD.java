/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.hudup.alg.cf.test2;

import net.hudup.core.data.DataConfig;

/**
 * SMD measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class SMD extends net.hudup.alg.cf.nb.beans.smd.SMD {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public SMD() {

	}


	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "a08.01.smd";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(KNN, 100);
		config.put(FAST_RECOMMEND, true);
		
		return config;
	}


}
