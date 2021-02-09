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
 * This class represents a particle.
 * @param <T> type of evaluated object.
 * @author Loc Nguyen
 * @version 1.0
 */
public class Particle<T> implements Serializable, Cloneable {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Position of particle.
	 */
	public Vector<T> position = null;
	
	
	/**
	 * Velocity of particle.
	 */
	public Vector<T> velocity = null;
	
	
	/**
	 * Best position of particle.
	 */
	public Vector<T> bestPosition = null;

	
	/**
	 * Best evaluated value of the best position.
	 */
	public T bestValue = null;
	
	
	/**
	 * Evaluated value of the position. This information is not important.
	 */
	public T value = null;

	
	/**
	 * Constructor with specified initial value and function.
	 * @param initialValue initial value.
	 * @param func specified function.
	 */
	public Particle(T initialValue, Function<T> func) {
		this.position = func.createVector(initialValue);
		this.velocity = func.createVector(initialValue);
		this.bestPosition = this.position != null ? this.position.duplicate() : null;
		
		if (func != null && this.bestPosition != null)
			this.bestValue = this.value = func.eval(this.bestPosition);
	}
	
	
	/**
	 * Constructor with specified position, velocity, and function.
	 * @param position specified position.
	 * @param velocity specified velocity.
	 * @param func specified function.
	 */
	public Particle(Vector<T> position, Vector<T> velocity, Function<T> func) {
		this.position = position;
		this.velocity = velocity;
		this.bestPosition = this.position != null ? this.position.duplicate() : null;
		
		if (this.bestPosition != null)
			this.bestValue = this.value = func.eval(this.bestPosition);
	}
	
	
	/**
	 * Evaluating position and best position by specified target function.
	 * @param func specified target function.
	 */
	public void eval(Function<T> func) {
		if (func == null) return;
		
		if (position != null) value = func.eval(position);
		if (bestPosition != null) bestValue = func.eval(bestPosition);
	}
	
	
	/**
	 * Checking whether this particle is valid.
	 * @return whether this particle is valid.
	 */
	public boolean isValid() {
		return position != null && velocity != null && bestPosition != null && bestPosition.isValid(bestValue);
	}
	
	

}
