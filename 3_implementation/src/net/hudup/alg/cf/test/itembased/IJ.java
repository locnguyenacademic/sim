package net.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.NeighborCFExtItemBased;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestAlg;
import net.hudup.core.data.DataConfig;

public class IJ extends NeighborCFExtItemBased implements TestAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public IJ() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return IJ;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i03.03.improvedj";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		IJ cf = new IJ();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


}
