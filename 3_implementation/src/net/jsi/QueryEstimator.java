package net.jsi;

import java.io.Serializable;

public interface QueryEstimator extends Serializable, Cloneable {

	
	Estimator getEstimator(String code, boolean buy);


}
