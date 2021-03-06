/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.bnet;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.alg.KBase;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.bit.BitData;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.logistic.inference.BnetBinaryGraph;
import net.hudup.logistic.inference.BnetBinaryGraphHybrid;

/**
 * This class implements hybrid collaborative filtering algorithm based on distributed binary Bayesian networks.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
public class BnetBinaryDistributedHybridCF extends BnetBinaryDistributedExtCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public BnetBinaryDistributedHybridCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase newKB() throws RemoteException {
		// TODO Auto-generated method stub
		return BnetBinaryDistributedHybridKB.create(this);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_binary_distributed_hybrid";
	}
	

	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Hybrid distributed clustered binary Bayesian network collaborative filtering algorithm";
	}


	@Override
	protected List<Pair> createEvidencePairList(RatingVector vRat, Profile profile) {
		return Pair.toCategoryPairList(vRat, profile);
	}


}



/**
 * This is knowledge base of hybrid collaborative filtering algorithm based on distributed binary Bayesian networks.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetBinaryDistributedHybridKB extends BnetBinaryDistributedExtKB {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	protected BnetBinaryGraph createBayesGraph(BitData bitData,
			int bitId, double minprob) {
		// TODO Auto-generated method stub
		return BnetBinaryGraphHybrid.create(bitData, bitId, minprob);
	}
	
	
	/**
	 * Create knowledge base of hybrid collaborative filtering algorithm based on distributed binary Bayesian network.
	 * @param cf hybrid collaborative filtering algorithm based on distributed binary Bayesian network.
	 * @return knowledge base of hybrid collaborative filtering algorithm based on distributed binary Bayesian network.
	 */
	public static BnetBinaryDistributedHybridKB create(final BnetBinaryDistributedHybridCF cf) {
		return new BnetBinaryDistributedHybridKB() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return cf.getName();
			}
		};
	}
	
	
}
