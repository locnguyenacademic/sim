/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.util.Collection;

/**
 * This class models a profile as real number vector.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public final class RealVector extends Vector<Double> {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public RealVector() {

	}

	
	/**
	 * Constructor with specified attribute list.
	 * @param attRef specified attribute list.
	 */
	public RealVector(AttributeList attRef) {
		super(attRef);
		int n = getAttCount();
		for (int i = 0; i < n; i++) setValue(i, 0.0);
	}

	
	/**
	 * Default constructor
	 * @param dim specified dimension.
	 * @param initialValue initial value.
	 */
	protected RealVector(int dim, double initialValue) {
		super(AttributeList.defaultRealVarAttributeList(dim));
		int n = getAttCount();
		for (int i = 0; i < n; i++) setValue(i, 0.0);
	}

	
	@Override
	public Double get(int index) {
		return getValueAsReal(index);
	}


	@Override
	public Vector<Double> duplicate() {
		RealVector profile = new RealVector();
		profile.attRef = this.attRef;
		
		profile.attValues.clear();
		profile.attValues.addAll(this.attValues);
		
		return profile;
	}


	@Override
	public boolean isValid(Double value) {
		return value != null && !Double.isNaN(value);
	}


	@Override
	public Double elementZero() {
		return 0.0;
	}


	@Override
	public Double module() {
		int n = this.getAttCount();
		double module = 0;
		for (int i = 0; i < n; i++) {
			double value = this.getValueAsReal(i);
			module += value * value;
		}
		
		return Math.sqrt(module);
	}


	@Override
	public Double distance(Vector<Double> that) {
		int n = Math.min(this.getAttCount(), that.getAttCount());
		double dis = 0;
		for (int i = 0; i < n; i++) {
			double deviate = this.getValueAsReal(i) - that.getValueAsReal(i);
			dis += deviate * deviate;
		}

		return Math.sqrt(dis);
	}


	@Override
	public Vector<Double> add(Vector<Double> that) {
		int n = Math.min(this.getAttCount(), that.getAttCount());
		for (int i = 0; i < n; i++) {
			double value = this.getValueAsReal(i) + that.getValueAsReal(i);
			this.setValue(i, value);
		}
		
		return this;
	}


	@Override
	public Vector<Double> subtract(Vector<Double> that) {
		int n = Math.min(this.getAttCount(), that.getAttCount());
		for (int i = 0; i < n; i++) {
			double value = this.getValueAsReal(i) - that.getValueAsReal(i);
			this.setValue(i, value);
		}
		
		return this;
	}


	@Override
	public Vector<Double> multiply(Double alpha) {
		int n = this.getAttCount();
		for (int i = 0; i < n; i++) {
			double value = alpha * this.getValueAsReal(i);
			this.setValue(i, value);
		}
		
		return this;
	}


	@Override
	public Vector<Double> multiplyWise(Vector<Double> that) {
		int n = Math.min(this.getAttCount(), that.getAttCount());
		for (int i = 0; i < n; i++) {
			double value = this.getValueAsReal(i) * that.getValueAsReal(i);
			this.setValue(i, value);
		}
		
		return this;
	}


	@Override
	public Vector<Double> mean(Collection<Vector<Double>> vectors) {
		int n = getAttCount();
		for (int i = 0; i < n; i++) setValue(i, 0.0);
		if (vectors == null || vectors.size() == 0) return this;
		
		for (Vector<Double> vector : vectors) {
			this.add(vector);
		}
		this.multiply(1.0 / (double)vectors.size());

		return this;
	}


	/**
	 * Converting this vector to array.
	 * @param vector specified vector.
	 * @return converted array.
	 */
	public static Double[] toArray(Vector<Double> vector) {
		int n = vector.getAttCount();
		Double[] array = new Double[n];
		for (int i = 0; i < n; i++) array[i] = vector.getValueAsReal(i);
		
		return array;
	}
	
	
}
