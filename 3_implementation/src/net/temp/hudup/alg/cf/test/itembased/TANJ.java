/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

/**
 * TANJ measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class TANJ extends NeighborCFExtItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public TANJ() {

	}

	
	@Override
	public String getDefaultMeasure() {
		return Measure.TA;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i10.04.tanj";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(TA_NORMALIZED_FIELD, true);
		config.put(TA_TYPE, TA_TYPE_JACCARD);
		return config;
	}


}
