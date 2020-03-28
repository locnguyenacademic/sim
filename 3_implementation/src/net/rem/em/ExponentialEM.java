package net.rem.em;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.logistic.LogUtil;

/**
 * This abstract class model a expectation maximization (EM) algorithm for exponential family.
 * In other words, probabilistic distributions in this class belongs to exponential family.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ExponentialEM extends EMAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ExponentialEM() {
		// TODO Auto-generated constructor stub
		super();
	}

	
	/**
	 * This method implement expectation step (E-step) of EM.
	 * @param currentParameter current parameter.
	 * @param info additional information.
	 * @return sufficient statistic given current parameter.
	 * @throws RemoteException if any error raises
	 */
	protected abstract Object expectation(Object currentParameter, Object...info) throws RemoteException;
	
	
	/**
	 * This method implement maximization step (M-step) of EM.
	 * @param currentStatistic current sufficient statistic.
	 * @param info additional information.
	 * @return estimated parameter given current sufficient statistic.
	 * @throws RemoteException if any error raises
	 */
	protected abstract Object maximization(Object currentStatistic, Object...info) throws RemoteException;
	
	
	/*
	 * In the previous version, the learn method is marked synchronized.
	 * However, if the number of loops is huge, removing synchronized keyword allows clients to retrieve the estimated parameter (learned result)
	 * when this learn method is running.
	 */
	@Override
	protected synchronized Object learn(Object...info) throws RemoteException {
		// TODO Auto-generated method stub
		this.estimatedParameter = this.currentParameter = this.previousParameter = this.statistics = null;
		this.currentIteration = 0;
		this.estimatedParameter = this.currentParameter = initializeParameter();
		initializeNotify();
		if (this.estimatedParameter == null) {
			finishNotify();
			return null;
		}
		
		this.currentIteration = 1;
		int maxIteration = getMaxIteration();
		while(this.currentIteration < maxIteration) {
			Object tempStatistics = expectation(this.currentParameter);
			if (tempStatistics == null)
				break;
			
			this.statistics = tempStatistics;
			this.estimatedParameter = maximization(this.statistics);
			if (this.estimatedParameter == null)
				break;
			
			//Firing setup doing event
			try {
				fireSetupEvent(new EMLearningEvent(this, Type.doing, this.dataset,
						this.currentIteration, (Serializable)this.statistics,
						(Serializable)this.currentParameter, (Serializable)this.estimatedParameter));
			}
			catch (Throwable e) {LogUtil.trace(e);}
			
			boolean terminated = terminatedCondition(this.estimatedParameter, this.currentParameter, this.previousParameter);
			if (terminated)
				break;
			else {
				this.previousParameter = this.currentParameter;
				this.currentParameter = this.estimatedParameter;
				this.currentIteration++;
				permuteNotify();
			}
		}
		
		if (this.estimatedParameter != null)
			this.currentParameter = this.estimatedParameter;
		else if (this.currentParameter != null)
			this.estimatedParameter = this.currentParameter;
		
		//Firing setup done event
		try {
			fireSetupEvent(new EMLearningEvent(this, Type.done, this.dataset,
					this.currentIteration, (Serializable)this.statistics,
					(Serializable)this.currentParameter, (Serializable)this.estimatedParameter));
		}
		catch (Throwable e) {LogUtil.trace(e);}

		finishNotify();
		return this.estimatedParameter;
	}


}
