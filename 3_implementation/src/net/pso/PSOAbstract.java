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
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class implements partially the particle swarm optimization (PSO) algorithm.
 * 
 * @param <T> type of evaluated object.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class PSOAbstract<T> extends NonexecutableAlgAbstract implements PSO, PSORemote, AllowNullTrainingSet {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Minimization mode.
	 */
	public final static String MINIMIZE_MODE_FIELD = "minimize_mode";
	
	
	/**
	 * Default value for minimization mode.
	 */
	public final static boolean MINIMIZE_MODE_DEFAULT = true;

	
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
	public final static String PARTICLE_NUMBER_FIELD = "pso_particle_number";
	
	
	/**
	 * Default value for particle number.
	 */
	public final static int PARTICLE_NUMBER_DEFAULT = 50;

	
	/**
	 * Terminated threshold.
	 */
	public final static String TERMINATED_THRESHOLD_FIELD = "terminated_threshold";

	
	/**
	 * Default value for terminated threshold .
	 */
	public final static double TERMINATED_THRESHOLD_DEFAULT = 0.001;
	
	
	/**
	 * Terminated ratio mode.
	 */
	public final static String TERMINATED_RATIO_MODE_FIELD = "terminated_ratio_mode";

	
	/**
	 * Default value for terminated ratio mode.
	 */
	public final static boolean TERMINATED_RATIO_MODE_DEFAULT = false;
	
	
	/**
	 * Lower bound of position.
	 */
	public final static String POSITION_LOWER_BOUND_FIELD = "pso_position_lower_bound";
	
	
	/**
	 * Upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_FIELD = "pso_position_upper_bound";
	
	
	/**
	 * Cognitive weight.
	 */
	public final static String COGNITIVE_WEIGHT_FIELD = "pso_cognitive_weight";

	
	/**
	 * Global social weight.
	 */
	public final static String SOCIAL_WEIGHT_GLOBAL_FIELD = "pso_social_weight_global";

	
	/**
	 * Global social weight.
	 */
	public final static String SOCIAL_WEIGHT_LOCAL_FIELD = "pso_social_weight_local";

	
	/**
	 * Inertial weight.
	 */
	public final static String INERTIAL_WEIGHT_FIELD = "pso_inertial_weight";

	
	/**
	 * Constriction weight.
	 */
	public final static String CONSTRICT_WEIGHT_FIELD = "pso_constrict_weight";

	
	/**
	 * Target function or cost function.
	 */
	protected Function<T> func = null;
	
	
	/**
	 * Internal swarm contains particles.
	 */
	protected List<Particle<T>> swarm = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public PSOAbstract() {

	}

	
	@Override
	public void setup() throws RemoteException {
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
	public void setup(Function<T> func) {
		try {
			setFunction(func);
			setup();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * New setting up method with mathematical expression of function.
	 * @param varNames variable names.
	 * @param funcExpr mathematical expression of function.
	 */
	public void setup(List<String> varNames, String funcExpr) {
		config.put(FUNC_EXPR_FIELD, funcExpr);
		this.config.put(FUNC_VARNAMES_FIELD, TextParserUtil.toText(varNames, ","));

		try {
			setup();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	@Override
	public Object learnStart(Object... info) throws RemoteException {
		if (isLearnStarted()) return null;

		swarm.clear();
		
		String expr = config.getAsString(FUNC_EXPR_FIELD);
		if (expr != null && !expr.trim().isEmpty()) {
			List<String> varNames = extractVarNames();
			func = defineExprFunction(varNames, expr);
		}
		if (func == null) return func;
		
		func.setOptimizer(null);
		
		@SuppressWarnings("unchecked")
		PSOConfiguration<T> psoConfig = (PSOConfiguration<T>) getPSOConfiguration();
		
		int N = config.getAsInt(PARTICLE_NUMBER_FIELD);
		N = N > 0 ? N : PARTICLE_NUMBER_DEFAULT;
		T[] lower = psoConfig.lower;
		T[] upper = psoConfig.upper;
		
		Optimizer<T> optimizer = null;
		for (int i = 0; i < N; i++) {
			Particle<T> x = func.createRandomParticle(lower, upper);
			if (x == null || !x.isValid())
				continue;
			
			swarm.add(x);
			
			if (optimizer == null || checkABetterThanB(x.bestValue, optimizer.bestValue))
				optimizer = Optimizer.extract(x, func);
		}
		if (swarm.size() == 0 || optimizer == null) return (func = null);

		int maxIteration = config.getAsInt(MAX_ITERATION_FIELD);
		maxIteration = maxIteration < 0 ? 0 : maxIteration;  
		T cognitiveWeight = psoConfig.cognitiveWeight;
		T socialWeightGlobal = psoConfig.socialWeightGlobal;
		T socialWeightLocal = psoConfig.socialWeightLocal;
		T inertialWeight = psoConfig.inertialWeight;
		T constrictWeight = psoConfig.constrictWeight;
		
		int iteration = 0;
		Optimizer<T> preOptimizer = null;
		learnStarted = true;
		while (learnStarted && (maxIteration <= 0 || iteration < maxIteration)) {
			for (Particle<T> x : swarm) {
				x.velocity.multiply(inertialWeight);
				
				Vector<T> cognitiveForce = func.createRandomVector(func.zero(), cognitiveWeight).multiplyWise(
					x.bestPosition.duplicate().subtract(x.position));
				x.velocity.add(cognitiveForce);
				
				Vector<T> socialForceGlobal = func.createRandomVector(func.zero(), socialWeightGlobal).multiplyWise(
					optimizer.bestPosition.duplicate().subtract(x.position));
				x.velocity.add(socialForceGlobal);

				List<Particle<T>> neighbors = defineNeighbors(x);
				if (neighbors != null && neighbors.size() > 0) {
					Vector<T> socialForceLocal = func.createVector(func.zero());
					List<Vector<T>> neighborForces = Util.newList(neighbors.size());
					for (Particle<T> neighbor : neighbors) {
						Vector<T> neighborForce = func.createRandomVector(func.zero(), socialWeightLocal).multiplyWise(
							neighbor.bestPosition.duplicate().subtract(x.position));
						neighborForces.add(neighborForce);
					}
					socialForceLocal.mean(neighborForces);
					
					x.velocity.add(socialForceLocal);
				}
				
				x.velocity.multiply(constrictWeight);
				x.position.add(x.velocity);
				
				T value = func.eval(x.position);
				if (value == null)
					continue;
				else if (checkABetterThanB(value, x.bestValue)) {
					x.bestPosition = x.position.duplicate();
					x.bestValue = value;
					
					if (checkABetterThanB(x.bestValue, optimizer.bestValue)) {
						preOptimizer = optimizer;
						optimizer = Optimizer.extract(x);
					}
				}
			}
			
			iteration ++;
			
			fireSetupEvent(new PSOLearnEvent(this, Type.doing, getName(),
					"At iteration " + iteration + ": optimizer is " + optimizer.toString(),
					iteration, maxIteration));
			
			if (terminatedCondition(optimizer, preOptimizer))
				learnStarted = false;
			
			synchronized (this) {
				while (learnPaused) {
					notifyAll();
					try {
						wait();
					} catch (Exception e) {LogUtil.trace(e);}
				}
			}

		}
		
		func.setOptimizer(optimizer);
		
		synchronized (this) {
			learnStarted = false;
			learnPaused = false;
			
			fireSetupEvent(new PSOLearnEvent(this, Type.done, getName(),
				"At final iteration " + iteration + ": final optimizer is " + optimizer.toString(),
				iteration, maxIteration));

			notifyAll();
		}

		return func;
	}


	/**
	 * Setting the terminated condition.
	 * @param curOptimizer current optimizer.
	 * @param preOptimizer previous optimizer.
	 * @return true then the algorithm can stop.
	 */
	protected boolean terminatedCondition(Optimizer<T> curOptimizer, Optimizer<T> preOptimizer) {
		if (curOptimizer == null || preOptimizer == null) return false;
		
		double terminatedThreshold = config.getAsReal(TERMINATED_THRESHOLD_FIELD);
		terminatedThreshold = Util.isUsed(terminatedThreshold) && terminatedThreshold >= 0 ? terminatedThreshold : TERMINATED_THRESHOLD_DEFAULT;
		boolean terminatedRatio = config.getAsBoolean(TERMINATED_RATIO_MODE_FIELD);
		if (terminatedRatio)
			return func.distance(curOptimizer.bestValue, preOptimizer.bestValue) <= terminatedThreshold * func.distance(preOptimizer.bestValue);
		else
			return func.distance(curOptimizer.bestValue, preOptimizer.bestValue) <= terminatedThreshold;
	}
	
	
	/**
	 * Checking if evaluated value A is better than evaluated value B.
	 * @param evalA evaluated value A.
	 * @param evalB evaluated value B.
	 * @return true if evaluated value A is better than evaluated value B.
	 */
	protected boolean checkABetterThanB(T evalA, T evalB) {
		if (func == null) return false;
		
		boolean minimize = config.getAsBoolean(MINIMIZE_MODE_FIELD);
		if (minimize)
			return func.compareTo(evalA, evalB) == -1;
		else
			return func.compareTo(evalA, evalB) == 1;
	}

	
	/**
	 * Defining neighbors of a given particle.
	 * @param particle given particle.
	 * @return list of neighbors of the given particle. Returning empty list in case of fully connected swarm topology.
	 */
	protected List<Particle<T>> defineNeighbors(Particle<T> particle) {
		return Util.newList();
	}
	
	
	/**
	 * Defining mathematical expression function.
	 * @param varNames variable names.
	 * @param expr mathematical expression.
	 * @return mathematical expression function.
	 */
	protected abstract Function<T> defineExprFunction(List<String> varNames, String expr);
	
	
	@Override
	public Function<?> getFunction() throws RemoteException {
		return func;
	}
	
	
	/**
	 * Setting target function (cost function).
	 * @param func target function (cost function).
	 * @throws RemoteException if any error raises.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void setFunction(Function<?> func) throws RemoteException {
		this.func = (Function<T>) func;
		
		if (func != null) {
			this.config.put(FUNC_EXPR_FIELD, "");
			this.config.put(FUNC_VARNAMES_FIELD, "");
		}
	}
	
	
	@Override
	public synchronized void setFunction(List<String> varNames, String funcExpr) throws RemoteException {
		if (varNames == null || funcExpr == null) return;
		if (varNames.size() == 0 || funcExpr.trim().isEmpty()) return;
		
		Function<T> exprFunc = defineExprFunction(varNames, funcExpr);
		if (exprFunc == null) return;
		
		this.func = exprFunc;
		this.config.put(FUNC_EXPR_FIELD, funcExpr);
		this.config.put(FUNC_VARNAMES_FIELD, TextParserUtil.toText(varNames, ","));
	}

	
	@Override
	public Object getParameter() throws RemoteException {
		return func;
	}

	
	@Override
	public String parameterToShownText(Object parameter, Object... info) throws RemoteException {
		if (parameter == null)
			return "";
		else if (parameter instanceof Function<?>)
			return func.toString();
		else
			return "";
	}

	
	@Override
	public synchronized String getDescription() throws RemoteException {
		return parameterToShownText(getParameter());
	}

	
	@Override
	public Inspector getInspector() {
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new DescriptionDlg(UIUtil.getFrameForComponent(null), "Inspector", desc);
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {PSORemote.class.getName()};
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MINIMIZE_MODE_FIELD, MINIMIZE_MODE_DEFAULT);
		config.put(FUNC_EXPR_FIELD, FUNC_EXPR_DEFAULT);
		config.put(FUNC_VARNAMES_FIELD, FUNC_VARNAMES_DEFAULT);
		config.put(MAX_ITERATION_FIELD, MAX_ITERATION_DEFAULT);
		config.put(PARTICLE_NUMBER_FIELD, PARTICLE_NUMBER_DEFAULT);
		config.put(TERMINATED_THRESHOLD_FIELD, TERMINATED_THRESHOLD_DEFAULT);
		config.put(TERMINATED_RATIO_MODE_FIELD, TERMINATED_RATIO_MODE_DEFAULT);
		
		return config;
	}


	/**
	 * Extracting variable names.
	 * @return variable names.
	 */
	private List<String> extractVarNames() {
		return extractNames(FUNC_VARNAMES_FIELD);
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
