/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.alg.AllowNullTrainingSet;
import net.hudup.core.alg.NonexecutableAlgAbstract;
import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.NullPointer;
import net.hudup.core.data.ProfileVector;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class implement the particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PSO extends NonexecutableAlgAbstract implements AllowNullTrainingSet {


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
	public final static String FUNC_EXPR_DEFAULT = "(var1 + var2)^2";

	
	/**
	 * Function variable names.
	 */
	public final static String FUNC_VARNAMES_FIELD = "function_variables";
	
	
	/**
	 * Default value for variable names.
	 */
	public final static String FUNC_VARNAMES_DEFAULT = "var1, var2";

	
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
	public final static String POSITION_LOWER_BOUND_FIELD = "position_lower_bound";
	
	
	/**
	 * Default value for lower bound of position.
	 */
	public final static String POSITION_LOWER_BOUND_DEFAULT = "0, 0";


	/**
	 * Upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_FIELD = "position_upper_bound";
	
	
	/**
	 * Default value for upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_DEFAULT = "1, 1";
	
	
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
	 * Default constructor.
	 */
	public PSO() {

	}

	
	/**
	 * New setting up method.
	 */
	public void setup() {
		try {
			super.setup(new NullPointer());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * New setting up method with target function.
	 * @param func target function (cost function).
	 */
	public void setup(Function func) {
		setFunction(func);

		setup();
	}

	
	@Override
	public synchronized Object learnStart(Object... info) throws RemoteException {
		swarm.clear();
		
		String expr = config.getAsString(FUNC_EXPR_FIELD);
		if (expr != null && !expr.trim().isEmpty()) {
			List<String> varNames = extractVarNames();
			func = new ExprFunction(varNames, expr);
		}
		if (func == null) return func;
		
		func.setOptimizer(null);
		
		int N = config.getAsInt(PARTICLE_NUMBER_FIELD);
		N = N > 0 ? N : PARTICLE_NUMBER_DEFAULT;
		double[] lower = extractLowerBound();
		double[] upper = extractUpperBound();
		
		int dim = func.getVarNum();
		Optimizer optimizer = null;
		for (int i = 0; i < N; i++) {
			Particle x = Particle.makeRandom(dim, lower, upper, func);
			if (x == null || !x.isValid())
				continue;
			
			swarm.add(x);
			
			if (optimizer == null || x.bestValue < optimizer.bestValue)
				optimizer = Optimizer.extract(x, func);
		}
		if (swarm.size() == 0 || optimizer == null) return (func = null);

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
		Optimizer preOptimizer = null;
		while (true) {
			for (Particle x : swarm) {
				ProfileVector force1 = Particle.makeRandom(dim, 0, phi1).multiplyWise(
					((ProfileVector)x.bestPosition.clone()).subtract(x.position));
				x.velocity.add(force1);
				
				List<Particle> neighbors = defineNeighbors(x);
				if (neighbors == null || neighbors.size() == 0) {
					ProfileVector force2 = Particle.makeRandom(dim, 0, phi2).multiplyWise(
						((ProfileVector)optimizer.bestPosition.clone()).subtract(x.position));
					x.velocity.add(force2);
				}
				else {
					ProfileVector sum = ProfileVector.createVector(dim, 0);
					int K = neighbors.size();
					for (int k = 0; k < K; k++) {
						ProfileVector force2 = Particle.makeRandom(dim, 0, phi2).multiplyWise(
							((ProfileVector)neighbors.get(k).bestPosition.clone()).subtract(x.position));
						sum.add(force2);
					}
					x.velocity.add(sum.multiply(1.0 / (double)K));
				}
				
				x.velocity.multiply(chi);
				x.position.add(x.velocity);
				
				double value = func.eval(x.position);
				if (!Util.isUsed(value))
					continue;
				else if (value < x.bestValue) {
					x.bestPosition = x.position;
					x.bestValue = value;
					
					if (x.bestValue < optimizer.bestValue) {
						preOptimizer = optimizer;
						optimizer = Optimizer.extract(x);
					}
				}
			}
			
			iteration ++;
			
			fireSetupEvent(new PSOLearnEvent(this, Type.doing, getName(),
					"At iteration " + iteration + ": optimizer is " + optimizer.toString(),
					iteration, maxIteration));
			
			if (preOptimizer != null) {
//				boolean satisfied = Math.abs(optimizer.bestValue - preOptimizer.bestValue) <= terminatedThreshold * Math.abs(preOptimizer.bestValue);
				boolean satisfied = Math.abs(preOptimizer.bestValue - optimizer.bestValue) <= terminatedThreshold;
				if (satisfied) {
					break;
				}
			}
			
			if (maxIteration > 0 && iteration >= maxIteration) {
				break;
			}
		}
		
		func.setOptimizer(optimizer);
		
		fireSetupEvent(new PSOLearnEvent(this, Type.done, getName(),
				"At final iteration " + iteration + ": final optimizer is " + optimizer.toString(),
				iteration, maxIteration));

		return func;
	}


	@Override
	public Object getParameter() throws RemoteException {
		return func;
	}

	
	/**
	 * Setting target function (cost function).
	 * @param func target function (cost function).
	 */
	public synchronized void setFunction(Function func) {
		this.func = func;
		
		if (func != null) {
			this.config.put(FUNC_EXPR_FIELD, "");
			this.config.put(FUNC_VARNAMES_FIELD, "");
		}
	}
	
	
	/**
	 * Defining neighbors of a given particle.
	 * @param particle given particle.
	 * @return list of neighbors of the given particle. Returning empty list in case of fully connected swarm topology.
	 */
	public synchronized List<Particle> defineNeighbors(Particle particle) {
		return Util.newList();
	}
	
	
	@Override
	public String parameterToShownText(Object parameter, Object... info) throws RemoteException {
		if (parameter == null)
			return "";
		else if (parameter instanceof Function)
			return ((Function)func).toString();
		else
			return "";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Particle swarm optimization (PSO) algorithm";
	}

	
	@Override
	public Inspector getInspector() {
		String desc = "";
		try {
			desc = getDescription() + ".\n" + parameterToShownText(getParameter());
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new DescriptionDlg(UIUtil.getFrameForComponent(null), "Inspector", desc);
	}

	
	@Override
	public String getName() {
		return "pso";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(FUNC_EXPR_FIELD, FUNC_EXPR_DEFAULT);
		config.put(FUNC_VARNAMES_FIELD, FUNC_VARNAMES_DEFAULT);
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
	 * Extracting lower bound.
	 * @return extracted lower bound.
	 */
	private double[] extractLowerBound() {
		return extractBound(POSITION_LOWER_BOUND_FIELD);
	}
	

	/**
	 * Extracting lower bound.
	 * @return extracted lower bound.
	 */
	private double[] extractUpperBound() {
		return extractBound(POSITION_UPPER_BOUND_FIELD);
	}


	/**
	 * Extracting variable names.
	 * @return variable names.
	 */
	private List<String> extractVarNames() {
		return extractNames(FUNC_VARNAMES_FIELD);
	}
	
	
	/**
	 * Extracting bound.
	 * @param key key of bound property.
	 * @return extracted bound.
	 */
	private double[] extractBound(String key) {
		try {
			if (!config.containsKey(key))
				return new double[0];
			else
				return DSUtil.toDoubleArray(TextParserUtil.parseListByClass(config.getAsString(key), Double.class, ","));
		}
		catch (Throwable e) {}
		
		return new double[0];
	}


	/**
	 * Extracting names.
	 * @param key key of names property.
	 * @return extracted names.
	 */
	private List<String> extractNames(String key) {
		try {
			if (!config.containsKey(key))
				return Util.newList();
			else
				return TextParserUtil.parseListByClass(config.getAsString(key), String.class, ",");
		}
		catch (Throwable e) {}
		
		return Util.newList();
	}


}
