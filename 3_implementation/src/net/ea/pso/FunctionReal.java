/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.util.List;
import java.util.Random;

import net.ea.pso.Attribute.Type;

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
	 * Zero vector.
	 */
	private RealVector zero = null;

	
	/**
	 * Constructor with dimension and type.
	 * @param dim specified dimension.
	 */
	public FunctionReal(int dim) {
		super(dim, Type.real);
	}


	@Override
	public Vector<Double> zero() {
		if (zero != null) return zero;
		
		int n = vars.size();
		if (n == 0)
			zero = null;
		else {
			zero = new RealVector(vars);
			for (int i = 0; i < n; i++) zero.setValue(i, zero.elementZero());
		}

		return zero;

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
	public Particle<Double> createParticle(Double initialValue) {
		return new Particle<Double>(initialValue, this);
	}


	@Override
	public Particle<Double> createParticle(Vector<Double> position, Vector<Double> velocity) {
		return new Particle<Double>(position, velocity, this);
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

		return createParticle(position, velocity);
	}


	@Override
	public PSOSetting<Double> extractPSOSetting(PSOConfig config) {
		PSOSetting<Double> setting = new PSOSetting<Double>();
		if (config == null) return setting;
		
		double cognitiveWeight = config.getAsReal(PSOSetting.COGNITIVE_WEIGHT_FIELD);
		setting.cognitiveWeight = !Double.isNaN(cognitiveWeight) && cognitiveWeight > 0 ? cognitiveWeight : PSOSetting.COGNITIVE_WEIGHT_DEFAULT;
		
		double socialWeightGlobal = config.getAsReal(PSOSetting.SOCIAL_WEIGHT_GLOBAL_FIELD);
		setting.socialWeightGlobal = !Double.isNaN(socialWeightGlobal) && socialWeightGlobal > 0 ? socialWeightGlobal : PSOSetting.SOCIAL_WEIGHT_GLOBAL_DEFAULT;

		double socialWeightLocal = config.getAsReal(PSOSetting.SOCIAL_WEIGHT_LOCAL_FIELD);
		setting.socialWeightLocal = !Double.isNaN(socialWeightLocal) && socialWeightLocal > 0 ? socialWeightLocal : PSOSetting.SOCIAL_WEIGHT_LOCAL_DEFAULT;

		double inertialWeight = config.getAsReal(PSOSetting.INERTIAL_WEIGHT_FIELD);
		inertialWeight = !Double.isNaN(inertialWeight) && inertialWeight > 0 ? inertialWeight : PSOSetting.INERTIAL_WEIGHT_DEFAULT;
		setting.inertialWeight = createVector(inertialWeight);

		double constrictWeight = config.getAsReal(PSOSetting.CONSTRICT_WEIGHT_FIELD);
		constrictWeight = !Double.isNaN(constrictWeight) && constrictWeight > 0 ? constrictWeight : PSOSetting.CONSTRICT_WEIGHT_DEFAULT;
		setting.constrictWeight = createVector(constrictWeight);

		setting.lower = extractBound(config.getAsString(PSOSetting.POSITION_LOWER_BOUND_FIELD));
		
		setting.upper = extractBound(config.getAsString(PSOSetting.POSITION_UPPER_BOUND_FIELD));

		return setting;
	}


	@Override
	public Double[] extractBound(String bounds) {
		if (bounds == null) return RealVector.toArray(zero());
		List<Double> boundList = Util.parseListByClass(bounds, Double.class, ",");
		if (boundList.size() == 0) return RealVector.toArray(zero());
		
		int n = getVarNum();
		if (n < boundList.size())
			boundList = boundList.subList(0, n);
		else {
			Double lastValue = boundList.get(boundList.size() - 1);
			n = n - boundList.size();
			for (int i = 0; i < n; i++) boundList.add(lastValue);
		}
		
		return boundList.toArray(new Double[] {});
	}


}
