package net.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.NeighborCFExtItemBased;
import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.ForTest;
import net.hudup.core.data.DataConfig;

public class QTI extends NeighborCFExtItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public QTI() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return QUASI_TFIDF;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i12.01.qti";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		QTI cf = new QTI();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


}
