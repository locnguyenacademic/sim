/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

/**
 * Logistic function
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class LogisticFunction implements Function {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public LogisticFunction() {

	}

	
	@Override
	public double eval(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}


}
