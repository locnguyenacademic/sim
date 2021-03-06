/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.hudup.alg.cf.test2;

import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

/**
 * Jaccard + MSD measure (MSDJ) for test.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JaccardxMSD extends net.hudup.core.alg.cf.nb.beans.MSD implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public JaccardxMSD() {
		
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "a03.01.04.01.jaccardxmsd";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(KNN, 100);
		config.put(FAST_RECOMMEND, true);
		config.put(MSD_TYPE, MSD_TYPE_JACCARD);
		
		return config;
	}


}
