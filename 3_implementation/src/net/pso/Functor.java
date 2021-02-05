package net.pso;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;

import net.hudup.core.data.Profile;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class is a starter or manifest of function
 * .
 * @author Loc Nguyen
 * @version 1.0
 */
public class Functor<T> implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Mathematical expression function.
	 */
	public Function<T> func = null;
	
	
	/**
	 * Optimizer.
	 */
	public Optimizer<T> optimizer = null;
	
	
	/**
	 * PSO configuration.
	 */
	public PSOConfiguration<T> psoConfig = null;
	
	
	/**
	 * Mathematical expression.
	 */
	public String expr = null;
	
	
	/**
	 * Default constructor.
	 */
	protected Functor() {

	}

	
	/**
	 * Extracting the pair of function and optimizer via specified profile.
	 * @param pso specified PSO.
	 * @param profile specified profile.
	 * @return the pair of function and optimizer via specified profile.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Functor<T> create(PSOAbstract<T> pso, Profile profile) {
		if (pso == null || profile == null || profile.getAttCount() < 6) return null;
		
		Functor<T> functor = new Functor<T>();

		functor.expr = profile.getValueAsString(0);
		functor.expr = functor.expr != null ? functor.expr.trim() : null;
		if (functor.expr == null) return null;
		List<String> varNames = TextParserUtil.parseListByClass(profile.getValueAsString(1), String.class, ",");
		if (varNames.size() == 0) return null;
		
		functor.func = pso.defineExprFunction(varNames, functor.expr);
		if (functor.func == null) return null;
		
		try {
			Class<T> tClass = (Class<T>) pso.getFunction().zero().elementZero().getClass();
			functor.psoConfig = (PSOConfiguration<T>) pso.getPSOConfiguration();
			
			List<T> lowerBound = pso.extractBound(profile.getValueAsString(2));
			functor.psoConfig.lower = lowerBound.toArray((T[])Array.newInstance(tClass, 0));
			
			List<T> upperBound = pso.extractBound(profile.getValueAsString(3));
			functor.psoConfig.upper = upperBound.toArray((T[])Array.newInstance(tClass, 0));
		} catch (Exception e) {LogUtil.trace(e);}
		
		T elementZero = functor.func.zero().elementZero();
		Vector<T> bestPosition = functor.func.createVector(elementZero);
		List<T> position = (List<T>) TextParserUtil.parseListByClass(profile.getValueAsString(4), elementZero.getClass(), ",");
		int n = Math.min(bestPosition.getAttCount(), position.size());
		for (int i = 0; i < n; i++) {
			bestPosition.setValue(i, position.get(i));
		}
		
		String bestValueText = profile.getValueAsString(5);
		T bestValue = (T) TextParserUtil.parseObjectByClass(bestValueText, elementZero.getClass());
		
		functor.optimizer = new Optimizer<T>(bestPosition, bestValue);
		
		return functor;
	}
	

}
