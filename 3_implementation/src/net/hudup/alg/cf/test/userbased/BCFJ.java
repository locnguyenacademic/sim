package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.NeighborCFExtUserBased;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestAlg;
import net.hudup.core.data.DataConfig;

public class BCFJ extends NeighborCFExtUserBased implements TestAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public BCFJ() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return BCFJ;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u06.02.bcfj";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		BCFJ cf = new BCFJ();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


}
