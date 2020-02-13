package net.hudup.alg.cf.test.compound;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestAlg;
import net.hudup.core.alg.cf.NeighborCF2d;
import net.hudup.core.data.DataConfig;

public class D2Cosine extends NeighborCF2d implements TestAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public D2Cosine() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return COSINE;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "c01.01.2d.cosine";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		D2Cosine cf = new D2Cosine();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


}
