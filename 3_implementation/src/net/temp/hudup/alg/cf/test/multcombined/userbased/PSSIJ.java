/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.hudup.alg.cf.test.multcombined.userbased;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;
import net.temp.hudup.alg.cf.test.multcombined.NeighborCFTwosCombinedMultUserBased;

/**
 * PSS + IJ measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PSSIJ extends NeighborCFTwosCombinedMultUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public PSSIJ() {

	}


	@Override
	public String getDefaultMeasure() {
		return Measure.PSS;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "mcu04.03.pss_improvedj";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.PSS);
		config.put(OTHER_MEASURE, Measure.JACCARD);
		config.put(JACCARD_TYPE, JACCARD_TYPE_IJ);
		
		return config;
	}


}
