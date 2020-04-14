package net.temp;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "new recommender";
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "new recommender";
	}
	

	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new TestRecommender();
	}


	

}
