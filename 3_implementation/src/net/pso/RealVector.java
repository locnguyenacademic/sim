/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.util.Collection;

import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;

/**
 * This class models a profile as real number vector.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class RealVector extends Vector<Double> {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
//	/**
//	 * Zero vector.
//	 */
//	private RealVector zero = null;
	
	
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

	
	@Override
	public Object clone() {
		RealVector profile = new RealVector();
		profile.attRef = this.attRef;
		
		profile.attValues.clear();
		profile.attValues.addAll(this.attValues);
		
		return profile;
	}

	
	@Override
	public Vector<Double> duplicate() {
		return (RealVector)clone();
	}


	@Override
	public boolean isValid(Double value) {
		return value != null && Util.isUsed(value);
	}


	@Override
	public Double elementZero() {
		return 0.0;
	}


//	@Override
//	public Vector<Double> zero() {
//		if (zero != null) return zero;
//		
//		if (this.attRef == null || this.attRef.size() == 0)
//			zero = null;
//		else {
//			zero = new RealVector(this.attRef);
//			int n = zero.getAttCount();
//			for (int i = 0; i < n; i++) zero.setValue(i, elementZero());
//		}
//
//		return zero;
//	}


//	@Override
//	public int compareTo(Double a, Double b) {
//		if (a < b)
//			return -1;
//		else if (a == b)
//			return 0;
//		else
//			return 1;
//	}


//	@Override
//	public double module(Double a) {
//		return Math.abs(a);
//	}

	
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


//	@Override
//	public double distance(Double a, Double b) {
//		return Math.abs(a - b);
//	}

	
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


}
