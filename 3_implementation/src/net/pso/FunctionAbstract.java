package net.pso;

import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;

/**
 * This abstract class represents the abstract function which is implements partially the interface {@link Function}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class FunctionAbstract implements Function {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of variables.
	 */
	protected AttributeList vars = new AttributeList();
	
	
	/**
	 * Internal optimizer.
	 */
	protected Optimizer optimizer = null;
	
	
	/**
	 * Default constructor.
	 */
	public FunctionAbstract() {

	}


	@Override
	public int getVarNum() {
		return vars.size();
	}


	@Override
	public Attribute getVar(int index) {
		return vars.get(index);
	}


	@Override
	public Optimizer getOptimizer() {
		return optimizer;
	}

	
	@Override
	public void setOptimizer(Optimizer optimizer) {
		this.optimizer = optimizer;
	}

	
}
