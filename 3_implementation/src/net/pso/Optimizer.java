/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.io.Serializable;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.ProfileVector;
import net.hudup.core.logistic.MathUtil;

/**
 * This class implements the optimizer of a function, which is also called optimal point.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Optimizer implements Serializable, Cloneable {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Best position.
	 */
	public ProfileVector bestPosition = null;
	
	
	/**
	 * Best value.
	 */
	public double bestValue = Constants.UNUSED;
	
	
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
	public Optimizer(ProfileVector bestPosition, double bestValue) {
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
		
		if (Util.isUsed(bestValue)) {
			if (bestPosition != null) buffer.append(", ");
			buffer.append("best value = " + MathUtil.format(bestValue));
		}
		
		return buffer.toString();
	}


	/**
	 * Extract optimizer from particle.
	 * @param particle specified particle.
	 * @return optimizer extracted from particle.
	 */
	public static Optimizer extract(Particle particle) {
		return extract(particle, null);
	}
	
	
	/**
	 * Extract optimizer from particle.
	 * @param particle specified particle.
	 * @param func specified function.
	 * @return optimizer extracted from particle.
	 */
	public static Optimizer extract(Particle particle, Function func) {
		if (func == null) return new Optimizer(particle.bestPosition, particle.bestValue);

		if (particle.bestPosition == null) {
			if (particle.position == null)
				return new Optimizer(particle.bestPosition, particle.bestValue);
			else {
				double value = func.eval(particle.position);
				if (!Util.isUsed(value))
					return new Optimizer(particle.bestPosition, particle.bestValue);
				else
					return new Optimizer(particle.position, value);
			}
		}
		else if (Util.isUsed(particle.bestValue))
			return new Optimizer(particle.bestPosition, particle.bestValue);
		else
			return new Optimizer(particle.bestPosition, func.eval(particle.bestPosition));
	}


}
