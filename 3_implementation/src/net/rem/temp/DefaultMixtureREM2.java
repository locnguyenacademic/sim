/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.rem.temp;

import java.rmi.RemoteException;

import net.hudup.core.Constants;
import net.rem.regression.em.DefaultMixtureREM;

/**
 * This class is an extension of {@link DefaultMixtureREM}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
public class DefaultMixtureREM2 extends DefaultMixtureREM {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public synchronized double executeByXStatistic(double[] xStatistic) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0 || xStatistic == null)
			return Constants.UNUSED;
		
		if (xStatistic[1] < 0.5)
			return this.rems.get(0).executeByXStatistic(xStatistic);
		else
			return this.rems.get(1).executeByXStatistic(xStatistic);
//		double result = 0;
//		for (REMImpl rem : this.rems) {
//			ExchangedParameter parameter = rem.getExchangedParameter();
//			
//			double value = rem.executeByXStatistic0(xStatistic);
//			if (Util.isUsed(value))
//				result += parameter.getCoeff() * value;
//			else
//				return Constants.UNUSED;
//		}
//		return result;
	}


	@Override
	public synchronized Object execute(Object input) throws RemoteException {
		// TODO Auto-generated method stub
		double x = extractRegressorValue(input, 1);
		if (x < 0.5)
			return this.rems.get(0).execute(input);
		else
			return this.rems.get(1).execute(input);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "default_mixrem2";
	}

	
	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}


}
