/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf;

import java.rmi.RemoteException;
import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;

/**
 * This abstract class implements mainly the Wasp Waist (WW) collaborative filtering algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class WaspWaistCF extends NeighborCFExt {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Cache of column rushed rating vectors.
	 */
	protected Map<Integer, Object> columnCrushedCache = Util.newMap();

	
	/**
	 * Dual CF algorithm.
	 */
	protected NeighborCFExt dualCF = createDualCF();
	
	
	/**
	 * Default constructor.
	 */
	public WaspWaistCF() {
		// TODO Auto-generated constructor stub
		this.dualCF.setConfig(this.getConfig());
	}


	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		// TODO Auto-generated method stub
		super.setup(dataset, params);
		this.columnCrushedCache.clear();
		this.dualCF.setup(dataset, params);
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		this.columnCrushedCache.clear();
		this.dualCF.unsetup();
	}


	/**
	 * Crushing column rating vector having specified ID.
	 * @param columnId specified ID.
	 * @param userRating recommendation user rating vector.
	 * @return crushed column rating vector.
	 */
	protected RatingVector crush(int columnId, RatingVector userRating) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				return crushAsUsual(columnId, userRating);
			}
		};
		
		return (RatingVector)cacheTask(columnId, this.columnCrushedCache, task);
	}
	
	
	/**
	 * Crushing column rating vector having specified ID as usual.
	 * @param columnId specified ID.
	 * @param userRating recommendation user rating vector.
	 * @return crushed column rating vector.
	 */
	protected abstract RatingVector crushAsUsual(int columnId, RatingVector userRating);
	
	
	/**
	 * Create dual CF algorithm.
	 * @return dual CF algorithm.
	 */
	protected abstract NeighborCFExt createDualCF();
	
	
}
