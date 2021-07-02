/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFTwosCombinedItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

/**
 * SMD2 + COD measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class SMD2COD extends NeighborCFTwosCombinedItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public SMD2COD() {

	}


	@Override
	public String getDefaultMeasure() {
		return Measure.SMD2;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i09.03.smd2_cosine_adjusted";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.SMD2);
		config.put(OTHER_MEASURE, Measure.COSINE);
		config.put(COSINE_TYPE, COSINE_TYPE_ADJUSTED);
		
		return config;
	}


}
