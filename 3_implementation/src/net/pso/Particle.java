/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.io.Serializable;
import java.util.Random;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.ProfileVector;

/**
 * This class represents a particle.
 * @author Loc Nguyen
 * @version 1.0
 */
public class Particle implements Serializable, Cloneable {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Position of particle.
	 */
	public ProfileVector position = null;
	
	
	/**
	 * Velocity of particle.
	 */
	public ProfileVector velocity = null;
	
	
	/**
	 * Best position of particle.
	 */
	public ProfileVector bestPosition = null;

	
	/**
	 * Best evaluated value of the best position.
	 */
	public double bestValue = Constants.UNUSED;
	
	
	/**
	 * Constructor with specified dimension, initial value, and function.
	 * @param dim specified dimension.
	 * @param initialValue initial value.
	 * @param func specified function.
	 */
	public Particle(int dim, double initialValue, Function func) {
		this.position = ProfileVector.createVector(dim, initialValue);
		this.velocity = ProfileVector.createVector(dim, initialValue);
		this.bestPosition = this.position;
		
		if (func != null && this.bestPosition != null)
			this.bestValue = func.eval(this.bestPosition);
	}
	
	
	/**
	 * Constructor with specified dimension and initial value.
	 * @param dim specified dimension.
	 * @param initialValue initial value.
	 */
	public Particle(int dim, double initialValue) {
		this(dim, initialValue, null);
	}

	
	/**
	 * Constructor with specified position, velocity, and function.
	 * @param position specified position.
	 * @param velocity specified velocity.
	 * @param func specified function.
	 */
	public Particle(ProfileVector position, ProfileVector velocity, Function func) {
		this.position = position;
		this.velocity = velocity;
		this.bestPosition = this.position;
		
		if (func != null && this.bestPosition != null)
			this.bestValue = func.eval(this.bestPosition);
	}
	
	
	/**
	 * Constructor with specified position and velocity.
	 * @param position specified position.
	 * @param velocity specified velocity.
	 */
	public Particle(ProfileVector position, ProfileVector velocity) {
		this(position, velocity, null);
	}

	
	/**
	 * Checking whether this particle is valid.
	 * @return whether this particle is valid.
	 */
	public boolean isValid() {
		return position != null && velocity != null && bestPosition != null && Util.isUsed(bestValue);
	}
	
	
	/**
	 * Making random particle in range from lower to upper.
	 * @param dim dimension.
	 * @param lower lower bound.
	 * @param upper upper bound.
	 * @param func specified function.
	 * @return random particle in range from lower to upper.
	 */
	public static Particle makeRandom(int dim, double[] lower, double[] upper, Function func) {
		double[] newLower = new double[dim];
		double[] newUpper = new double[dim];
		double[] distances = new double[dim];
		for (int i = 0; i < dim; i++) {
			newLower[i] = 0;
			newUpper[i] = 1;
		}
		int n = lower != null ? Math.min(lower.length, dim) : 0;
		for (int i = 0; i < n; i++) newLower[i] = lower[i];
		n = upper != null ? Math.min(upper.length, dim) : 0;
		for (int i = 0; i < n; i++) newUpper[i] = upper[i];
		
		for (int i = 0; i < dim; i++) {
			double min = Math.min(newLower[i], newUpper[i]);
			double max = Math.max(newLower[i], newUpper[i]);
			newLower[i] = min;
			newUpper[i] = max;
			distances[i] = max - min;
		}
		
		Random rnd = new Random();
		ProfileVector position = ProfileVector.createVector(dim, 0);
		ProfileVector velocity = ProfileVector.createVector(dim, 0);
		if (func != null) {
			int d = Math.min(dim, func.getVarNum());
			for (int i = 0; i < d; i++) {
				String attName = func.getVar(i).getName();
				position.getAtt(i).setName(attName);
				velocity.getAtt(i).setName(attName);
			}
		}
		
		for (int i = 0; i < dim; i++) {
			double p = (newUpper[i] - newLower[i]) * rnd.nextDouble() + newLower[i];
			position.setValue(i, p);
			
			double v = distances[i] * (2*rnd.nextDouble()-1);
			velocity.setValue(i, v);
		}

		return new Particle(position, velocity, func);
	}
	
	
	/**
	 * Making random vector from low bound to high bound.
	 * @param dim dimension.
	 * @param lower low bound.
	 * @param upper high bound.
	 * @return random vector from low bound to high bound.
	 */
	public static ProfileVector makeRandom(int dim, double lower, double upper) {
		Random rnd = new Random();
		ProfileVector x = ProfileVector.createVector(dim, 0);
		
		for (int i = 0; i < dim; i++) {
			x.setValue(i, (upper - lower) * rnd.nextDouble() + lower);
		}
		
		return x;
	}


}
