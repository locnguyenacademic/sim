/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * <code>PSO</code> is the most abstract interface for all particle swarm optimization (PSO) algorithms.
 * 
 * @param <T> data type
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface PSO<T> extends Remote, Serializable, Cloneable, AutoCloseable {

	
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
	public final static String FUNC_EXPR_DEFAULT = "(x1 + x2)^2";

	
	/**
	 * Function variable names.
	 */
	public final static String FUNC_VARNAMES_FIELD = "function_variables";
	
	
	/**
	 * Default value for variable names.
	 */
	public final static String FUNC_VARNAMES_DEFAULT = "x1, x2";

	
	/**
	 * Learning the PSO algorithm based on specified setting and mathematical expression.
	 * @param setting specified setting. It can be null.
	 * @param funcExpr mathematical expression to represent a function. It can be null.
	 * @return target function.
	 * @throws RemoteException if any error raises.
	 */
	Object learn(PSOSetting<T> setting, String funcExpr) throws RemoteException;

		
	/**
	 * Getting target function (cost function).
	 * @return target function (cost function).
	 * @throws RemoteException if any error raises.
	 */
	Function<T> getFunction() throws RemoteException;

	
	/**
	 * Setting target function (cost function).
	 * @param func target function (cost function).
	 * @throws RemoteException if any error raises.
	 */
	void setFunction(Function<T> func) throws RemoteException;

	
	/**
	 * Setting target function (cost function) with mathematical expression.
	 * @param varNames variable names.
	 * @param funcExpr mathematical expression of target function (cost function).
	 * @throws RemoteException if any error raises.
	 */
	void setFunction(List<String> varNames, String funcExpr) throws RemoteException;

	
	/**
	 * Getting PSO setting.
	 * @return PSO setting.
	 * @throws RemoteException if any error raises.
	 */
	PSOSetting<T> getPSOSetting() throws RemoteException;
	
	
	/**
	 * Setting PSO setting.
	 * @param setting PSO setting.
	 * @throws RemoteException if any error raises.
	 */
	void setPSOSetting(PSOSetting<T> setting) throws RemoteException;
	
	
	/**
	 * Adding listener.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
	void addListener(PSOListener listener) throws RemoteException;

	
	/**
	 * Removing listener.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeListener(PSOListener listener) throws RemoteException;


	/**
	 * Getting configuration of this network.
	 * @return configuration of this network.
	 * @throws RemoteException if any error raises.
	 */
    PSOConfig getConfig() throws RemoteException;

	
	/**
	 * Setting configuration of this network.
	 * @param config specified configuration.
	 * @throws RemoteException if any error raises.
	 */
	void setConfig(PSOConfig config) throws RemoteException;
	
	
	/**
	 * Pause doing.
	 * @return true if pausing is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean doPause() throws RemoteException;


	/**
	 * Resume doing.
	 * @return true if resuming is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean doResume() throws RemoteException;


	/**
	 * Stop doing.
	 * @return true if stopping is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean doStop() throws RemoteException;

	/**
	 * Checking whether in doing mode.
	 * @return whether in doing mode.
	 * @throws RemoteException if any error raises.
	 */
	boolean isDoStarted() throws RemoteException;


	/**
	 * Checking whether in paused mode.
	 * @return whether in paused mode.
	 * @throws RemoteException if any error raises.
	 */
	boolean isDoPaused() throws RemoteException;


	/**
	 * Checking whether in running mode.
	 * @return whether in running mode.
	 * @throws RemoteException if any error raises.
	 */
	boolean isDoRunning() throws RemoteException;

	
	/**
     * Exporting this network.
     * @param serverPort server port. Using port 0 if not concerning registry or naming.
     * @return stub as remote object. Return null if exporting fails.
     * @throws RemoteException if any error raises.
     */
    Remote export(int serverPort) throws RemoteException;
    
    
    /**
     * Unexporting this network.
     * @throws RemoteException if any error raises.
     */
    void unexport() throws RemoteException;

    
	@Override
    void close() throws Exception;


}
