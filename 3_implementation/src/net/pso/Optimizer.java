/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.io.Serializable;

import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.logistic.MathUtil;

/**
 * This class implements the optimizer of a function, which is also called optimal point.
 * 
 * @param <T> type of evaluated object.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Optimizer<T> implements Serializable, Cloneable {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Best position.
	 */
	public Vector<T> bestPosition = null;
	
	
	/**
	 * Best value.
	 */
	public T bestValue = null;
	
	
	/**
	 * Default constructor.
	 */
	public Optimizer() {

	}

	
	/**
	 * Constructor with best position and best value.
	 * @param bestPosition best position.
	 * @param bestValue best value.
	 */
	public Optimizer(Vector<T> bestPosition, T bestValue) {
		this.bestPosition = bestPosition;
		this.bestValue = bestValue;
	}
	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		if (bestPosition != null) {
			buffer.append("best position = {");
			
			int n = bestPosition.getAttCount();
			for (int i = 0; i < n; i++) {
				if ( i > 0) buffer.append(", ");
				
				Attribute att = bestPosition.getAtt(i);
				String attName = att.getName();
				buffer.append(attName + "=");
				Object value = bestPosition.getValue(i);
				if (value == null) continue;
				
				if ((att.getType() == Type.real) && (value instanceof Number))
					buffer.append(MathUtil.format(((Number)value).doubleValue()));
				else
					buffer.append(value.toString());
			}
			
			buffer.append("}");
		}
		
		if (bestValue != null) {
			if (bestPosition != null) buffer.append(", ");
			buffer.append("best value = ");
			
			if ((bestValue instanceof Double) || (bestValue instanceof Float))
				buffer.append(MathUtil.format(((Number)bestValue).doubleValue()));
			else
				buffer.append(bestValue.toString());
		}
		
		return buffer.toString();
	}


	/**
	 * Extract optimizer from particle.
	 * @param <T> type of evaluated object.
	 * @param particle specified particle.
	 * @return optimizer extracted from particle.
	 */
	public static <T> Optimizer<T> extract(Particle<T> particle) {
		return extract(particle, null);
	}
	
	
	/**
	 * Extract optimizer from particle.
	 * @param <T> type of evaluated object.
	 * @param particle specified particle.
	 * @param func specified function.
	 * @return optimizer extracted from particle.
	 */
	public static <T> Optimizer<T> extract(Particle<T> particle, Function<T> func) {
		if (func == null) return new Optimizer<T>(particle.bestPosition, particle.bestValue);

		if (particle.bestPosition == null) {
			if (particle.position == null)
				return new Optimizer<T>(particle.bestPosition, particle.bestValue);
			else {
				T value = func.eval(particle.position);
				if (value != null)
					return new Optimizer<T>(particle.bestPosition, particle.bestValue);
				else
					return new Optimizer<T>(particle.position, value);
			}
		}
		else if (particle.bestValue != null)
			return new Optimizer<T>(particle.bestPosition, particle.bestValue);
		else
			return new Optimizer<T>(particle.bestPosition, func.eval(particle.bestPosition));
	}


}
