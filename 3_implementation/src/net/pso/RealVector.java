/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

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
		return multiplyCoeff(alpha);
	}


	@Override
	public Vector<Double> multiplyCoeff(double alpha) {
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


}
