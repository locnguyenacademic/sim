/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;

/**
 * This abstract class represents the abstract function which is implements partially the interface {@link Function}.
 * 
 * @param <T> type of evaluated object.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class FunctionAbstract<T> implements Function<T> {


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
	protected Optimizer<T> optimizer = null;
	
	
	/**
	 * Constructor with dimension and type.
	 * @param dim specified dimension.
	 * @param type variable type.
	 */
	public FunctionAbstract(int dim, Type type) {
		vars = AttributeList.defaultVarAttributeList(dim, type);
	}


	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(T evalA, T evalB) {
		if (evalA == null && evalB == null) return 0;
		if (evalA == null) return -1;
		if (evalB == null) return 1;
		
		if ((evalA instanceof Number) && (evalB instanceof Number)) {
			double a = ((Number)evalA).doubleValue();
			double b = ((Number)evalB).doubleValue();
			if (a < b)
				return -1;
			else if (a == b)
				return 0;
			else
				return 1;
		}
		else if (evalA instanceof Comparable<?>)
			return ((Comparable<T>)evalA).compareTo(evalB);
		else
			return -1;
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
	public Optimizer<T> getOptimizer() {
		return optimizer;
	}

	
	@Override
	public void setOptimizer(Optimizer<T> optimizer) {
		this.optimizer = optimizer;
	}

	
}
