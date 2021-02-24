/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.io.Serializable;

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
	 * Getting zero vector.
	 * @return zero vector.
	 */
	Vector<T> zero();

	
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
	 * Creating particle with specified initial value.
	 * @param initialValue initial value.
	 * @return particle with initial values.
	 */
	Particle<T> createParticle(T initialValue);
	
	
	/**
	 * Creating particle with specified position and velocity.
	 * @param position specified position.
	 * @param velocity specified velocity.
	 * @return particle with specified position and velocity.
	 */
	Particle<T> createParticle(Vector<T> position, Vector<T> velocity);

		
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
	
	
	/**
	 * Extracting PSO setting from configuration.
	 * @param config data configuration.
	 * @return PSO setting.
	 */
	PSOSetting<T> extractPSOSetting(PSOConfig config);


	/**
	 * Extracting bound.
	 * @param bounds bound text.
	 * @return extracted bound.
	 */
	T[] extractBound(String bounds);
	
	
}
