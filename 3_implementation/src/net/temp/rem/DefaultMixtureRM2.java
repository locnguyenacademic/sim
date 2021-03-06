/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.rem;

import static net.rem.em.EMAbstract.EM_EPSILON_FIELD;
import static net.rem.em.EMAbstract.EM_MAX_ITERATION_FIELD;
import static net.rem.regression.em.DefaultMixtureREM.COMP_NUMBER_FIELD;
import static net.rem.regression.em.DefaultMixtureREM.PREV_PARAMS_FIELD;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.rem.regression.DefaultMixtureRM;
import net.rem.regression.em.DefaultMixtureREM;
import net.rem.regression.em.ExchangedParameter;

/**
 * This class is an extension of {@link DefaultMixtureRM}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
public class DefaultMixtureRM2 extends DefaultMixtureRM {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/*
	 * This method is not marked synchronized because it is called by setup method.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public /*synchronized*/ Object learnStart(Object... info) throws RemoteException {
		// TODO Auto-generated method stub
		DefaultMixtureREM prevMixREM = null;
		double prevFitness = -1;
		int maxK = getConfig().getAsInt(COMP_MAX_NUMBER_FIELD);
		maxK = maxK <= 0 ? Integer.MAX_VALUE : maxK;
		while (true) {
			DefaultMixtureREM mixREM = createInternalRM();
			
			if (prevMixREM != null) {
				List<ExchangedParameter> prevParameters = ExchangedParameter.clone((List<ExchangedParameter>)prevMixREM.getParameter());
				if (prevParameters instanceof Serializable)
					mixREM.getConfig().put(PREV_PARAMS_FIELD, (Serializable)prevParameters);
				else {
					ArrayList<ExchangedParameter> tempParameters = new ArrayList<>();
					tempParameters.addAll(prevParameters);
					mixREM.getConfig().put(PREV_PARAMS_FIELD, tempParameters);
				}
				mixREM.getConfig().put(COMP_NUMBER_FIELD, prevParameters.size() + 1);
			}
			if (prevMixREM == null)
				mixREM.setup((Fetcher<Profile>)sample);
			else
				mixREM.setup(prevMixREM);
			
			// Breaking if zero alpha or zero coefficient.
			List<ExchangedParameter> parameters = (List<ExchangedParameter>)mixREM.getParameter();
			if (parameters == null || parameters.size() == 0 || parameters.size() > maxK) {
				mixREM.unsetup();
				break;
			}
			boolean breakhere = false;
			for (ExchangedParameter parameter : parameters) {
				if (parameter.getCoeff() == 0 || parameter.isNullAlpha()) {
					breakhere = true;
					break;
				}
			}
			if (breakhere) {
				mixREM.unsetup();
				break;
			}
			
			double fitness = mixREM.getFitness();
			if (Util.isUsed(fitness)
					&& fitness > prevFitness) {
				prevFitness = fitness;
				if (prevMixREM != null)
					prevMixREM.unsetup();
				prevMixREM = mixREM;
				
				if (((List<ExchangedParameter>)prevMixREM.getParameter()).size() >= maxK)
					break;
			}
			else {
				mixREM.unsetup();
				break;
			}
		}
		
		if (prevMixREM != null)
			prevMixREM.unsetup();
		this.mixREM = prevMixREM;
		return prevMixREM;
	}


	/**
	 * Creating internal regression model.
	 * @return internal regression model.
	 */
	protected DefaultMixtureREM createInternalRM() {
		DefaultMixtureREM2 mixREM = new DefaultMixtureREM2();
		mixREM.getConfig().put(EM_EPSILON_FIELD, this.getConfig().get(EM_EPSILON_FIELD));
		mixREM.getConfig().put(EM_MAX_ITERATION_FIELD, this.getConfig().get(EM_MAX_ITERATION_FIELD));
		mixREM.getConfig().put(R_INDICES_FIELD, this.getConfig().get(R_INDICES_FIELD));
		mixREM.getConfig().put(COMP_NUMBER_FIELD, 1);
		
		return mixREM;
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "cluster_mixrm2";
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}


}
