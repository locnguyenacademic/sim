/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.util.List;

import net.ea.pso.Attribute.Type;

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


	/**
	 * Extracting bound.
	 * @param <T> element type.
	 * @param func specified function.
	 * @param bounds bound text.
	 * @return extracted bound.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static <T> List<T> extractBound(Function<T> func, String bounds) {
		List<T> boundList = Util.newList(0);
		if (func == null) return boundList;
		
		try {
			@SuppressWarnings("unchecked")
			Class<T> tClass = (Class<T>) func.zero().elementZero().getClass();
			bounds = bounds != null ? bounds : "";
			
			boundList = Util.parseListByClass(bounds, tClass, ",");
			if (boundList.size() == 0) return boundList;
			
			int n = func.getVarNum();
			if (n < boundList.size()) {
				boundList = boundList.subList(0, n);
			}
			else {
				T lastValue = boundList.get(boundList.size() - 1);
				n = n - boundList.size();
				for (int i = 0; i < n; i++) boundList.add(lastValue);
			}
		}
		catch (Throwable e) {}
		
		return boundList;
	}

	
}
