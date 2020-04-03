package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.NeighborCFExtUserBased;
import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.ForTest;
import net.hudup.core.data.DataConfig;

/**
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Amer extends NeighborCFExtUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public Amer() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return AMER;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u08.01.amer";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		Amer cf = new Amer();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}

	
}
