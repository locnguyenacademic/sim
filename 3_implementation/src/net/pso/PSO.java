/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.ExecutableAlgAbstract;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.Vector;

/**
 * This class implement the particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PSO extends ExecutableAlgAbstract {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Function expression.
	 */
	public final static String FUNC_EXPR_FIELD = "function_expression";
	
	
	/**
	 * Default value for function expression.
	 */
	public final static String FUNC_EXPR_DEFAULT = "(#x1 + #x2)^2";

	
	/**
	 * Maximum iteration.
	 */
	public final static String MAX_ITERATION_FIELD = "max_iteration";
	
	
	/**
	 * Default value for maximum iteration.
	 */
	public final static int MAX_ITERATION_DEFAULT = 1000;

	
	/**
	 * Particle number.
	 */
	public final static String PARTICLE_NUMBER_FIELD = "particle_number";
	
	
	/**
	 * Default value for particle number.
	 */
	public final static int PARTICLE_NUMBER_DEFAULT = 50;

	
	/**
	 * Lower bound of position.
	 */
	public final static String POSITION_LOWER_BOUND_FIELD = "position_hint_lower";
	
	
	/**
	 * Default value for lower bound of position.
	 */
	public final static double POSITION_LOWER_BOUND_DEFAULT = 0;


	/**
	 * Upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_FIELD = "position_hint_upper";
	
	
	/**
	 * Default value for upper bound of position.
	 */
	public final static double POSITION_UPPER_BOUND_DEFAULT = 1.0;
	
	
	/**
	 * Terminated threshold.
	 */
	public final static String TERMINATED_THRESHOLD_FIELD = "terminated_threshold";

	
	/**
	 * Default value for terminated threshold .
	 */
	public final static double TERMINATED_THRESHOLD_DEFAULT = 0.001;

	
	/**
	 * Phi 1 parameter.
	 */
	public final static String PHI1_FIELD = "phi1";

	
	/**
	 * Default value for Phi 1 parameter.
	 */
	public final static double PHI1_DEFAULT = 0.5;

	
	/**
	 * Phi 2 parameter.
	 */
	public final static String PHI2_FIELD = "phi2";

	
	/**
	 * Default value for Phi 2 parameter.
	 */
	public final static double PHI2_DEFAULT = 0.5;

	
	/**
	 * Chi parameter.
	 */
	public final static String CHI_FIELD = "chi";

	
	/**
	 * Default value for Chi parameter.
	 */
	public final static double CHI_DEFAULT = 0.5;

	
	/**
	 * Target function or cost function.
	 */
	protected Function func = null;
	
	
	/**
	 * Internal swarm contains particles.
	 */
	protected List<Particle> swarm = Util.newList();
	
	
	/**
	 * Swarm's best particle.
	 */
	protected Particle swarmBest = null;
	
	
	/**
	 * Default constructor.
	 */
	public PSO() {

	}

	
	@Override
	public synchronized Object learnStart(Object... info) throws RemoteException {
		swarm.clear();
		if (func == null) return (swarmBest = null);
		
		int N = config.getAsInt(PARTICLE_NUMBER_FIELD);
		N = N > 0 ? N : PARTICLE_NUMBER_DEFAULT;
		double lower = config.getAsReal(POSITION_LOWER_BOUND_FIELD);
		lower = Util.isUsed(lower) ? lower : POSITION_LOWER_BOUND_DEFAULT;
		double upper = config.getAsReal(POSITION_UPPER_BOUND_FIELD);
		upper = Util.isUsed(upper) ? upper : POSITION_UPPER_BOUND_DEFAULT;
		
		swarmBest = null;
		int dim = func.getVarNum();
		double minValue = Constants.UNUSED;
		for (int i = 0; i < N; i++) {
			Vector position = makeRandom(dim, lower, upper);
			double value = func.evaluate(position);
			if (!Util.isUsed(value)) continue;
			
			Vector velocity = makeRandom(dim, -Math.abs(upper - lower), Math.abs(upper - lower));
			Particle x = new Particle(position, velocity);
			x.bestPosition = position;
			x.bestValue = value;
			swarm.add(x);
			
			if (!Util.isUsed(minValue) || value < minValue)
				swarmBest = x;
		}
		if (swarm.size() == 0) return (swarmBest = null);

		int maxIteration = config.getAsInt(MAX_ITERATION_FIELD);
		maxIteration = maxIteration < 0 ? 0 : maxIteration;  
		double terminatedThreshold = config.getAsReal(TERMINATED_THRESHOLD_FIELD);
		terminatedThreshold = Util.isUsed(terminatedThreshold) && terminatedThreshold >= 0 ? terminatedThreshold : TERMINATED_THRESHOLD_DEFAULT;
		double phi1 = config.getAsReal(PHI1_FIELD);
		phi1 = Util.isUsed(phi1) && phi1 > 0 ? phi1 : PHI1_DEFAULT;
		double phi2 = config.getAsReal(PHI2_FIELD);
		phi2 = Util.isUsed(phi2) && phi2 > 0 ? phi1 : PHI2_DEFAULT;
		double chi = config.getAsReal(CHI_FIELD);
		chi = Util.isUsed(chi) && chi > 0 ? chi : CHI_DEFAULT;
		
		int iteration = 0;
		double preBestValue = swarmBest.bestValue;
		while (true) {
			for (Particle x : swarm) {
				Vector u1 = makeRandom(dim, 0, phi1);
				x.velocity = x.velocity.add(wiseProduct(u1, x.bestPosition.subtract(x.position)));
				
				List<Particle> neighbors = defineNeighbors(x);
				if (neighbors == null || neighbors.size() == 0) {
					Vector u2 = makeRandom(dim, 0, phi2);
					x.velocity = x.velocity.add(wiseProduct(u2, swarmBest.bestPosition.subtract(x.position)));
				}
				else {
					Vector sum = new Vector(dim, 0);
					int K = neighbors.size();
					for (int k = 0; k < K; k++) {
						Vector u2 = makeRandom(dim, 0, phi2);
						sum = sum.add(wiseProduct(u2, neighbors.get(k).bestPosition.subtract(x.position)));
					}
					x.velocity = x.velocity.add(sum.multiply(1.0 / (double)K));
				}
				
				x.velocity = x.velocity.multiply(chi);
				x.position = x.position.add(x.velocity);
				
				if (!Util.isUsed(x.bestValue)) {
					x.bestPosition = x.position;
					x.bestValue = func.evaluate(x.bestPosition);
				}
				if (!Util.isUsed(x.bestValue)) continue;
				
				double value = func.evaluate(x.position);
				if (value < x.bestValue) {
					x.bestPosition = x.position;
					x.bestValue = value;
					
					if (!Util.isUsed(swarmBest.bestValue) || x.bestValue < swarmBest.bestValue) {
						swarmBest.bestPosition = swarmBest.position = x.bestPosition;
						preBestValue = swarmBest.bestValue;
						swarmBest.bestValue = x.bestValue; 
					}
				}
			}
			
			iteration ++;
			
			if (Util.isUsed(preBestValue)) {
				boolean satisfied = Math.abs(swarmBest.bestValue - preBestValue) <= terminatedThreshold * Math.abs(preBestValue);
				if (satisfied) break;
			}
			
			if (maxIteration > 0 && iteration >= maxIteration)
				break;
		}
		
		return swarmBest;
	}


	@Override
	public Object execute(Object input) throws RemoteException {
		return null;
	}

	
	@Override
	public Object getParameter() throws RemoteException {
		return swarmBest;
	}

	
	/**
	 * Setting target function (cost function).
	 * @param func target function (cost function).
	 */
	public void setFunction(Function func) {
		this.func = func;
	}
	
	
	/**
	 * Defining neighbors of a given particle.
	 * @param particle given particle.
	 * @return list of neighbors of the given particle. Returning empty list in case of fully connected swarm topology.
	 */
	public List<Particle> defineNeighbors(Particle particle) {
		return Util.newList();
	}
	
	
	@Override
	public String parameterToShownText(Object parameter, Object... info) throws RemoteException {
		if (swarmBest == null)
			return "";
		else
			return swarmBest.toString();
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Particle swarm optimization (PSO) algorithm";
	}

	
	@Override
	public String getName() {
		return "pso";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(FUNC_EXPR_FIELD, FUNC_EXPR_DEFAULT);
		config.put(MAX_ITERATION_FIELD, MAX_ITERATION_DEFAULT);
		config.put(PARTICLE_NUMBER_FIELD, PARTICLE_NUMBER_DEFAULT);
		config.put(POSITION_LOWER_BOUND_FIELD, POSITION_LOWER_BOUND_DEFAULT);
		config.put(POSITION_UPPER_BOUND_FIELD, POSITION_UPPER_BOUND_DEFAULT);
		config.put(TERMINATED_THRESHOLD_FIELD, TERMINATED_THRESHOLD_DEFAULT);
		config.put(PHI1_FIELD, PHI1_DEFAULT);
		config.put(PHI2_FIELD, PHI2_DEFAULT);
		config.put(CHI_FIELD, CHI_DEFAULT);
		
		return config;
	}

	
	/**
	 * This class represents particle.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static class Particle implements Serializable, Cloneable {

		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Position of particle.
		 */
		public Vector position = null;
		
		/**
		 * Velocity of particle.
		 */
		public Vector velocity = null;
		
		/**
		 * Best position of particle.
		 */
		public Vector bestPosition = null;

		/**
		 * Best evaluated value of the best position.
		 */
		public double bestValue = Constants.UNUSED;
		
		/**
		 * Constructor with specified dimension and initial value.
		 * @param dim specified dimension.
		 * @param initialValue initial value.
		 */
		public Particle(int dim, double initialValue) {
			this.position = new Vector(dim, initialValue);
			this.velocity = new Vector(dim, initialValue);
			this.bestPosition = this.position;
		}
		
		/**
		 * Constructor with specified position and velocity.
		 * @param position specified position.
		 * @param velocity specified velocity.
		 */
		public Particle(Vector position, Vector velocity) {
			this.position = position;
			this.velocity = velocity;
			this.bestPosition = this.position;
		}
		
		/**
		 * Making random particle in range [0, 1].
		 * @param dim dimension.
		 * @return random particle in range [0, 1].
		 */
		public static Particle makeRandom(int dim) {
			Random rnd = new Random();
			double[] position = new double[dim];
			double[] velocity = new double[dim];
			
			for (int i = 0; i < dim; i++) {
				position[i] = rnd.nextDouble();
				velocity[i] = rnd.nextDouble();
			}
			
			return new Particle(new Vector(position), new Vector(velocity));
		}
		
	}
	
	
	/**
	 * Making random vector from low bound to high bound.
	 * @param dim dimension.
	 * @param lower low bound.
	 * @param upper high bound.
	 * @return random vector from low bound to high bound.
	 */
	protected static Vector makeRandom(int dim, double lower, double upper) {
		Random rnd = new Random();
		double[] x = new double[dim];
		
		for (int i = 0; i < dim; i++) {
			x[i] = (upper - lower) * rnd.nextDouble() + lower;
		}
		
		return new Vector(x);
	}

	
	/**
	 * Wise-component multiplication of two vectors.
	 * @param v1 the first vector.
	 * @param v2 the second vector.
	 * @return vector resulted from wise-component multiplication of the two vectors.
	 */
	protected static Vector wiseProduct(Vector v1, Vector v2) {
		if (v1 == null || v2 == null) return null;
		
		int dim = Math.min(v1.dim(), v2.dim());
		double[] x = new double[dim];
		
		for (int i = 0; i < dim; i++) {
			x[i] = v1.get(i) * v2.get(i);
		}
		
		return new Vector(x);
	}
	

}
