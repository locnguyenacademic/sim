/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.io.Serializable;

import net.hudup.core.data.Attribute;

/**
 * This interface represents function.
 * 
 * @param <T> type of evaluated object.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Function<T> extends Serializable, Cloneable {

	
	/**
	 * Evaluating this function given arguments.
	 * @param arg argument.
	 * @return evaluated value.
	 */
	T eval(Vector<T> arg);
	
	
	/**
	 * Getting zero.
	 * @return defined zero.
	 */
	T zero();
	
	
	/**
	 * Comparing evaluated value A with evaluated value B.
	 * @param evalA evaluated value A.
	 * @param evalB evaluated value B.
	 * @return -1 if value A is less than value B, 0 if value A is equal to value B, and 1 if value A is larger than value B.
	 */
	int compareTo(T evalA, T evalB);
	
	
	/**
	 * Calculating distance between evaluated value A with evaluated value B.
	 * @param evalA evaluated value A.
	 * @param evalB evaluated value B.
	 * @return distance between evaluated value A with evaluated value B.
	 */
	double distance(T evalA, T evalB);
	
	
	/**
	 * Calculating the module of value A.
	 * @param evalA evaluated value A.
	 * @return the module of value A.
	 */
	double distance(T evalA);

	
	/**
	 * Getting number of variables.
	 * @return number of variables.
	 */
	int getVarNum();
	
	
	/**
	 * Getting variable at specified index.
	 * @param index specified index.
	 * @return variable at specified index.
	 */
	Attribute getVar(int index);
	
	
	/**
	 * Getting optimizer.
	 * @return optimizer.
	 */
	Optimizer<T> getOptimizer();
	
	
	/**
	 * Setting optimizer.
	 * @param optimizer specified optimizer.
	 */
	void setOptimizer(Optimizer<T> optimizer);
	
	
	/**
	 * Creating vector with initial value.
	 * @param initialValue initial value.
	 * @return vector created with initial value.
	 */
	Vector<T> createVector(T initialValue);
	
	
	/**
	 * Making random vector from low bound to high bound.
	 * @param lower low bound.
	 * @param upper high bound.
	 * @return random vector from low bound to high bound.
	 */
	Vector<T> createRandomVector(T lower, T upper);

	
	/**
	 * Making random particle in range from lower to upper.
	 * @param lower lower bound.
	 * @param upper upper bound.
	 * @return random particle in range from lower to upper.
	 */
	Particle<T> createRandomParticle(T[] lower, T[] upper);
	
	
}
