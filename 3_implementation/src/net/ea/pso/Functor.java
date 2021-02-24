package net.ea.pso;

import java.io.Serializable;
import java.util.List;

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
	 * PSO setting.
	 */
	public PSOSetting<T> setting = null;
	
	
	/**
	 * Default constructor.
	 */
	public Functor() {

	}

	
	/**
	 * Extracting the pair of function and optimizer via specified profile.
	 * @param pso specified PSO.
	 * @param profile specified profile.
	 * @return the pair of function and optimizer via specified profile.
	 */
	@Deprecated
	@SuppressWarnings({ "unchecked", "unused" })
	private static <T> Functor<T> create(PSOAbstract<T> pso, Profile profile) {
		if (pso == null || profile == null || profile.getAttCount() < 6) return null;
		
		Functor<T> functor = new Functor<T>();

		String expr = profile.getValueAsString(0);
		expr = expr != null ? expr.trim() : null;
		if (expr == null) return null;
		List<String> varNames = Util.parseListByClass(profile.getValueAsString(1), String.class, ",");
		if (varNames.size() == 0) return null;
		
		functor.func = pso.defineExprFunction(varNames, expr);
		if (functor.func == null) return null;
		
		try {
			functor.setting = (PSOSetting<T>) functor.func.extractPSOSetting(pso.getConfig());
			functor.setting.lower = functor.func.extractBound(profile.getValueAsString(2));
			functor.setting.upper = functor.func.extractBound(profile.getValueAsString(3));
		} catch (Exception e) {Util.trace(e);}
		
		T elementZero = functor.func.zero().elementZero();
		Vector<T> bestPosition = functor.func.createVector(elementZero);
		List<T> position = (List<T>) Util.parseListByClass(profile.getValueAsString(4), elementZero.getClass(), ",");
		int n = Math.min(bestPosition.getAttCount(), position.size());
		for (int i = 0; i < n; i++) {
			bestPosition.setValue(i, position.get(i));
		}
		
		String bestValueText = profile.getValueAsString(5);
		T bestValue = (T) Util.parseObjectByClass(bestValueText, elementZero.getClass());
		
		functor.func.setOptimizer(new Optimizer<T>(bestPosition, bestValue));
		
		return functor;
	}
	

}
