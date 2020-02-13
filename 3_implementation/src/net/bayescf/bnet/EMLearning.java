package net.bayescf.bnet;

import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class represent the expectation maximization (EM) algorithm for learning Bayesian network.
 * 
 * @author ShahidNaseem, Anum Shafiq, Loc Nguyen
 * @version 1.0 
 *
 */
@NextUpdate
public class EMLearning implements Blearning {

	
	@Override
	public Bnet learn(Fetcher<Profile> input, Object param) {
		//Coding here, learning Bayesian network from collection of profiles.
		return null;
	}

	
}
