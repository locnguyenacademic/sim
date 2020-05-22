package net.temp;

import java.rmi.RemoteException;

import net.hudup.core.alg.cf.NeighborCFUserBased;

/**
 * Temporal test recommender algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
public class TestRecommender extends NeighborCFUserBased {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1;

	
	/**
	 * Default constructor.
	 */
	public TestRecommender() {

	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "new recommender";
	}

	
	@Override
	public String getName() {
		return "new recommender";
	}
	

}
