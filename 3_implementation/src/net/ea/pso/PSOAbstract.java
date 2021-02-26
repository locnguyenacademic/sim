/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import net.ea.pso.PSODoEvent.Type;

/**
 * This class implements partially the particle swarm optimization (PSO) algorithm.
 * 
 * @param <T> type of evaluated object.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class PSOAbstract<T> implements PSO<T> {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Maximum iteration.
	 */
	public final static String MAX_ITERATION_FIELD = "terminate_max_iteration";
	
	
	/**
	 * Default value for maximum iteration.
	 */
	public final static int MAX_ITERATION_DEFAULT = 1000;

	
	/**
	 * Terminated threshold.
	 */
	public final static String TERMINATED_THRESHOLD_FIELD = "terminate_threshold";

	
	/**
	 * Default value for terminated threshold .
	 */
	public final static double TERMINATED_THRESHOLD_DEFAULT = 0.001;
	
	
	/**
	 * Terminated ratio mode.
	 */
	public final static String TERMINATED_RATIO_MODE_FIELD = "terminate_ratio_mode";

	
	/**
	 * Default value for terminated ratio mode.
	 */
	public final static boolean TERMINATED_RATIO_MODE_DEFAULT = false;

	
	/**
	 * Target function or cost function.
	 */
	protected Function<T> func = null;
	
	
	/**
	 * Internal swarm contains particles.
	 */
	protected List<Particle<T>> swarm = Util.newList(0);
	
	
	/**
	 * Holding a list of listeners.
	 */
    protected transient PSOListenerList listenerList = new PSOListenerList();

    
    /**
     * Flag to indicate whether algorithm learning process was started.
     */
    protected volatile boolean doStarted = false;
    
    
    /**
     * Flag to indicate whether algorithm learning process was paused.
     */
    protected volatile boolean doPaused = false;

    
	/**
	 * Internal configuration.
	 */
	protected PSOConfig config = new PSOConfig();
	
	
	/**
	 * Flag to indicate whether this PSO was exported.
	 */
	protected boolean exported = false;

	
	/**
	 * Default constructor.
	 */
	public PSOAbstract() {
		config.put(MINIMIZE_MODE_FIELD, MINIMIZE_MODE_DEFAULT);
		config.put(FUNC_EXPR_FIELD, FUNC_EXPR_DEFAULT);
		config.put(FUNC_VARNAMES_FIELD, FUNC_VARNAMES_DEFAULT);
		config.put(MAX_ITERATION_FIELD, MAX_ITERATION_DEFAULT);
		config.put(PSOSetting.PARTICLE_NUMBER_FIELD, PSOSetting.PARTICLE_NUMBER_DEFAULT);
	}

	
	@Override
	public Object learn(PSOSetting<T> setting, String funcExpr) throws RemoteException {
		if (isDoStarted()) return null;
		
		swarm.clear();
		
		String expr = funcExpr != null ? funcExpr.trim() : null;
		expr = expr != null ? expr : config.getAsString(FUNC_EXPR_FIELD);
		expr = expr != null ? expr.trim() : null;
		if (expr != null) {
			List<String> varNames = extractVarNames();
			func = defineExprFunction(varNames, expr);
		}
		if (func == null) return func;
		
		if (setting == null) setting = getPSOSetting();

		func.setOptimizer(null);
		
		int N = config.getAsInt(PSOSetting.PARTICLE_NUMBER_FIELD);
		N = N > 0 ? N : PSOSetting.PARTICLE_NUMBER_DEFAULT;
		T[] lower = setting.lower;
		T[] upper = setting.upper;
		
		Optimizer<T> optimizer = null;
		N = 2*N; //Solving the problem of invalid randomization.
		for (int i = 0; i < N; i++) {
			Particle<T> x = func.createRandomParticle(lower, upper);
			if (x == null || !x.isValid())
				continue;
			
			swarm.add(x);
			
			if (optimizer == null || checkABetterThanB(x.bestValue, optimizer.bestValue))
				optimizer = Optimizer.extract(x, func);
			
			if (swarm.size() >= N && optimizer != null) break;
		}
		if (swarm.size() == 0 || optimizer == null) return (func = null);

		int maxIteration = config.getAsInt(MAX_ITERATION_FIELD);
		maxIteration = maxIteration < 0 ? 0 : maxIteration;  
		T cognitiveWeight = setting.cognitiveWeight;
		T socialWeightGlobal = setting.socialWeightGlobal;
		T socialWeightLocal = setting.socialWeightLocal;
		Vector<T> inertialWeight = setting.inertialWeight;
		Vector<T> constrictWeight = setting.constrictWeight;
		
		T elementZero = func.zero().elementZero();
		int iteration = 0;
		Optimizer<T> preOptimizer = null;
		doStarted = true;
		while (doStarted && (maxIteration <= 0 || iteration < maxIteration)) {
			for (Particle<T> x : swarm) {
				Vector<T> inertialWeightCustom = customizeInertialWeight(x, optimizer);
				if (inertialWeightCustom != null && inertialWeightCustom.getAttCount() > 0)
					x.velocity.multiplyWise(inertialWeightCustom);
				else
					x.velocity.multiplyWise(inertialWeight);
				
				Vector<T> cognitiveForce = func.createRandomVector(elementZero, cognitiveWeight).multiplyWise(
					x.bestPosition.duplicate().subtract(x.position));
				x.velocity.add(cognitiveForce);
				
				Vector<T> socialForceGlobal = func.createRandomVector(elementZero, socialWeightGlobal).multiplyWise(
					optimizer.bestPosition.duplicate().subtract(x.position));
				x.velocity.add(socialForceGlobal);

				List<Particle<T>> neighbors = defineNeighbors(x);
				if (neighbors != null && neighbors.size() > 0) {
					Vector<T> socialForceLocal = func.createVector(elementZero);
					List<Vector<T>> neighborForces = Util.newList(neighbors.size());
					for (Particle<T> neighbor : neighbors) {
						Vector<T> neighborForce = func.createRandomVector(elementZero, socialWeightLocal).multiplyWise(
							neighbor.bestPosition.duplicate().subtract(x.position));
						neighborForces.add(neighborForce);
					}
					socialForceLocal.mean(neighborForces);
					
					x.velocity.add(socialForceLocal);
				}
				
				Vector<T> constrictWeightCustom = customizeConstrictWeight(x, optimizer);
				if (constrictWeightCustom != null && constrictWeightCustom.getAttCount() > 0)
					x.velocity.multiplyWise(constrictWeightCustom);
				else
					x.velocity.multiplyWise(constrictWeight);
				x.position.add(x.velocity);
				
				x.value = func.eval(x.position);
				if (!x.position.isValid(x.value))
					continue;
				else if (checkABetterThanB(x.value, x.bestValue)) {
					x.bestPosition = x.position.duplicate();
					x.bestValue = x.value;
					
					if (checkABetterThanB(x.bestValue, optimizer.bestValue)) {
						preOptimizer = optimizer;
						optimizer = Optimizer.extract(x);
					}
				}
			}
			
			iteration ++;
			
			fireDoEvent(new PSODoEventImpl(this, Type.doing, "pso",
					"At iteration " + iteration + ": optimizer is " + optimizer.toString(),
					iteration, maxIteration));
			
			if (terminatedCondition(optimizer, preOptimizer))
				doStarted = false;
			
			synchronized (this) {
				while (doPaused) {
					notifyAll();
					try {
						wait();
					} catch (Exception e) {Util.trace(e);}
				}
			}

		}
		
		func.setOptimizer(optimizer);
		
		synchronized (this) {
			doStarted = false;
			doPaused = false;
			
			fireDoEvent(new PSODoEventImpl(this, Type.done, "pso",
				"At final iteration " + iteration + ": final optimizer is " + optimizer.toString(),
				iteration, iteration));

			notifyAll();
		}

		return func;
	}
	
	
	/**
	 * Checking whether the terminated condition is satisfied.
	 * @param curOptimizer current optimizer.
	 * @param preOptimizer previous optimizer.
	 * @return true then the algorithm can stop.
	 */
	protected abstract boolean terminatedCondition(Optimizer<T> curOptimizer, Optimizer<T> preOptimizer);
	
	
	/**
	 * Checking if value a is better than value b.
	 * @param a value a.
	 * @param b value b.
	 * @return true if value a is better than value b.
	 */
	protected abstract boolean checkABetterThanB(T a, T b);

	
	/**
	 * Defining neighbors of a given particle.
	 * @param targetParticle given particle.
	 * @return list of neighbors of the given particle. Returning empty list in case of fully connected swarm topology.
	 */
	protected List<Particle<T>> defineNeighbors(Particle<T> targetParticle) {
		return Util.newList(0);
	}
	
	
	/**
	 * Defining mathematical expression function.
	 * @param varNames variable names.
	 * @param expr mathematical expression.
	 * @return mathematical expression function.
	 */
	protected abstract Function<T> defineExprFunction(List<String> varNames, String expr);
	
	
	/**
	 * Customizing inertial weight vector.
	 * @param targetParticle target particle.
	 * @param optimizer specified optimizer.
	 * @return customized inertial weight vector given target particle and optimizer. Return null if there is no customized constriction weight.
	 */
	protected Vector<T> customizeInertialWeight(Particle<T> targetParticle, Optimizer<T> optimizer) {
		return null;
	}
	

	/**
	 * Customizing constriction weight vector.
	 * @param targetParticle target particle.
	 * @param optimizer specified optimizer.
	 * @return customized constriction weight vector given target particle and optimizer. Return null if there is no customized constriction weight.
	 */
	protected Vector<T> customizeConstrictWeight(Particle<T> targetParticle, Optimizer<T> optimizer) {
		return null;
	}
	

	@Override
	public Function<T> getFunction() throws RemoteException {
		return func;
	}
	
	
	/**
	 * Setting target function (cost function).
	 * @param func target function (cost function).
	 * @throws RemoteException if any error raises.
	 */
	public synchronized void setFunction(Function<T> func) throws RemoteException {
		this.func = func;
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
		this.config.put(FUNC_VARNAMES_FIELD, Util.toText(varNames, ","));
	}

	
	/**
	 * Extracting variable names.
	 * @return variable names.
	 */
	private List<String> extractVarNames() {
		String names = config.getAsString(FUNC_VARNAMES_FIELD);
		if (names == null)
			return Util.newList(0);
		else
			return Util.parseListByClass(names, String.class, ",");
	}
	
	
	/**
	 * Create functor from profile.
	 * @param profile specified profile.
	 * @return functor created from profile.
	 */
	public abstract Functor<T> createFunctor(Profile profile);

	
	@Override
	public void addListener(PSOListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(PSOListener.class, listener);
		}
	}


	@Override
	public void removeListener(PSOListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(PSOListener.class, listener);
		}
	}
	
	
	/**
	 * Getting an array of listeners.
	 * @return array of listeners.
	 */
	protected PSOListener[] getPSOListeners() {
		if (listenerList == null) return new PSOListener[] {};
		synchronized (listenerList) {
			return listenerList.getListeners(PSOListener.class);
		}

	}
	
	
	/**
	 * Firing information event.
	 * @param evt information event.
	 */
	protected void fireInfoEvent(PSOInfoEvent evt) {
		if (listenerList == null) return;
		
		PSOListener[] listeners = getPSOListeners();
		for (PSOListener listener : listeners) {
			try {
				listener.receivedInfo(evt);
			}
			catch (Throwable e) { 
				Util.trace(e);
			}
		}
	}

	
	/**
	 * Firing learning event.
	 * @param evt learning event.
	 */
	protected void fireDoEvent(PSODoEvent evt) {
		if (listenerList == null) return;
		
		PSOListener[] listeners = getPSOListeners();
		for (PSOListener listener : listeners) {
			try {
				listener.receivedDo(evt);
			}
			catch (Throwable e) {
				Util.trace(e);
			}
		}
	}


	@Override
	public boolean doPause() throws RemoteException {
		if (!isDoRunning()) return false;
		
		doPaused  = true;
		
		try {
			wait();
		} 
		catch (Throwable e) {
			Util.trace(e);
		}
		
		return true;
	}


	@Override
	public boolean doResume() throws RemoteException {
		if (!isDoPaused()) return false;
		
		doPaused = false;
		notifyAll();
		
		return true;
	}


	@Override
	public boolean doStop() throws RemoteException {
		if (!isDoStarted()) return false;
		
		doStarted = false;
		
		if (doPaused) {
			doPaused = false;
			notifyAll();
		}
		
		try {
			wait();
		} 
		catch (Throwable e) {
			Util.trace(e);
		}
		
		return true;
	}


	@Override
	public boolean isDoStarted() throws RemoteException {
		return doStarted;
	}


	@Override
	public boolean isDoPaused() throws RemoteException {
		return doStarted && doPaused;
	}


	@Override
	public boolean isDoRunning() throws RemoteException {
		return doStarted && !doPaused;
	}

	
	@Override
	public PSOConfig getConfig() throws RemoteException {
		return config;
	}


	@Override
	public void setConfig(PSOConfig config) throws RemoteException {
		if (config != null) this.config.putAll(config);
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exported) return null;
		
		Remote stub = null;
		try {
			stub = UnicastRemoteObject.exportObject(this, serverPort);
		}
		catch (Exception e) {
			try {
				if (stub != null) UnicastRemoteObject.unexportObject(this, true);
			}
			catch (Exception e2) {}
			stub = null;
		}
	
		exported = stub != null;
		return stub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (!exported) return;

		try {
        	UnicastRemoteObject.unexportObject(this, true);
			exported = false;
		}
		catch (NoSuchObjectException e) {
			exported = false;
			Util.trace(e);
		}
		catch (Throwable e) {
			Util.trace(e);
		}
	}

	
	@Override
	public void close() throws Exception {
		try {
			unexport();
		}
		catch (Throwable e) {
			Util.trace(e);
		}
	}


	@Override
	public String toString() {
		if (func == null)
			return super.toString();
		else
			return func.toString();
	}


	@Override
	protected void finalize() throws Throwable {
		try {
			close();
		} catch (Throwable e) {}
		
		//super.finalize();
	}


}
