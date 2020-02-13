package net.rem.em;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.alg.SetupAlgEvent.Type;

/**
 * This class represents the generalized expectation maximization (GEM) algorithm.
 * It inherits directly from {@link EMAbstract}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class GEM extends EMAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public GEM() {
		// TODO Auto-generated constructor stub
		super();
	}

	
	/**
	 * Finding a maximizer of the conditional expectation Q based on current parameter.
	 * @param currentParameter current parameter.
	 * @param info additional information.
	 * @return a maximizer of the conditional expectation Q based on current parameter.
	 * @throws RemoteException if any error raises.
	 */
	protected abstract Object argmaxQ(Object currentParameter, Object...info) throws RemoteException;
	
	
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
			this.estimatedParameter = argmaxQ(this.currentParameter);
			if (this.estimatedParameter == null)
				break;
			
			//Firing doing event
			try {
				fireSetupEvent(new EMLearningEvent(this, Type.doing, this.dataset,
						this.currentIteration, null,
						(Serializable)this.currentParameter, (Serializable)this.estimatedParameter));
			}
			catch (Throwable e) {e.printStackTrace();}
			
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

		//Firing done event
		try {
			fireSetupEvent(new EMLearningEvent(this, Type.done, this.dataset,
					this.currentIteration, null,
					(Serializable)this.currentParameter, (Serializable)this.estimatedParameter));
		}
		catch (Throwable e) {e.printStackTrace();}

		finishNotify();
		return this.estimatedParameter;
	}


}
