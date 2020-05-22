/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.rem.cf;

import java.rmi.RemoteException;

import net.hudup.alg.cf.NeighborCFExtUserBased;
import net.hudup.core.Constants;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.rem.soco.Soco;

/**
 * This class sets up user-based neighbor collaborative filtering (Neighbor CF) algorithm with soft cosine.
 *  
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@NextUpdate
@BaseClass //This is not base class but the base class annotation prevents this algorithm to be registered in plug-in storage. The algorithm needs to be revised because it runs too slowly. Maybe it is not converged.
public class NeighborCFExtUserBasedSoco extends NeighborCFExtUserBased {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Soft cosine algorithm.
	 */
	protected Soco soco = null;
	
	
	/**
	 * Default constructor.
	 */
	public NeighborCFExtUserBasedSoco() {
		soco = new Soco() {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected double sim(RatingVector vRating1, RatingVector vRating2) {
				// TODO Auto-generated method stub
				return getThisNeighborCF().sim0(getThisNeighborCF().getMeasure(), vRating1, vRating2, (Profile)null, (Profile)null);
			}
			
		};
		soco.getConfig().put(DataConfig.MAIN_UNIT, DataConfig.RATING_UNIT);
		soco.getConfig().put(Soco.USER_RATING_MATRIX_FIELD, true);
	}


	@Override
	public synchronized void setup(Dataset dataset, Object... params) throws RemoteException {
		super.setup(dataset, params);
		soco.setup(dataset);
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		soco.unsetup();
	}
	
	
	@Override
	public synchronized double sim(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2,
			Object... parameters) {
		// TODO Auto-generated method stub
		try {
			return soco.getRowSim(vRating1.id(), vRating2.id());
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		return Constants.UNUSED;
	}


	/**
	 * Getting this algorithm.
	 * @return this algorithm.
	 */
	private NeighborCFExtUserBasedSoco getThisNeighborCF() {
		return this;
	}
	
	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_userbased_softcosine";
	}


}
