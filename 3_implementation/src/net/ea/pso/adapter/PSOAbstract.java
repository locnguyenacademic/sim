/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso.adapter;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import net.ea.pso.ExprFunction;
import net.ea.pso.Functor;
import net.ea.pso.Optimizer;
import net.ea.pso.PSODoEvent;
import net.ea.pso.PSOInfoEvent;
import net.ea.pso.PSOListener;
import net.ea.pso.PSOSetting;
import net.ea.pso.Vector;
import net.hudup.core.alg.AllowNullTrainingSet;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.ExecuteAsLearnAlgAbstract;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.NullPointer;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class implements partially the particle swarm optimization (PSO) algorithm.
 * 
 * @param <T> data type.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class PSOAbstract<T> extends ExecuteAsLearnAlgAbstract implements PSO, PSORemote, PSOListener, AllowNullTrainingSet, DuplicatableAlg {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal PSO algorithm.
	 */
	protected net.ea.pso.PSOAbstract<T> pso = null;
	
	
	/**
	 * Default constructor.
	 */
	public PSOAbstract() {
		pso = createPSO();
		
		try {
			config.putAll(Util.toConfig(pso.getConfig()));
		} catch (Throwable e) {Util.trace(e);}
		
		try {
			pso.addListener(this);
		} catch (Throwable e) {Util.trace(e);}
	}

	
	@Override
	protected Object fetchSample(Dataset dataset) {
		return dataset != null ? dataset.fetchSample2() : null;
	}


	@Override
	public void setup() throws RemoteException {
		try {
			super.setup(new NullPointer());
		}
		catch (Throwable e) {
			Util.trace(e);
		}
	}
	

	@Override
	public void setup(List<String> varNames, String funcExpr) throws RemoteException {
		config.put(net.ea.pso.PSO.FUNC_EXPR_FIELD, funcExpr);
		config.put(net.ea.pso.PSO.FUNC_VARNAMES_FIELD, Util.toText(varNames, ","));

		try {
			setup();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public Object executeAsLearn(Object input) throws RemoteException {
		pso.getConfig().putAll(Util.transferToPSOConfig(config));

		if (input == null) {
			PSOSetting<T> setting = null;
			String funcExpr = null;
			try {
				net.ea.pso.AttributeList newAttRef = null;
				for (Profile profile : (Collection<Profile>)sample) {
					if (profile == null) continue;
					if (newAttRef == null) newAttRef = Util.extractPSOAttributes(profile);
					
					Functor<T> functor = pso.createFunctor(Util.toPSOProfile(newAttRef, profile));
					if (functor != null && functor.setting != null && functor.func != null) {
						setting = functor.setting;
						if (functor.func instanceof ExprFunction)
							funcExpr = ((ExprFunction)functor.func).getExpr();
						
						break;
					}
				}
			}
			catch (Throwable e) {
				setting = null;
				funcExpr = null;
				Util.trace(e);
			}
			
			return pso.learn(setting, funcExpr);
		}
		
		if (input instanceof Vector<?>) {
			if (pso.getFunction() == null)
				return null;
			else {
				Vector<T> arg = (Vector<T>)input;
				return pso.getFunction().eval(arg);
			}
		}

		if (!(input instanceof Profile)) return null;
		
		Profile profile = (Profile)input;
		Functor<T> functor = pso.createFunctor(Util.toPSOProfile(profile));
		if (functor == null || functor.func == null) return null;
		
		String funcExpr = functor.func instanceof ExprFunction ? ((ExprFunction)functor.func).getExpr() : null;
		Object f = pso.learn(functor.setting, funcExpr);
		if (f == null) return null;
		
		Optimizer<?> optimizer = pso.getFunction().getOptimizer();
		return optimizer != null ? optimizer.toArray() : null;
	}


	/**
	 * Create PSO instance.
	 * @return PSO instance.
	 */
	protected abstract net.ea.pso.PSOAbstract<T> createPSO();
	
	
	@Override
	public Object getParameter() throws RemoteException {
		return pso;
	}

	
	@Override
	public String parameterToShownText(Object parameter, Object... info) throws RemoteException {
		if (parameter == null)
			return "";
		else if (!(parameter instanceof net.ea.pso.PSO))
			return "";
		else
			return ((net.ea.pso.PSO<?>)parameter).toString();
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
		} catch (Exception e) {Util.trace(e);}
		
		return new DescriptionDlg(UIUtil.getDialogForComponent(null), "Inspector", desc);
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {PSORemote.class.getName()};
	}

	
	@Override
	public void receivedInfo(PSOInfoEvent evt) throws RemoteException {

	}

	
	@Override
	public void receivedDo(PSODoEvent evt) throws RemoteException {
		if (evt.getType() == PSODoEvent.Type.doing) {
			fireSetupEvent(new SetupAlgEvent(this, Type.doing, getName(), null,
				evt.getLearnResult(),
				evt.getProgressStep(), evt.getProgressTotalEstimated()));
		}
		else if (evt.getType() == PSODoEvent.Type.done) {
			fireSetupEvent(new SetupAlgEvent(this, Type.done, getName(), null,
					evt.getLearnResult(),
					evt.getProgressStep(), evt.getProgressTotalEstimated()));
		}
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
}
