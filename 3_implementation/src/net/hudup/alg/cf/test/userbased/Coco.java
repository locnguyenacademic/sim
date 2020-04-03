package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.NeighborCFExtUserBased;
import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.ForTest;
import net.hudup.core.data.DataConfig;

public class Coco extends NeighborCFExtUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public Coco() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return COCO;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u20.01.coco";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		Coco cf = new Coco();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}

	
}
