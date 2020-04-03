package net.temp;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.RecommenderAbstract;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;

/**
 * Temporal test recommender algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
public class TestRecommender extends RecommenderAbstract {

	
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
	public Dataset getDataset() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void setup(Dataset dataset, Object... params) throws RemoteException {
		// TODO Auto-generated method stub

	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
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
