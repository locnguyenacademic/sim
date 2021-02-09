/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.util.List;
import java.util.Random;

/**
 * This class is an implement of the observation in hidden Markov model with focus that such observation is a real number.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class MonoObs implements Obs {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal value as a real number.
	 */
	public double value;
	
	
	/**
	 * Constructor with an integer.
	 * @param value an integer.
	 */
	public MonoObs(int value) {
		this.value = value;
	}

	
	/**
	 * Constructor with a real number.
	 * @param value a real number.
	 */
	public MonoObs(double value) {
		this.value = value;
	}
	
	
	/**
	 * Create a list of observations from arrays of numbers.
	 * @param numbers arrays of numbers.
	 * @return a list of observations from arrays of numbers.
	 */
	public static List<Obs> createObsList(Number...numbers) {
		List<Obs> obsList = Util.newList(numbers.length);
		for (Number number : numbers) {
			obsList.add(new MonoObs(number.doubleValue()));
		}
		
		return obsList;
	}


	/**
	 * Create a list of observations from random integers.
	 * @param size size of the list of observations.
	 * @param maxExclusiveInteger maximum exclusive integer.
	 * @return list of observations from random integers.
	 */
	public static List<Obs> createObsListRandomInteger(int size, int maxExclusiveInteger) {
		List<Obs> obsList = Util.newList(size);
		Random rnd = new Random();
		for (int i = 0; i < size; i++) {
			obsList.add(new MonoObs(rnd.nextInt(maxExclusiveInteger)));
		}
		
		return obsList;
	}
	
	
	/**
	 * Create a list of observations from random real number rang from 0 to 1.
	 * @param size size of the list of observations.
	 * @return a list of observations from random real number rang from 0 to 1.
	 */
	public static List<Obs> createObsListRandomReal(int size) {
		List<Obs> obsList = Util.newList(size);
		Random rnd = new Random();
		for (int i = 0; i < size; i++) {
			obsList.add(new MonoObs(rnd.nextDouble()));
		}
		
		return obsList;
	}

	
	@Override
	public String toString() {
		return String.format(Util.DECIMAL_FORMAT, value);
	}
	
	
}
