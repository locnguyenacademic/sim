/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.util.Random;

import net.hudup.core.data.Attribute.Type;

/**
 * This abstract class represents the abstract function whose image domain is real space.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class FunctionReal extends FunctionAbstract<Double> {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor with dimension and type.
	 * @param dim specified dimension.
	 */
	public FunctionReal(int dim) {
		super(dim, Type.real);
	}


	@Override
	public Double zero() {
		return 0.0;
	}


	@Override
	public int compareTo(Double evalA, Double evalB) {
		if (evalA < evalB)
			return -1;
		else if (evalA == evalB)
			return 0;
		else
			return 1;
	}


	@Override
	public double distance(Double evalA, Double evalB) {
		return Math.abs(evalA - evalB);
	}

	
	@Override
	public double distance(Double evalA) {
		return Math.abs(evalA);
	}

	
	@Override
	public Vector<Double> createVector(Double initialValue) {
		RealVector vector = new RealVector(vars);
		
		int dim = vars.size();
		for (int i = 0; i < dim; i++) {
			vector.setValue(i, initialValue);
		}
		
		return vector;
	}


	@Override
	public Vector<Double> createRandomVector(Double lower, Double upper) {
		Random rnd = new Random();
		Vector<Double> x = createVector(0.0);
		
		int dim = vars.size();
		for (int i = 0; i < dim; i++) {
			x.setValue(i, (upper - lower) * rnd.nextDouble() + lower);
		}
		
		return x;
	}


	@Override
	public Particle<Double> createRandomParticle(Double[] lower, Double[] upper) {
		int dim = vars.size();

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
		Vector<Double> position = createVector(0.0);
		Vector<Double> velocity = createVector(0.0);
		int d = Math.min(dim, getVarNum());
		for (int i = 0; i < d; i++) {
			String attName = getVar(i).getName();
			position.getAtt(i).setName(attName);
			velocity.getAtt(i).setName(attName);
		}
		
		for (int i = 0; i < dim; i++) {
			double p = (newUpper[i] - newLower[i]) * rnd.nextDouble() + newLower[i];
			position.setValue(i, p);
			
			double v = distances[i] * (2*rnd.nextDouble()-1);
			velocity.setValue(i, v);
		}

		return new Particle<Double>(position, velocity, this);
	}


}
