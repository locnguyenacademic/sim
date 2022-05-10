/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

import net.ml.hmm.HMMDoEvent.Type;

/**
 * This class is the default hidden Markov model {@link HMM}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public class DefaultHMM implements Serializable, Cloneable, AutoCloseable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Variable A represents transition probability matrix.
	 */
	protected List<List<Double>> A = Util.newList(0);

	
	/**
	 * Variable PI represents initial probability matrix.
	 */
	protected List<Double> PI = Util.newList(0);

	
	/**
	 * Variable B represents observation probability distribution.
	 */
	protected List<Distribution> B = Util.newList(0);
	
	
	/**
	 * Optional list of state names
	 */
	protected List<String> S = Util.newList(0);
	
	
	/**
	 * Optional list of observation names
	 */
	protected List<String> OBS = Util.newList(0);

	
	/**
	 * Holding a list of listeners.
	 */
    protected transient HMMListenerList listenerList = new HMMListenerList();

    
    /**
     * Flag to indicate whether algorithm learning process was started.
     */
    protected volatile boolean doStarted = false;
    
    
    /**
     * Flag to indicate whether algorithm learning process was paused.
     */
    protected volatile boolean doPaused = false;

    
    /**
	 * Default constructor
	 */
	protected DefaultHMM() {

	}
	
	
	/**
	 * Constructor with transition probability matrix, initial state probability distribution, observation distributions, state names, and observation names.
	 * @param A transition probability matrix.
	 * @param PI initial state probability distribution.
	 * @param B observation distributions.
	 * @param S state names.
	 * @param OBS observation names.
	 */
	protected DefaultHMM(List<List<Double>> A, List<Double> PI, List<Distribution> B, List<String> S, List<String> OBS) {
		this.A = A;
		this.PI = PI;
		this.B = B;
		this.S = S;
		this.OBS = OBS;
	}
	
	
	/**
	 * Getting name of specified state.
	 * @param state interger as specified state.
	 * @return Name of specified state
	 */
	public List<String> getStateNames(int state) {
		return S;
	}
	
	
	/**
	 * Setting state names.
	 * @param stateNames state names.
	 */
	public void setStateNames(List<String> stateNames) {
		S.clear();
		S.addAll(stateNames);
	}
	
	
	/**
	 * Getting observation names.
	 * @return observation names
	 */
	public List<String> getObsNames() {
		return OBS;
	}
	
	
	/**
	 * Setting observation names.
	 * @param obsNames observation names.
	 */
	public void setObsNames(List<String> obsNames) {
		OBS.clear();
		OBS.addAll(obsNames);
	}

	
	/**
	 * Getting transition probability from state i=0,1,2... to state j=0,1,2....
	 * @param i=0,1,2...
	 * @param j=0,1,2...
	 * @return Transition probability from state i=0,1,2... to state j=0,1,2...
	 */
	public double getA(int i, int j) {
		return A.get(i).get(j);
	}

	
	/**
	 * Assigning transition probability from state i=0,1,2... to state j=0,1,2.... by specified value.
	 * @param i=0,1,2...
	 * @param j=0,1,2...
	 * @param value Specified value for transition probability from state i=0,1,2... to state j=0,1,2....
	 */
	public void setA(int i, int j, double value) {
		A.get(i).set(j, value);
	}

	
	/**
	 * Getting initial probability at state i = 0,1,2....
	 * @param i state i = 0,1,2...
	 * @return Initial probability at state i = 0,1,2....
	 */
	public double getPI(int i) {
		return PI.get(i);
	}

	
	/**
	 * Getting initial probability at state i = 0,1,2....
	 * @param i state i = 0,1,2...
	 * @param value initial probability at state i = 0,1,2....
	 */
	public void setPI(int i, double value) {
		PI.set(i, value);
	}

	
	/**
	 * Getting observation probability at state i and observation x in both discrete case and continuous case.
	 * In discrete case, parameter x is the index of observation.
	 * In continuous case, parameter x is the value of observation.
	 * @param i state i = 0,1,2...
	 * @param x observation. In discrete case, x is the index of observation. In continuous case, x is the value of observation.
	 * @param kComp the kth component in case of mixture model.
	 * @return Observation probability at state i and observation x in both discrete case and continuous case
	 */
	public double getB(int i, Obs x, int kComp) {
		return B.get(i).getProb(x, kComp);
	}
	

	/**
	 * Getting observation probability at state i with specified distribution.
	 * @param i state i = 0,1,2...
	 * @param dist specified distribution.
	 */
	public void setB(int i, Distribution dist) {
		B.set(i, dist);
	}

	
	/**
	 * Getting the number of states.
	 * @return The number of states.
	 */
	public int getStateNumber() {
		return A.size();
	}
	
	
	/**
	 * Evaluating forward variable exactly at specified time point given observation sequence.
	 * @param O Observation sequence.
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param kComp the kth component in case of mixture model.
	 * @return List of evaluated forward variables exactly at specified time point given observation sequence
	 */
	public List<Double> alpha(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		List<Double> alphaSeq = Util.newList(n);
		Obs ot = O.get(0);
		for (int i = 0; i < n; i++) {
			double alpha = getB(i, ot, kComp)*getPI(i);
			alphaSeq.add(alpha);
		}
		
		List<Double> alphaTemp = Util.newList(n);
		for (int t = 1; t <= timePoint; t++) {
			alphaTemp.addAll(alphaSeq);
			ot = O.get(t);
			for (int j = 0; j < n; j++) {
				double sumAlpha = 0;
				for (int i =0; i < n; i++) {
					sumAlpha += alphaTemp.get(i) * getA(i, j);
				}
				alphaSeq.set(j, sumAlpha * getB(j, ot, kComp));
			}
			alphaTemp.clear();
		}
		
		return alphaSeq;
	}
	
	
	/**
	 * Evaluating all forward variables until specified time point given observation sequence.
	 * The result is a list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of forward variables evaluated of given state and given time point.
	 * @param O Observation sequence.
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param kComp the kth component in case of mixture model.
	 * @return A list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of forward variables evaluated.
	 */
	public List<List<Double>> alphaAll(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		List<List<Double>> alphaOut = Util.newList(timePoint+1, n, 0d);
		
		Obs ot = O.get(0);
		List<Double> alphaList = alphaOut.get(0);
		for (int i = 0; i < n; i++) {
			double alpha = getB(i, ot, kComp)*getPI(i);
			alphaList.set(i, alpha);
		}
		
		for (int t = 1; t <= timePoint; t++) {
			List<Double> preAlphaList = alphaOut.get(t-1);
			alphaList = alphaOut.get(t);
			ot = O.get(t);
			for (int j = 0; j < n; j++) {
				double sumAlpha = 0;
				for (int i =0; i < n; i++) {
					sumAlpha += preAlphaList.get(i) * getA(i, j);
				}
				alphaList.set(j, sumAlpha * getB(j, ot, kComp));
			}
		}
		
		return alphaOut;
	}
	
	
	/**
	 * Evaluating all forward variables given observation sequence.
	 * The result is a list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of forward variables evaluated of given state and given time point.
	 * @param O Observation sequence.
	 * @param kComp the kth component in case of mixture model.
	 * @return A list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of forward variables evaluated.
	 */
	public List<List<Double>> alphaAll(List<Obs> O, int kComp) {
		return alphaAll(O, O.size()-1, kComp);
	}

	
	/**
	 * Evaluating backward variable exactly at specified time point given observation sequence.
	 * @param O Observation sequence.
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param kComp the kth component in case of mixture model.
	 * @return List of evaluated backward variables exactly at specified time point given observation sequence
	 */
	public List<Double> beta(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<Double> betaPost = Util.newList(n, 1d);
		
		List<Double> betaTemp = Util.newList(n);
		for (int t = T-1; t >= timePoint; t--) {
			betaTemp.addAll(betaPost);
			Obs ot_plus_1 = O.get(t+1);
			for (int i = 0; i < n; i++) {
				double sumBeta = 0;
				for (int j = 0; j < n; j++) {
					sumBeta += getA(i, j) * getB(j, ot_plus_1, kComp) * betaTemp.get(j);
				}
				betaPost.set(i, sumBeta);
			}
			betaTemp.clear();
		}
		
		return betaPost;
	}

	
	/**
	 * Evaluating all backward variables until specified time point given observation sequence.
	 * The result is a list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of backward variables evaluated of given state and given time point.
	 * @param O Observation sequence.
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param kComp the kth component in case of mixture model.
	 * @return A list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of backward variables evaluated.
	 */
	public List<List<Double>> betaAll(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<List<Double>> betaOut = Util.newList(T-timePoint+1, n, 1d);
		
		for (int t = T-1; t >= timePoint; t--) {
			List<Double> preBetaList = betaOut.get(t-timePoint+1);
			List<Double> betaList = betaOut.get(t-timePoint);
			Obs ot_plus_1 = O.get(t+1);
			for (int i = 0; i < n; i++) {
				double sumBeta = 0;
				for (int j = 0; j < n; j++) {
					sumBeta += getA(i, j) * getB(j, ot_plus_1, kComp) * preBetaList.get(j);
				}
				betaList.set(i, sumBeta);
			}
		}
		
		return betaOut;
	}

	
	/**
	 * Evaluating all backward variables given observation sequence.
	 * The result is a list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of backward variables evaluated of given state and given time point.
	 * @param O Observation sequence.
	 * @param kComp the kth component in case of mixture model.
	 * @return A list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of backward variables evaluated.
	 */
	public List<List<Double>> betaAll(List<Obs> O, int kComp) {
		return betaAll(O, 0, kComp);
	}
	
	
	/**
	 * Evaluating all forward and backward variables given observation sequence.
	 * @param O Observation sequence.
	 * @param kComp the kth component in case of mixture model.
	 * @return a list of two elements. One is list of all forward variables  and the other is list of all backward variables.
	 */
	public List<?>[] alphaBetaAll(List<Obs> O, int kComp) {
		int T = O.size() - 1;
		int n = getStateNumber();
		List<List<Double>> alphas = Util.newList(T+1, n, 0d);
		List<List<Double>> betas = Util.newList(T+1, n, 0d);
		alphaBetaAll(O, alphas, betas, kComp);
		
		return new List<?>[] {alphas, betas};
	}
	
	
	/**
	 * Evaluating all forward and backward variables given observation sequence.
	 * @param O Observation sequence.
	 * @param alphaOut list of all forward variables.
	 * @param betaOut  list of all backward variables.
	 * @param kComp the kth component in case of mixture model.
	 */
	private void alphaBetaAll(List<Obs> O, List<List<Double>> alphaOut, List<List<Double>> betaOut, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		
		Obs o_t = O.get(0);
		List<Double> alphaList = alphaOut.get(0);
		List<Double> betaList = betaOut.get(T);
		for (int i = 0; i < n; i++) {
			double alpha = getB(i, O.get(0), kComp)*getPI(i);
			alphaList.set(i, alpha);
			betaList.set(i, 1d);
		}
		
		for (int t = 1; t <= T; t++) {
			int rt = T - t;
			List<Double> preAlphaList = alphaOut.get(t-1);
			List<Double> preBetaList = betaOut.get(rt+1);
			Obs o_rt_pre =  O.get(rt+1);
			alphaList = alphaOut.get(t);
			betaList = betaOut.get(rt);
			o_t = O.get(t);
			for (int i = 0; i < n; i++) {
				double sumAlpha = 0;
				double sumBeta = 0;
				for (int j = 0; j < n; j++) {
					sumAlpha += preAlphaList.get(j) * getA(j, i);
					sumBeta += getA(i, j) * getB(j, o_rt_pre, kComp) * preBetaList.get(j);
				}
				alphaList.set(i, sumAlpha * getB(i, o_t, kComp));
				betaList.set(i, sumBeta);
			}
		}
	}


	/**
	 * Evaluating a list of gamma variables which are products of forward variables and backward variables.
	 * @param O Observation sequence.
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param kComp the kth component in case of mixture model.
	 * @return a list of gamma variables which are products of forward variables and backward variables.
	 */
	public List<Double> gamma(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		List<Double> alist = alpha(O, timePoint, kComp);
		List<Double> blist = beta(O, timePoint, kComp);
		List<Double> glist = Util.newList(n);
		for (int i = 0; i < n; i++) {
			double g = alist.get(i) * blist.get(i);
			glist.add(g);
		}
		alist.clear();
		blist.clear();
		
		return glist;
	}
	
	
	/**
	 * Evaluating lists of all gamma variables which are products of forward variables and backward variables.
	 * @param O Observation sequence.
	 * @param kComp the kth component in case of mixture model.
	 * @return lists of all gamma variables which are products of forward variables and backward variables.
	 */
	public List<List<Double>> gammaAll(List<Obs> O, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<List<Double>> gammaOut = Util.newList(T+1);
		
		List<?>[] abs = alphaBetaAll(O, kComp);
		@SuppressWarnings("unchecked")
		List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
		@SuppressWarnings("unchecked")
		List<List<Double>> betas = (List<List<Double>>)(abs[1]);
		for (int t = 0; t <= T; t++) {
			List<Double> alist = alphas.get(t);
			List<Double> blist = betas.get(t);
			List<Double> glist = Util.newList(n);
			for (int i = 0; i < n; i++) {
				double g = alist.get(i) * blist.get(i);
				glist.add(g);
			}
			gammaOut.add(glist);
		}
		alphas.clear();
		betas.clear();
		
		return gammaOut;
	}

	
	/**
	 * Evaluating lists of all gamma variables which are products of forward variables and backward variables.
	 * Note, these lists are followed states.
	 * @param O Observation sequence.
	 * @param kComp the kth component in case of mixture model.
	 * @return lists of all gamma variables which are products of forward variables and backward variables.
	 */
	public List<List<Double>> gammaAllByState(List<Obs> O, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<List<Double>> gammaOut = Util.newList(n);
		
		List<?>[] abs = alphaBetaAll(O, kComp);
		@SuppressWarnings("unchecked")
		List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
		@SuppressWarnings("unchecked")
		List<List<Double>> betas = (List<List<Double>>)(abs[1]);
		for (int i = 0; i < n; i++) {
			List<Double> glist = Util.newList(T+1);
			for (int t = 0; t <= T; t++) {
				double g = alphas.get(t).get(i) * betas.get(t).get(i);
				glist.add(g);
			}
			gammaOut.add(glist);
		}
		alphas.clear();
		betas.clear();
		
		return gammaOut;
	}

	
	/**
	 * Evaluating lists of all gamma variables which are products of forward variables and backward variables.
	 * Note, these lists are followed components in case of mixture model.
	 * @param O Observation sequence.
	 * @param kCompCount the number of components in case of mixture model.
	 * @param state specified state.
	 * @return lists of all gamma variables which are products of forward variables and backward variables.
	 */
	public List<List<Double>> gammaAllByComp(List<Obs> O, int kCompCount, int state) {
		List<List<Double>> gammaOut = Util.newList(kCompCount);
		int T = O.size() - 1;
		for (int k = 0; k < kCompCount; k++) {
			List<?>[] abs = alphaBetaAll(O, k);
			@SuppressWarnings("unchecked")
			List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
			@SuppressWarnings("unchecked")
			List<List<Double>> betas = (List<List<Double>>)(abs[1]);
			
			List<Double> glist = Util.newList(T+1);
			for (int t = 0; t <= T; t++) {
				double g = alphas.get(t).get(state) * betas.get(t).get(state);
				glist.add(g);
			}
			gammaOut.add(glist);
			
			alphas.clear();
			betas.clear();
		}
		
		return gammaOut;
	}
	
	
	/**
	 * Evaluating lists of c variables which are products of forward variables, transition probabilities, observation probabilities, and backward variables.
	 * @param O Observation sequence.
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param kComp the kth component in case of mixture model.
	 * @return lists of c variables which are products of forward variables, transition probabilities, observation probabilities, and backward variables.
	 */
	public List<List<Double>> c(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		List<List<Double>> cmatrix = Util.newList(n);
		List<Double> alphas = alpha(O, timePoint-1, kComp);
		List<Double> betas = beta(O, timePoint, kComp);
		Obs ot = O.get(timePoint);
		for (int i = 0; i < n; i++) {
			List<Double> clist = Util.newList(n);
			cmatrix.add(clist);
			for (int j = 0; j < n; j++) {
				double c = 
						alphas.get(i) * 
						getA(i,j) * 
						getB(j, ot, kComp) *
						betas.get(j);
				clist.add(c);
			}
		}
		alphas.clear();
		betas.clear();
		
		return cmatrix;
	}
	
	
	/**
	 * Evaluating lists of c variables which are products of forward variables, transition probabilities, observation probabilities, and backward variables.
	 * Note, evaluation is based on fixed pre-state.
	 * @param O Observation sequence.
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param preState fixed pre-state.
	 * @param kComp the kth component in case of mixture model.
	 * @return lists of c variables which are products of forward variables, transition probabilities, observation probabilities, and backward variables.
	 */
	public List<Double> cForPre(List<Obs> O, int timePoint, int preState, int kComp) {
		int n = getStateNumber();
		List<Double> clist = Util.newList(n);
		List<Double> alphas = alpha(O, timePoint-1, kComp);
		List<Double> betas = beta(O, timePoint, kComp);
		
		double alpha = alphas.get(preState);
		alphas.clear();
		Obs ot = O.get(timePoint);
		for (int j = 0; j < n; j++) {
			double c = 
					alpha * 
					getA(preState,j) * 
					getB(j, ot, kComp) *
					betas.get(j);
			clist.add(c);
		}
		betas.clear();
		
		return clist;
	}

	
	/**
	 * Evaluating lists of c variables which are products of forward variables, transition probabilities, observation probabilities, and backward variables.
	 * Note, evaluation is based on fixed post-state.
	 * @param O Observation sequence.
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param postState fixed post-state.
	 * @param kComp the kth component in case of mixture model.
	 * @return lists of c variables which are products of forward variables, transition probabilities, observation probabilities, and backward variables.
	 */
	public List<Double> cForPost(List<Obs> O, int timePoint, int postState, int kComp) {
		int n = getStateNumber();
		List<Double> clist = Util.newList(n);
		List<Double> alphas = alpha(O, timePoint-1, kComp);
		List<Double> betas = beta(O, timePoint, kComp);

		double beta = betas.get(postState);
		betas.clear();
		Obs ot = O.get(timePoint);
		for (int i = 0; i < n; i++) {
			double c = 
					alphas.get(i) * 
					getA(i,postState) * 
					getB(postState, ot, kComp) *
					beta;
			clist.add(c);
		}
		alphas.clear();
		
		return clist;
	}

	
	/**
	 * Evaluating lists of c variables which are products of forward variables, transition probabilities, observation probabilities, and backward variables.
	 * Note, there is no specified time point and fixed state.
	 * @param O Observation sequence.
	 * @param kComp the kth component in case of mixture model.
	 * @return lists of c variables which are products of forward variables, transition probabilities, observation probabilities, and backward variables.
	 */
	@Deprecated
	public List<List<List<Double>>> cAll(List<Obs> O, int kComp) {
		int T = O.size() - 1;
		int n = getStateNumber();
		List<List<List<Double>>> cout = Util.newList(T);
		List<?>[] abs = alphaBetaAll(O, kComp);
		@SuppressWarnings("unchecked")
		List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
		@SuppressWarnings("unchecked")
		List<List<Double>> betas = (List<List<Double>>)(abs[1]);
		
		for (int t = 1; t <= T; t++) {
			List<List<Double>> cmatrix = Util.newList(n);
			cout.add(cmatrix);
			for (int i = 0; i < n; i++) {
				List<Double> clist = Util.newList(n);
				cmatrix.add(clist);
				for (int j = 0; j < n; j++) {
					double c = 
							alphas.get(t-1).get(i) * 
							getA(i,j) * 
							getB(j, O.get(t), kComp) *
							betas.get(t).get(j);
					clist.add(c);
				}
			}
		}
		alphas.clear();
		betas.clear();
		
		return cout;
	}
	
	
	/**
	 * Implementing Viterbi algorithm to solve the uncovering problem that find out the state sequence that is appropriate mostly to given observation sequence.
	 * This method is very important.
	 * @param O Observation sequence.
	 * @return The state sequence that is appropriate mostly to given observation sequence
	 */
	public List<Integer> viterbi(List<Obs> O) {
		int T = O.size() - 1;
		int n = getStateNumber();
		
		fireInfoEvent(new HMMInfoEventImpl(this, 
			"Viterbi algorithm on observation sequence O=" + toObsString(O) + " with HMM:" + "\n" + this + "\n-----t=0-----"));
		
		List<Double> deltaSeq = Util.newList(n);
		List<List<Integer>> tracks = Util.newList(T+1);
		for (int t = 0; t <= T; t++) {
			List<Integer> track = Util.newList(n);
			tracks.add(track);
		}
		List<Integer> track = tracks.get(0);
		Obs ot = O.get(0);
		for (int i = 0; i < n; i++) {
			double alpha = getB(i, ot, -1)*getPI(i);
			deltaSeq.add(alpha);
			track.add(0);
			
			fireInfoEvent(new HMMInfoEventImpl(this,
				String.format("alpha0(%d)=" + Util.DECIMAL_FORMAT, i, alpha) + "\n" +
				String.format("q0(%d)=0", i)));
		}
		
		List<Double> deltaTemp = Util.newList(n);
		for (int t = 1; t <= T; t++) {
			fireInfoEvent(new HMMInfoEventImpl(this, "\n-----t=" + t + "-----"));
			
			deltaTemp.addAll(deltaSeq);
			ot = O.get(t);
			track = tracks.get(t);
			for (int j = 0; j < n; j++) {
				double maxalpha = -1;
				int maxstate = -1;
				for (int i =0; i < n; i++) {
					double alpha = deltaTemp.get(i) * getA(i, j);
					if (alpha > maxalpha) {
						maxalpha = alpha;
						maxstate = i;
					}
					
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("alpha%d(%d)*a%d(%d)=" + Util.DECIMAL_FORMAT, t-1, i, i, j, alpha)));
				}
				double delta = maxalpha * getB(j, ot, -1);
				deltaSeq.set(j, delta);
				track.add(maxstate);
				
				fireInfoEvent(new HMMInfoEventImpl(this,
					String.format("Max{alpha%d(i)*ai(%d)} = alpha%d(%d)*a%d(%d) = " + Util.DECIMAL_FORMAT, t-1, j, t-1, maxstate, maxstate, j, maxalpha) + "\n" +
					String.format("delta%d(%d) = Max{alpha%d(i)*ai(%d)}*b%d(%d) = alpha%d(%d)*a%d(%d)*b%d(%d) = " + Util.DECIMAL_FORMAT, t, j, t-1, j, j, t, t-1, maxstate, maxstate, j, j, t, maxalpha) + "\n" +
					String.format("q%d(%d)=%d", t, j, maxstate)));
			}
			deltaTemp.clear();
		}
		
		List<Integer> states = Util.newList(T+1, -1);
		double maxalpha = -1;
		int maxstate = -1;
		for (int j =0; j < n; j++) {
			double alpha = deltaSeq.get(j);
			if (alpha > maxalpha) {
				maxalpha = alpha;
				maxstate = j;
			}
		}
		states.set(T, maxstate);
		deltaSeq.clear();
		
		fireInfoEvent(new HMMInfoEventImpl(this, String.format("Optimal state x(%d) = argmax{delta%d(j)} = %d", T, T, maxstate)));
		
		for (int t = T-1; t >= 0; t--) {
			int postState = states.get(t+1);
			maxstate = tracks.get(t+1).get(postState);
			states.set(t, maxstate);
			
			fireInfoEvent(new HMMInfoEventImpl(this, String.format("Optimal state x(%d) = q%d(x(%d)) = q%d(%d) = %d", t, t+1, t+1, t+1, postState, maxstate)));
		}
		tracks.clear();
		
		fireInfoEvent(new HMMInfoEventImpl(this, "\nThe resulted optimal state sequence is X=" + toStateString(states)));
		
		return states;
	}


	/**
	 * Getting weight that is product of observation probability and state.
	 * @param ot observation probability.
	 * @param xt state as integer index.
	 * @return weight which is product of observation probability and state.
	 */
	public double weight(Obs ot, int xt) {
		return getB(xt, ot, -1) * getPI(xt);
	}
	
	
	/**
	 * Getting weight that is product of observation probability, previous state, and current state.
	 * @param ot observation probability.
	 * @param pre_xt previous state.
	 * @param xt current state.
	 * @return weight that is product of observation probability, previous state, and current state.
	 */
	public double weight(Obs ot, int pre_xt, int xt) {
		double b = getB(xt, ot, -1);
		return b * b * getA(pre_xt, xt) * getPI(xt);
	}
	
	
	/**
	 * Getting path as weight sequence from observation sequence and state sequence.
	 * @param O observation sequence.
	 * @param X state sequence
	 * @return path as weight sequence from observation sequence and state sequence.
	 */
	public double path(List<Obs> O, List<Double> X) {
		double path = weight(O.get(0), X.get(0).intValue());
		int T = O.size() - 1;
		for (int t = 1; t <= T; t++) {
			path *= weight(O.get(t), X.get(t-1).intValue(), X.get(t).intValue());
		}
		
		return path;
	}
	
	
	/**
	 * Finding longest path from observation sequence. This method has the same purpose of {@link #viterbi(List)} to solve the uncovering problem.
	 * @param O observation sequence.
	 * @return list of state (integer indices) as longest path.
	 */
	public List<Integer> longestPath(List<Obs> O) {
		int T = O.size() - 1;
		List<Integer> states = Util.newList(T+1);

		fireInfoEvent(new HMMInfoEventImpl(this,
			"Longest-path algorithm on observation sequence O=" + toObsString(O) + " with HMM:" + "\n" + this + "\n-----t=0-----"));
			
		int maxstate = -1;
		double maxweight = -1;
		int n = getStateNumber();
		Obs o0 = O.get(0);
		for (int i = 0; i < n; i++) {
			double w = weight(o0, i);
			if (w > maxweight) {
				maxweight = w;
				maxstate = i;
			}

			fireInfoEvent(new HMMInfoEventImpl(this, String.format("W011%d=" + Util.DECIMAL_FORMAT, i, w)));
		}
		states.add(maxstate);
		
		fireInfoEvent(new HMMInfoEventImpl(this, String.format("Max{W011k} k from 0 to %d is W011%d=" + Util.DECIMAL_FORMAT, n-1, maxstate, maxweight)));
		
		int j = maxstate;
		for (int t = 1; t <= T; t++) {
			Obs ot = O.get(t);
			maxstate = -1;
			maxweight = -1;
			for (int k = 0; k < n; k++) {
				double w = weight(ot, j, k);
				if (w > maxweight) {
					maxweight = w;
					maxstate = k;
				}
				
				fireInfoEvent(new HMMInfoEventImpl(this, String.format("W%d%d%d%d=" + Util.DECIMAL_FORMAT, t-1, j, t, k, w)));
			}
			states.add(maxstate);
			j = maxstate;
			
			fireInfoEvent(new HMMInfoEventImpl(this,
				String.format("Max{W%d%d%dk} k from 0 to %d is W%d%d%d%d=" + Util.DECIMAL_FORMAT, t-1, j, t, n-1, t-1, j, t, maxstate, maxweight)));
		}
		
		fireInfoEvent(new HMMInfoEventImpl(this, "\nThe longest-path (optimal state sequence) is X=" + toStateString(states)));

		return states;
	}
	
	
	/**
	 * Finding longest path from observation sequence. This method has the same purpose of {@link #viterbi(List)} to solve the uncovering problem.
	 * It is advanced version of method {@link #longestPath(List)}.
	 * @param O observation sequence.
	 * @return list of state (integer indices) as longest path.
	 */
	public List<Integer> longestPathAdvanced(List<Obs> O) {
		int T = O.size() - 1;
		int n = getStateNumber();
		List<Integer> states = Util.newList(T+1);
		
		fireInfoEvent(new HMMInfoEventImpl(this,
				"Advanced longest-path algorithm on observation sequence O=" + toObsString(O) + " with HMM:" + "\n" + this + "\n-----t=0-----"));
		
		int i = 0;
		List<Double> W1 = Util.newList(n, 0d);
		List<Double> W2 = Util.newList(n, 0d);
		List<Integer> S2 = Util.newList(n, 0);
		for (int t = 0; t <= T; t+=2) {
			fireInfoEvent(new HMMInfoEventImpl(this, "\n-----t=" + t + "-----"));
			
			if (t == 0) {
				Obs o0 = O.get(0);
				for (int j = 0; j < n; j++) {
					double w = weight(o0, j);
					W1.set(j, w);
					
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("W%d%d%d%d=" + Util.DECIMAL_FORMAT, t-1, i, t, j, w)));
				}
			}
			else {
				Obs ot = O.get(t);
				for (int j = 0; j < n; j++) {
					double w = weight(ot, i, j);
					W1.set(j, w);
					
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("W%d%d%d%d=" + Util.DECIMAL_FORMAT, t-1, i, t, j, w)));
				}
			}
			
			if (t == T) {
				int maxstate = -1;
				double maxweight = -1;
				for (int j = 0; j < n; j++) {
					double w = W1.get(j);
					if (w > maxweight) {
						maxweight = w;
						maxstate = j;
					}
				}
				states.add(maxstate);
				i = maxstate;
				
				fireInfoEvent(new HMMInfoEventImpl(this, 
					String.format("Max{W%d%d%dk} k from 0 to %d is W%d%d%d%d=" + Util.DECIMAL_FORMAT, t-1, i, t, n-1, t-1, i, t, maxstate, maxweight) + "\n" +
					String.format("Optimal states: x%d=%d", t, maxstate)));
			}
			else {
				Obs ot_plus_1 = O.get(t+1);
				for (int j = 0; j < n; j++) {
					int maxstate = -1;
					double maxweight = -1;
					for (int k = 0; k < n; k++) {
						double w = weight(ot_plus_1, j, k);
						if (w > maxweight) {
							maxweight = w;
							maxstate = k;
						}
						
						fireInfoEvent(new HMMInfoEventImpl(this, String.format("W%d%d%d%d=" + Util.DECIMAL_FORMAT, t, j, t+1, k, w)));
					}
					W2.set(j, maxweight);
					S2.set(j, maxstate);
					
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("Max{W%d%d%dk} k from 1 to %d is W%d%d%d%d=" + Util.DECIMAL_FORMAT, t, j, t+1, n-1, t, j, t+1, maxstate, maxweight)));
				}
				
				int maxstate = -1;
				double maxweight = -1;
				for (int j = 0; j < n; j++) {
					double w = W1.get(j) * W2.get(j);
					if (w > maxweight) {
						maxweight = w;
						maxstate = j;
					}
					
					fireInfoEvent(new HMMInfoEventImpl(this,
						String.format("W%d%d%d%d*W%d%d%d%d=" + Util.DECIMAL_FORMAT + "*" + Util.DECIMAL_FORMAT + "=" + Util.DECIMAL_FORMAT, 
							t-1, i, t, j,
							t, j, t+1, S2.get(j), W1.get(j), W2.get(j), w)));
				}
				
				int maxstate2 = S2.get(maxstate);
				states.add(maxstate);
				states.add(maxstate2);
				i = maxstate2;
				
				fireInfoEvent(new HMMInfoEventImpl(this,
					String.format("The product W%d%d%d[%d]*W%d%d%d[%d]=" + Util.DECIMAL_FORMAT + " is maximal and so:", 
						t-1, i, t, maxstate,
						t, maxstate, t+1, maxstate2, maxweight) + "\n" +
					String.format("Optimal states is: x%d=%d, x%d=%d", t, maxstate, t+1, maxstate2)));
			}
		}//End for t
		W1.clear();
		W2.clear();
		S2.clear();
		
		fireInfoEvent(new HMMInfoEventImpl(this, "\nThe longest-path (optimal state sequence) is X=" + toStateString(states)));
		
		return states;
	}
	
	
	/**
	 * Learning this hidden Markov model (HMM) by expectation maximization (EM) algorithm from observation sequence.
	 * This method is very important to solve the learning problem.
	 * @param O observation sequence.
	 * @param terminatedThreshold terminated threshold.
	 * @param terminatedRatioMode flag to indicate whether terminated threshold is for ratio.
	 * @param maxIteration maximum number of iterations.
	 */
	public void em(List<Obs> O, double terminatedThreshold, boolean terminatedRatioMode, int maxIteration) {
		int n = getStateNumber();
		int T = O.size() - 1;

		fireInfoEvent(new HMMInfoEventImpl(this,
			"EM learning algorithm on observation sequence O=" + toObsString(O) + " with HMM:" + "\n" + this));

		//Lists of alpha (s) and beta (s) are enhanced via data structure.
		List<List<Double>> alphas = Util.newList(T+1, n, 0d);
		List<List<Double>> betas = Util.newList(T+1, n, 0d);
		
		List<Double> numerators = Util.newList(n, 0d);
		List<Double> glist = Util.newList(T+1, 0d);
		double preCriterion = -1;
		int iteration = 0;
		doStarted = true;
		while (doStarted && (maxIteration <= 0 || iteration < maxIteration)) {
			alphaBetaAll(O, alphas, betas, -1);
			
			double curCriterion = 0;
			List<Double> alphaT = alphas.get(T);
			for (double alpha : alphaT)
				curCriterion += alpha;
			
			fireInfoEvent(new HMMInfoEventImpl(this, "\n-----Iteration " + iteration + "-----"));
			serializeQuantities(O);
			fireInfoEvent(new HMMInfoEventImpl(this, String.format("\nGiven current parameters, terminating criterion is P(O)=" + Util.DECIMAL_FORMAT, curCriterion)));
			
			if (preCriterion >= 0) {
				boolean satisfied = false;
				if (terminatedRatioMode)
					satisfied = Math.abs(curCriterion - preCriterion) <= terminatedThreshold * Math.abs(preCriterion);
				else
					satisfied = Math.abs(curCriterion - preCriterion) <= terminatedThreshold;
				if (satisfied) {
					doStarted = false;
					fireInfoEvent(new HMMInfoEventImpl(this, "\nThe resulted estimate is:\n" + this));
					break;
				}
			}
			preCriterion = curCriterion;
			
			//Updating transition probability matrix
			for (int i = 0; i < n; i++) {
				double denominator = 0;
				for (int j = 0; j < n; j++)
					numerators.set(j, 0d);
				
				for (int t = 1; t <= T; t++) {
					List<Double> preAlphaList = alphas.get(t-1);
					List<Double> betaList = betas.get(t);
					Obs ot = O.get(t);
					for (int k = 0; k < n; k++) {
						double c = 
								preAlphaList.get(i) * 
								getA(i,k) * 
								getB(k, ot, -1) *
								betaList.get(k);
						
						numerators.set(k, numerators.get(k) + c);
						denominator += c;
					}
				}
				if (denominator == 0)
					continue;
				
				for (int j = 0; j < n; j++) {
					this.setA(i, j, numerators.get(j)/denominator);
				}
			}//End for i
			
			//Updating initial probability matrix
			double denominator = 0;
			List<Double> alpha0 = alphas.get(0);
			List<Double> beta0 = betas.get(0);
			for (int j = 0; j < n; j++) {
				double g = alpha0.get(j) * beta0.get(j);
				numerators.set(j, g);
				denominator += g;
			}
			if (denominator != 0) {
				for (int j = 0; j < n; j++) {
					this.PI.set(j, numerators.get(j)/denominator);
				}
			}
			
			//Updating observation probability distribution
			for (int j = 0; j < n; j++) {
				Distribution dist = this.B.get(j);
				if (dist instanceof AtomicDistribution) {
					for (int t = 0; t <= T; t++) {
						double g = alphas.get(t).get(j) * betas.get(t).get(j);
						glist.set(t, g);
					}
					((AtomicDistribution) dist).learn(O, glist);
				}
				else if (dist instanceof MixtureDistribution) {
					int K = ((MixtureDistribution)dist).getComponentCount();
					List<List<Double>> glistByK = gammaAllByComp(O, K, j);
					((MixtureDistribution)dist).learn(O, glistByK);
					glistByK.clear();
				}
				
			}//End for j
			
			iteration ++;

			String info = "\nThe resulted estimate is:\n" + this;
			fireInfoEvent(new HMMInfoEventImpl(this, info));
			fireDoEvent(new HMMDoEventImpl(this, Type.doing, "hmm_em", "At iteration " + iteration + info, iteration, maxIteration));
		
			synchronized (this) {
				while (doPaused) {
					notifyAll();
					try {
						wait();
					} catch (Exception e) {Util.trace(e);}
				}
			}
		}
		
		alphas.clear();
		betas.clear();
		numerators.clear();
		glist.clear();
		
		synchronized (this) {
			doStarted = false;
			doPaused = false;
			
			fireDoEvent(new HMMDoEventImpl(this, Type.done, "hmm_em",
					"At final iteration " + iteration + "\nThe final resulted estimate is:\n" + this, iteration, maxIteration));

			notifyAll();
		}

	}

	
	/**
	 * Serializing quantities (parameters and some values) of this hidden Markov model (HMM) along with observation sequence.
	 * @param O observation sequence.
	 */
	private void serializeQuantities(List<Obs> O) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<List<Double>> alphas = Util.newList(T+1, n, 0d);
		List<List<Double>> betas = Util.newList(T+1, n, 0d);
		alphaBetaAll(O, alphas, betas, -1);
		
		if (!(this.B.get(0) instanceof MixtureDistribution)) {
			for (int i = 0; i < n; i++) {
				for (int t = 0; t <= T; t++)
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("b%d(o%d=%s)=" + Util.DECIMAL_FORMAT, i, t, O.get(t).toString(), getB(i, O.get(t), -1))));
			}
			fireInfoEvent(new HMMInfoEventImpl(this, ""));
			
			for (int t = 0; t <= T; t++) {
				List<Double> alist = alphas.get(t);
				for (int i = 0; i < n; i++)
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("alpha%d(%d)=" + Util.DECIMAL_FORMAT, t, i, alist.get(i))));
			}
			fireInfoEvent(new HMMInfoEventImpl(this, ""));
			
			for (int t = 0; t <= T; t++) {
				List<Double> blist = betas.get(t);
				for (int i = 0; i < n; i++)
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("beta%d(%d)=" + Util.DECIMAL_FORMAT, t, i, blist.get(i))));
			}
			fireInfoEvent(new HMMInfoEventImpl(this, ""));

			for (int t = 1; t <= T; t++) {
				List<Double> pre_alist = t > 0 ? alphas.get(t-1) : null;
				List<Double> blist = betas.get(t);
				Obs ot = O.get(t);
				for (int i = 0; i < n; i++) {
					for (int j = 0; t > 0 && j < n; j++) {
						double c = 
								pre_alist.get(i) * 
								getA(i,j) * 
								getB(j, ot, -2) *
								blist.get(j);
						fireInfoEvent(new HMMInfoEventImpl(this, String.format("c%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, j, c)));
					}
				}
			}
			fireInfoEvent(new HMMInfoEventImpl(this, ""));

			for (int t = 0; t <= T; t++) {
				List<Double> alist = alphas.get(t);
				List<Double> blist = betas.get(t);
				for (int i = 0; i < n; i++) {
					double g = alist.get(i) * blist.get(i);
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("gamma%d(%d)=" + Util.DECIMAL_FORMAT, t, i, g)));
				}
			}
		}
		else {
			int K = ((MixtureDistribution)(this.B.get(0))).getComponentCount();
			List<List<List<Double>>> alphasK = Util.newList(K);
			List<List<List<Double>>> betasK = Util.newList(K);
			for (int k = 0; k < K; k++) {
				List<List<Double>> alphas_temp = Util.newList(T+1, n, 0d);
				List<List<Double>> betas_temp = Util.newList(T+1, n, 0d);
				alphaBetaAll(O, alphas_temp, betas_temp, k);
	
				alphasK.add(alphas_temp);
				betasK.add(betas_temp);
			}
			
			for (int i = 0; i < n; i++) {
				for (int t = 0; t <= T; t++) {
					for (int k = 0; k < K; k++) {
						fireInfoEvent(new HMMInfoEventImpl(this, String.format("b%d(o%d=%s,%d)=" + Util.DECIMAL_FORMAT, i, t, O.get(t).toString(), k, getB(i, O.get(t), k))));
					}
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("b%d(o%d=%s)=" + Util.DECIMAL_FORMAT, i, t, O.get(t).toString(), getB(i, O.get(t), -1))));
				}
			}
			fireInfoEvent(new HMMInfoEventImpl(this, ""));

			for (int t = 0; t <= T; t++) {
				List<Double> alist = alphas.get(t);
				for (int i = 0; i < n; i++) {
					for (int k = 0; k < K; k++) {
						List<Double> aklist = alphasK.get(k).get(t);
						fireInfoEvent(new HMMInfoEventImpl(this, String.format("alpha%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, k, aklist.get(i))));
					}
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("alpha%d(%d)=" + Util.DECIMAL_FORMAT, t, i, alist.get(i))));
				}
			}
			fireInfoEvent(new HMMInfoEventImpl(this, ""));
			
			for (int t = 0; t <= T; t++) {
				List<Double> blist = betas.get(t);
				for (int i = 0; i < n; i++) {
					for (int k = 0; k < K; k++) {
						List<Double> bklist = betasK.get(k).get(t);
						fireInfoEvent(new HMMInfoEventImpl(this, String.format("beta%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, k, bklist.get(i))));
					}
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("beta%d(%d)=" + Util.DECIMAL_FORMAT, t, i, blist.get(i))));
				}
			}
			fireInfoEvent(new HMMInfoEventImpl(this, ""));

			for (int t = 1; t <= T; t++) {
				List<Double> pre_alist = t > 0 ? alphas.get(t-1) : null;
				List<Double> blist = betas.get(t);
				Obs ot = O.get(t);
				for (int i = 0; i < n; i++) {
					for (int j = 0; t > 0 && j < n; j++) {
						double c = 
								pre_alist.get(i) * 
								getA(i,j) * 
								getB(j, ot, -2) *
								blist.get(j);
						fireInfoEvent(new HMMInfoEventImpl(this, String.format("c%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, j, c)));
					}
				}
			}
			fireInfoEvent(new HMMInfoEventImpl(this, ""));

			for (int t = 0; t <= T; t++) {
				List<Double> alist = alphas.get(t);
				List<Double> blist = betas.get(t);
				for (int i = 0; i < n; i++) {
					for (int k = 0; k < K; k++) {
						List<Double> aklist = alphasK.get(k).get(t);
						List<Double> bklist = betasK.get(k).get(t);
						double g = aklist.get(i) * bklist.get(i);
						fireInfoEvent(new HMMInfoEventImpl(this, String.format("gamma%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, k, g)));
					}
					double g = alist.get(i) * blist.get(i);
					fireInfoEvent(new HMMInfoEventImpl(this, String.format("gamma%d(%d)=" + Util.DECIMAL_FORMAT, t, i, g)));
				}
			}

			alphasK.clear();
			betasK.clear();
		}
		alphas.clear();
		betas.clear();
	}
	
	
	/**
	 * Learning this hidden Markov model (HMM) by expectation maximization (EM) algorithm from observation sequence.
	 * This method has the same to the {@link #em(List, double, boolean, int)} method but it calls the {@link #emOneLoop(List)} for each iteration.
	 * @param O observation sequence.
	 * @param terminatedThreshold terminated threshold.
	 * @param terminatedRatioMode flag to indicate whether terminated threshold is for ratio.
	 * @param maxIteration maximum number of iterations.
	 */
	@Deprecated
	public void em2(List<Obs> O, double terminatedThreshold, boolean terminatedRatioMode, int maxIteration) {
		fireInfoEvent(new HMMInfoEventImpl(this,
			"EM learning algorithm on observation sequence O=" + toObsString(O) + " with HMM:" + "\n" + this));
		
		double preCriterion = -1;
		double curCriterion = -1;
		int iteration = 0;
		while (true) {
			fireInfoEvent(new HMMInfoEventImpl(this, "\n-----Iteration " + iteration + "-----"));

			curCriterion = emOneLoop(O);
			
			fireInfoEvent(new HMMInfoEventImpl(this, String.format("Given resulted estimate, terminating criterion is P(O)=" + Util.DECIMAL_FORMAT, curCriterion)));
			
			if (preCriterion >= 0) {
				boolean satisfied = false;
				if (terminatedRatioMode)
					satisfied = Math.abs(curCriterion - preCriterion) <= terminatedThreshold * Math.abs(preCriterion);
				else
					satisfied = Math.abs(curCriterion - preCriterion) <= terminatedThreshold;
				if (satisfied) break;
			}
			preCriterion = curCriterion;
			
			iteration ++;
			if (maxIteration > 0 && iteration >= maxIteration)
				break;
		}
	}
	
	
	/**
	 * Learning this hidden Markov model (HMM) by expectation maximization (EM) algorithm from observation sequence in one loop.
	 * @param O observation sequence.
	 * @return the probability of observation sequence.
	 */
	@Deprecated
	protected double emOneLoop(List<Obs> O) {
		int n = getStateNumber();
		int T = O.size() - 1;

		//Lists of alpha (s) and beta (s) are enhanced via data structure.
		List<?>[] abs = alphaBetaAll(O, -1);
		@SuppressWarnings("unchecked")
		List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
		@SuppressWarnings("unchecked")
		List<List<Double>> betas = (List<List<Double>>)(abs[1]);
		List<Double> numerators = Util.newList(n, 0d);
		
		serializeQuantities(O);
		
		//Updating transition probability matrix
		for (int i = 0; i < n; i++) {
			double denominator = 0;
			for (int j = 0; j < n; j++)
				numerators.set(j, 0d);
			
			for (int t = 1; t <= T; t++) {
				List<Double> preAlphaList = alphas.get(t-1);
				List<Double> betaList = betas.get(t);
				Obs ot = O.get(t);
				for (int k = 0; k < n; k++) {
					double c = 
							preAlphaList.get(i) * 
							getA(i,k) * 
							getB(k, ot, -1) *
							betaList.get(k);
					
					numerators.set(k, numerators.get(k) + c);
					denominator += c;
				}
			}
			if (denominator == 0)
				continue;
			
			for (int j = 0; j < n; j++) {
				double numerator = numerators.get(j);
				this.setA(i, j, numerator/denominator);
			}
		}
		
		//Updating initial probability matrix
		double denominator = 0;
		List<Double> alpha0 = alphas.get(0);
		List<Double> beta0 = betas.get(0);
		for (int j = 0; j < n; j++) {
			double g = alpha0.get(j) * beta0.get(j);
			numerators.set(j, g);
			denominator += g;
		}
		if (denominator != 0) {
			for (int j = 0; j < n; j++) {
				this.PI.set(j, numerators.get(j)/denominator);
			}
		}
		
		//Updating observation probability distribution
		for (int j = 0; j < n; j++) {
			Distribution dist = this.B.get(j);
			if (dist instanceof AtomicDistribution) {
				List<Double> glist = Util.newList(T+1);
				for (int t = 0; t <= T; t++) {
					double g = alphas.get(t).get(j) * betas.get(t).get(j);
					glist.add(g);
				}
				((AtomicDistribution) dist).learn(O, glist);
				glist.clear();
			}
			else if (dist instanceof MixtureDistribution) {
				alphas.clear();
				betas.clear();
				numerators.clear();

				int K = ((MixtureDistribution)dist).getComponentCount();
				List<List<Double>> glistByK = gammaAllByComp(O, K, j);
				((MixtureDistribution)dist).learn(O, glistByK);
				glistByK.clear();
			}
			
		}//End for j
		alphas.clear();
		betas.clear();
		numerators.clear();
		
		fireInfoEvent(new HMMInfoEventImpl(this, "\nThe resulted estimate is:\n" + this));

		return probObs(O);
	}

	
	/**
	 * Calculating the conditional probability of observation sequence given state sequence, P(O | X).
	 * @param O observation sequence.
	 * @param X state sequence.
	 * @return the conditional probability of observation sequence given state sequence, P(O | X).
	 */
	public double condProb(List<Obs> O, List<Integer> X) {
		double p = 1;
		int T = O.size() - 1;
		for (int t = 0; t <= T; t++) {
			Obs o = O.get(t);
			int x = X.get(t);
			p *= getB(x, o, -1);
		}
		return p;
	}

	
	/**
	 * Calculating the joint probability of observation sequence and state sequence, P(O, X).
	 * @param O observation sequence.
	 * @param X state sequence.
	 * @return the joint probability of observation sequence and state sequence, P(O, X).
	 */
	public double jointProb(List<Obs> O, List<Integer> X) {
		double p = getPI(X.get(0));
		int T = O.size() - 1;
		for (int t = 1; t <= T; t++ ) {
			p *= getA(X.get(t-1), X.get(t));
		}
		return p * condProb(O, X);
	}

	
	/**
	 * Calculating the probability of observation sequence.
	 * @param O observation sequence.
	 * @return the probability of observation sequence.
	 */
	public double probObs(List<Obs> O) {
		int T = O.size() - 1;
		List<Double> alist = alpha(O, T, -1);
		double p = 0;
		for (double alpha : alist)
			p += alpha;
		
		alist.clear();
		return p;
	}
	
	
	/**
	 * Calculating the probability of state sequence.
	 * @param X state sequence.
	 * @return the probability of state sequence.
	 */
	public double probState(List<Integer> X) {
		double p = getPI(X.get(0));
		
		int T = X.size() - 1;
		for (int t = 1; t <= T; t++)
			p *= getA(X.get(t-1), X.get(t));
		
		return p;
	}
	
	
	/**
	 * Adding listener.
	 * @param listener specified listener.
	 */
	public void addListener(HMMListener listener) {
		synchronized (listenerList) {
			listenerList.add(HMMListener.class, listener);
		}
	}


	/**
	 * Removing listener.
	 * @param listener specified listener.
	 */
	public void removeListener(HMMListener listener) {
		synchronized (listenerList) {
			listenerList.remove(HMMListener.class, listener);
		}
	}

	
	/**
	 * Getting an array of listeners.
	 * @return array of listeners.
	 */
	protected HMMListener[] getListeners() {
		if (listenerList == null) return new HMMListener[] {};
		synchronized (listenerList) {
			return listenerList.getListeners(HMMListener.class);
		}
	}
	
	
	/**
	 * Firing information event.
	 * @param evt information event.
	 */
	protected void fireInfoEvent(HMMInfoEvent evt) {
		if (listenerList == null) return;
		
		HMMListener[] listeners = getListeners();
		for (HMMListener listener : listeners) {
			try {
				listener.receivedInfo(evt);
			}
			catch (Throwable e) { Util.trace(e);}
		}
	}

	
	/**
	 * Firing learning event.
	 * @param evt learning event.
	 */
	protected void fireDoEvent(HMMDoEvent evt) {
		if (listenerList == null) return;
		
		HMMListener[] listeners = getListeners();
		for (HMMListener listener : listeners) {
			try {
				listener.receivedDo(evt);
			}
			catch (Throwable e) {Util.trace(e);}
		}
	}

	
	/**
	 * Pause doing.
	 * @return true if pausing is successful.
	 */
	public synchronized boolean doPause() {
		if (!isDoRunning()) return false;
		
		doPaused  = true;
		
		try {
			wait();
		} 
		catch (Throwable e) {Util.trace(e);}
		
		return true;
	}


	/**
	 * Resume doing.
	 * @return true if resuming is successful.
	 */
	public synchronized boolean doResume() {
		if (!isDoPaused()) return false;
		
		doPaused = false;
		notifyAll();
		
		return true;
	}


	/**
	 * Stop doing.
	 * @return true if stopping is successful.
	 */
	public synchronized boolean doStop() {
		if (!isDoStarted()) return false;
		
		doStarted = false;
		
		if (doPaused) {
			doPaused = false;
			notifyAll();
		}
		
		try {
			wait();
		} 
		catch (Throwable e) {Util.trace(e);}
		
		return true;
	}


	/**
	 * Checking whether in doing mode.
	 * @return whether in doing mode.
	 */
	public boolean isDoStarted() {
		return doStarted;
	}


	/**
	 * Checking whether in paused mode.
	 * @return whether in paused mode.
	 */
	public boolean isDoPaused() {
		return doStarted && doPaused;
	}


	/**
	 * Checking whether in running mode.
	 * @return whether in running mode.
	 */
	public boolean isDoRunning() {
		return doStarted && !doPaused;
	}


	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		int n = getStateNumber();
		
		if (S.size() > 0) {
			buffer.append("States S={");
			for (int i = 0; i < S.size(); i++) {
				if (i > 0)
					buffer.append(", ");
				buffer.append("s" + i + "=" + S.get(i));
			}
			buffer.append("}\n\n");
		}
		
		if (OBS.size() > 0) {
			buffer.append("Observations O={");
			for (int i = 0; i < OBS.size(); i++) {
				if (i > 0)
					buffer.append(", ");
				buffer.append("o" + i + "=" + OBS.get(i));
			}
			buffer.append("}\n\n");
		}

		buffer.append("Transition probability matrix A\n");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				buffer.append("\n");
			
			for (int j = 0; j < n; j++) {
				if (j > 0)
					buffer.append(" ");
				buffer.append(String.format(Util.DECIMAL_FORMAT, getA(i, j)));
			}
		}
		
		buffer.append("\n\nInitial state probability PI\n");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				buffer.append(" ");
			
			buffer.append(String.format(Util.DECIMAL_FORMAT, PI.get(i)));
		}

		buffer.append("\n\nObservation probability matrix or distribution B\n");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				buffer.append("\n");
			
			buffer.append("Distribution " + i + ":\n" + B.get(i).toString());
		}
		
		return buffer.toString();
	}

	
	/**
	 * Converting the state sequence as integer list into text.
	 * @param X state sequence.
	 * @return text converted from the state sequence.
	 */
	protected static String toStateString(List<Integer> X) {
		int T = X.size() - 1;
		StringBuffer buffer = new StringBuffer("{");
		for (int t = 0; t <= T; t++) {
			if (t > 0)
				buffer.append(", ");
			buffer.append("x(" + t + ")=" + X.get(t));
		}
		buffer.append("}");
		
		return buffer.toString();
	}
	
	
	/**
	 * Converting the observation sequence into text.
	 * @param O observation sequence.
	 * @return text converted from observation sequence.
	 */
	protected static String toObsString(List<Obs> O) {
		int T = O.size() - 1;
		StringBuffer buffer = new StringBuffer("{");
		for (int t = 0; t <= T; t++) {
			if (t > 0)
				buffer.append(", ");
			buffer.append("o(" + t + ")=" + O.get(t));
		}
		buffer.append("}");
		
		return buffer.toString();
	}

	
	@Override
	public void close() throws Exception {
		try {
			if (S != null) S.clear();
			
			if (OBS != null) OBS.clear();
			
			if (A != null) A.clear();
			
			if (PI != null) PI.clear();
			
			if (B != null) B.clear();
		}
		catch (Throwable e) {Util.trace(e);}
	}


	/**
	 * Creating discrete hidden Markov model (HMM) from transition probability matrix, initial state distribution, and observation probability matrix.
	 * @param A transition probability matrix.
	 * @param PI initial state distribution.
	 * @param B observation probability matrix
	 * @return Discrete hidden Markov model created from transition probability matrix, initial state distribution, and observation probability matrix.
	 */
	public static HMM createDiscreteHMM(double[][] A, double[] PI, double[][] B) {
		DefaultHMM defaultHMM = new DefaultHMM();
		defaultHMM.A = createListList(A);
		defaultHMM.PI = Util.newList(PI.length);
		for (int i = 0; i < PI.length; i++) {
			defaultHMM.PI.add(PI[i]);
		}
		
		defaultHMM.B = Util.newList(B.length);
		for (int i = 0; i < B.length; i++) {
			ProbabilityTable dist = new ProbabilityTable(B[i].length);
			for (int j = 0; j < B[i].length; j++) {
				dist.setProb(j, B[i][j]);
			}
			defaultHMM.B.add(dist); 
		}
		
		return new HMMWrapperImpl(defaultHMM);
	}


	/**
	 * Creating discrete hidden Markov model (HMM) from the number of states and the number of observations with uniform probabilities.
	 * @param nState the number of states.
	 * @param mObs the number of observations.
	 * @return discrete hidden Markov model created from the number of states and the number of observations with uniform probabilities.
	 */
	public static HMM createDiscreteHMM(int nState, int mObs) {
		Random rnd = new Random();
		double sum = 0;
		
		double[][] A = new double[nState][nState];
		for (int i = 0; i < nState; i++) {
			sum = 0;
			for (int j = 0; j < nState; j++) {
				double a = rnd.nextDouble();
				A[i][j] = a;
				sum += a;
			}
			if (sum == 0) {
				int k = rnd.nextInt(nState);
				while((A[i][k] = rnd.nextDouble()) != 0) {}
				sum = A[i][k];
			}
			
			for (int j = 0; j < nState; j++)
				A[i][j] = A[i][j] / sum;
		}
		
		double[] PI = new double[nState];
		sum = 0;
		for (int i = 0; i < nState; i++) {
			double pi = rnd.nextDouble();
			PI[i] = pi;
			sum += pi;
		}
		if (sum == 0) {
			int k = rnd.nextInt(nState);
			while((PI[k] = rnd.nextDouble()) != 0) {}
			sum = PI[k];
		}
		for (int i = 0; i < nState; i++)
			PI[i] = PI[i] / sum;
		
		double[][] B = new double[nState][mObs];
		for (int i = 0; i < nState; i++) {
			sum = 0;
			for (int j = 0; j < mObs; j++) {
				double b = rnd.nextDouble();
				B[i][j] = b;
				sum += b;
			}
			if (sum == 0) {
				int k = rnd.nextInt(mObs);
				while((B[i][k] = rnd.nextDouble()) != 0) {}
				sum = B[i][k];
			}
			for (int j = 0; j < mObs; j++)
				B[i][j] = B[i][j] / sum;
		}

		return createDiscreteHMM(A, PI, B);
	}


	/**
	 * Create continuous hidden Markov model (HMM) with continuous normal distributions of observations.
	 * @param A transition probability matrix.
	 * @param PI initial state distribution.
	 * @param means means of continuous normal distributions of observations.
	 * @param variances variances of continuous normal distributions of observations.
	 * @return continuous hidden Markov model (HMM) with continuous normal distribution of observations.
	 */
	public static HMM createNormalHMM(double[][] A, double[] PI, double[] means, double[] variances) {
		DefaultHMM defaultHMM = new DefaultHMM();
		defaultHMM.A = createListList(A);
		defaultHMM.PI = Util.newList(PI.length);
		for (int i = 0; i < PI.length; i++) {
			defaultHMM.PI.add(PI[i]);
		}
		
		defaultHMM.B = Util.newList(means.length);
		for (int i = 0; i < means.length; i++) {
			NormalDistribution dist = new NormalDistribution(means[i], variances[i]);
			defaultHMM.B.add(dist); 
		}
		
		return new HMMWrapperImpl(defaultHMM);
	}


	/**
	 * Create continuous hidden Markov model (HMM) with continuous exponential distributions of observations.
	 * @param A transition probability matrix.
	 * @param PI initial state distribution.
	 * @param lambdas lambda parameters of continuous exponential distributions of observations.
	 * @return continuous hidden Markov model (HMM) with continuous exponential distributions of observations.
	 */
	public static HMM createExponentialHMM(double[][] A, double[] PI, double[] lambdas) {
		DefaultHMM defaultHMM = new DefaultHMM();
		defaultHMM.A = createListList(A);
		defaultHMM.PI = Util.newList(PI.length);
		for (int i = 0; i < PI.length; i++) {
			defaultHMM.PI.add(PI[i]);
		}
		
		defaultHMM.B = Util.newList(lambdas.length);
		for (int i = 0; i < lambdas.length; i++) {
			ExponentialDistribution dist = new ExponentialDistribution(lambdas[i]);
			defaultHMM.B.add(dist); 
		}
		
		return new HMMWrapperImpl(defaultHMM);
	}


	/**
	 * Create continuous hidden Markov model (HMM) with continuous normal mixture distributions of observations.
	 * @param A transition probability matrix.
	 * @param PI initial state distribution.
	 * @param means means of continuous normal mixture distributions of observations.
	 * @param variances variances of continuous normal mixture distributions of observations.
	 * @param weights weights of components.
	 * @return continuous hidden Markov model (HMM) with continuous normal mixture distributions of observations.
	 */
	public static HMM createNormalMixtureHMM(double[][] A, double[] PI, double[][] means, double[][] variances, double[][] weights) {
		DefaultHMM defaultHMM = new DefaultHMM();
		defaultHMM.A = createListList(A);
		defaultHMM.PI = Util.newList(PI.length);
		for (int i = 0; i < PI.length; i++) {
			defaultHMM.PI.add(PI[i]);
		}
		
		defaultHMM.B = Util.newList(weights.length);
		for (int i = 0; i < weights.length; i++) {
			MixtureDistribution dist = MixtureDistribution.createNormalMixture(means[i], variances[i], weights[i]);
			
			int K = dist.getComponentCount();
			for (int k = 0; k < K; k++) {
				//((NormalDistribution)dist.getComponent(k)).setEpsilon(epsilon);
			}
			
			defaultHMM.B.add(dist);
		}
		
		return new HMMWrapperImpl(defaultHMM);
	}


	/**
	 * Creating list of lists from matrix data.
	 * @param data matrix data.
	 * @return list of lists created from matrix data.
	 */
	private static List<List<Double>> createListList(double[][] data) {
		List<List<Double>> matrix = Util.newList(data.length);
		for (int i = 0; i < data.length; i++) {
			List<Double> rowData = Util.newList(data[i].length);
			for (int j = 0; j < data[i].length; j++) {
				rowData.add(data[i][j]);
			}
			matrix.add(rowData);
		}
		
		return matrix;
	}


}



/**
 * This is the full wrapper of standard hidden Markov model (HMM) specified by the interface {@link HMM}.
 * In other words, it is an implementation of the interface {@link HMM}.
 * The core of this wrapper is the default hidden Markov model specified by {@link DefaultHMM}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class HMMWrapperImpl extends HMMWrapper {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor with the default hidden Markov model (HMM).
	 * @param defaultHMM the default hidden Markov model (HMM).
	 */
	public HMMWrapperImpl(DefaultHMM defaultHMM) {
		super(defaultHMM);
	}


	@Override
	public int n() throws RemoteException {
		return ((DefaultHMM)hmm).getStateNumber();
	}


	@Override
	public double a(int stateI, int stateJ) throws RemoteException {
		return ((DefaultHMM)hmm).getA(stateI, stateJ);
	}


	@Override
	public double pi(int stateI) throws RemoteException {
		return ((DefaultHMM)hmm).getPI(stateI);
	}


	@Override
	public double b(int stateI, Obs obs) throws RemoteException {
		return ((DefaultHMM)hmm).getB(stateI, obs, -1);
	}


	@Override
	public double evaluate(List<Obs> obsSeq) throws RemoteException {
		return ((DefaultHMM)hmm).probObs(obsSeq);
	}


	@Override
	public List<Integer> uncover(List<Obs> obsSeq) throws RemoteException {
		return ((DefaultHMM)hmm).viterbi(obsSeq);
	}


	@Override
	public synchronized void learn(List<Obs> obsSeq) throws RemoteException {
		int maxIteration = config.getAsInt(LEARN_MAX_ITERATION_FIELD);
		maxIteration = maxIteration >= 0 ? maxIteration :  LEARN_MAX_ITERATION_DEFAULT;
		double terminatedThreshold = config.getAsReal(LEARN_TERMINATED_THRESHOLD_FIELD);
		terminatedThreshold = Double.isNaN(terminatedThreshold) ? LEARN_TERMINATED_THRESHOLD_DEFAULT : terminatedThreshold; 
		boolean terminatedRatio = config.getAsBoolean(LEARN_TERMINATED_RATIO_MODE_FIELD);
		
		((DefaultHMM)hmm).em(obsSeq, terminatedThreshold, terminatedRatio, maxIteration);
	}
	
	
	@Override
	public void addListener(HMMListener listener) throws RemoteException {
		((DefaultHMM)hmm).addListener(listener);
	}


	@Override
	public void removeListener(HMMListener listener) throws RemoteException {
		((DefaultHMM)hmm).removeListener(listener);
	}


	@Override
	public boolean doPause() throws RemoteException {
		return ((DefaultHMM)hmm).doPause();
	}


	@Override
	public boolean doResume() throws RemoteException {
		return ((DefaultHMM)hmm).doResume();
	}


	@Override
	public boolean doStop() throws RemoteException {
		return ((DefaultHMM)hmm).doStop();
	}


	@Override
	public boolean isDoStarted() throws RemoteException {
		return ((DefaultHMM)hmm).isDoStarted();
	}


	@Override
	public boolean isDoPaused() throws RemoteException {
		return ((DefaultHMM)hmm).isDoPaused();
	}


	@Override
	public boolean isDoRunning() throws RemoteException {
		return ((DefaultHMM)hmm).isDoRunning();
	}


	/**
	 * Getting the default hidden Markov model (HMM).
	 * @return the default hidden Markov model (HMM).
	 */
	public DefaultHMM getHMMImpl() {
		return ((DefaultHMM)hmm);
	}


}
