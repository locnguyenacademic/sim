/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.data;

import java.util.Collection;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.DSUtil;

/**
 * This class represents a document vector. SMTP measure in this class is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public class DocumentVector extends Profile {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Referred list of variances.
	 */
	protected List<Double> refVars = Util.newList();

	
	/**
	 * Referred lambda.
	 */
	protected double refLambda = 0;
	
	
	/**
	 * Constructor with specified list of double values.
	 * @param data specified collection of double values.
	 */
	public DocumentVector(Collection<Double> data) {
		init(data);
	}


	/**
	 * Constructor with specified array of double values.
	 * @param data specified array of double values.
	 */
	public DocumentVector(double[] data) {
		this(DSUtil.toDoubleList(data));
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified dimension and specified initial value.
	 * @param dim specified dimension.
	 * @param initValue specified initial value.
	 */
	public DocumentVector(int dim, double initValue) {
		init(DSUtil.initDoubleList(dim, initValue));
	}

	
	/**
	 * Initializing the document with specified collection.
	 * @param data specified collection.
	 */
	protected void init(Collection<Double> data) {
		List<Attribute> attList = Util.newList();
		for (int i = 0; i < data.size(); i++) {
			Attribute att = new Attribute("w" + i, Type.real);
			attList.add(att);
		}
		setAttRef(AttributeList.create(attList));
		
		int i = 0;
		for (double value : data) {
			setValue(i, value);
			i++;
		}
	}
	
	
	/**
	 * Getting referred variances.
	 * @return referred variances.
	 */
	public List<Double> getRefVars() {
		return refVars;
	}
	
	
	/**
	 * Setting referred variances.
	 * @param refVars referred variances.
	 */
	public void setRefVars(List<Double> refVars) {
		this.refVars = refVars;
	}
	
	
	/**
	 * Getting referred lambda.
	 * @return referred lambda.
	 */
	public double getRefLambda() {
		return refLambda;
	}

	
	/**
	 * Setting referred variances.
	 * @param refLambda referred lambda.
	 */
	public void setRefLambda(double refLambda) {
		this.refLambda = refLambda;
	}
	
	
	/**
	 * Computing the module (length) of this document.
	 * @return module (length) of this document.
	 */
	public double module() {
		int n = this.getAttCount();
		
		double sum = 0;
		for (int i = 0; i < n; i++) {
			double v = this.getValueAsReal(i);
			sum += v*v;
		}
		
		return Math.sqrt(sum);
	}
	

	/**
	 * Calculating mean (average value) of this document.
	 * @return mean of this vector.
	 */
	public double mean() {
		int n = this.getAttCount();
		if (n == 0) return Constants.UNUSED;
		
		double sum = 0;
		for (int i = 0; i < n; i++) {
			double v = this.getValueAsReal(i);
			sum += v;
		}
		
		return sum / (double)n;
	}
	
	
	/**
	 * Calculating variance of this vector.
	 * @return variance of this vector.
	 */
	public double var() {
		int n = this.getAttCount();
		if (n < 2) return Constants.UNUSED;

		double mean = mean();
		double sum = 0;
		for (int i = 0; i < n; i++) {
			double v = this.getValueAsReal(i);
			double deviation = v - mean;
			sum += deviation * deviation;
		}
		return sum / (double)(n - 1);
	}

	
	/**
	 * Calculating variance of this vector according to maximum likelihood estimation method.
	 * @return variance of this vector according to maximum likelihood estimation method.
	 */
	public double mleVar() {
		int n = this.getAttCount();
		if (n == 0) return Constants.UNUSED;

		double mean = mean();
		double sum = 0;
		for (int i = 0; i < n; i++) {
			double v = this.getValueAsReal(i);
			double deviation = v - mean;
			sum += deviation * deviation;
		}
		return sum / (double)n;
	}

	
	/**
	 * Calculating standard deviation of this vector.
	 * @return standard deviation of this vector.
	 */
	public double sd() {
		return Math.sqrt(var());
	}

	
	/**
	 * Calculating standard error of this vector.
	 * @return standard error of this vector.
	 */
	public double se() {
		int n = this.getAttCount();
		if (n == 0)
			return Constants.UNUSED;
		else
			return Math.sqrt(var() / n);
	}

	
	/**
	 * Calculating Euclidean distance between this vector and the other vector.
	 * @param other other vector.
	 * @return Euclidean distance between this vector and the other vector.
	 */
	public double distance(DocumentVector other) {
		double dis = 0;
		
		int n = Math.min(this.getAttCount(), other.getAttCount());
		for (int i = 0; i < n; i++) {
			double deviate =  this.getValueAsReal(i) - other.getValueAsReal(i);
			dis += deviate * deviate;
		}
		return Math.sqrt(dis);
	}

	
	/**
	 * Calculating the dot product (scalar product) of this vector and the other vector.
	 * @param other other vector
	 * @return dot product (scalar product) of this vector and the other vector.
	 */
	public double product(DocumentVector other) {
		double product = 0;
		int n = Math.min(this.getAttCount(), other.getAttCount());
		for (int i = 0; i < n; i++) {
			product += this.getValueAsReal(i) * other.getValueAsReal(i);
		}
		
		return product;
	}

	
	/**
	 * Calculating the cosine of this vector and the other vector.
	 * @param other other vector.
	 * @return cosine of this vector and the other vector.
	 */
	public double cosine(DocumentVector other) {
		double module1 = module();
		double module2 = other.module();
		if (module1 == 0 || module2 == 0)
			return Constants.UNUSED;
		else
			return product(other) / ( module1 * module2);
	}

	
	/**
	 * Calculating the normalized cosine of this vector and the other vector.
	 * @param other other vector.
	 * @param average averaged value.
	 * @return normalized cosine of this vector and the other vector.
	 */
	public double cosine(DocumentVector other, double average) {
		double product = 0;
		double length1 = 0;
		double length2 = 0;
		int n = Math.min(this.getAttCount(), other.getAttCount());
		for (int i = 0; i < n; i++) {
			double value1 = this.getValueAsReal(i) - average;
			double value2 = other.getValueAsReal(i) - average;
			
			length1 += value1 * value1;
			length2 += value2 * value2;
			product += value1 * value2;
		}
		
		if (length1 == 0 || length2 == 0)
			return Constants.UNUSED;
		else
			return product / Math.sqrt(length1 * length2);
	}

	
	/**
	 * Calculating the correlation coefficient (Pearson coefficient) of this vector and the other vector.
	 * @param other other vector.
	 * @return correlation coefficient of this vector and the other vector.
	 */
	public double corr(DocumentVector other) {
		double mean1 = mean();
		double mean2 = other.mean();
		
		int n = Math.min(this.getAttCount(), other.getAttCount());
		double VX = 0, VY = 0;
		double VXY = 0;
		for (int i = 0; i < n; i++) {
			double deviate1 = this.getValueAsReal(i) - mean1;
			double deviate2 = other.getValueAsReal(i) - mean2;
			
			VX += deviate1 * deviate1;
			VY += deviate2 * deviate2;
			VXY += deviate1 * deviate2;
		}
		
		if (VX == 0 || VY == 0)
			return Constants.UNUSED;
		else
			return VXY / Math.sqrt(VX * VY);
	}

	
	/**
	 * Computing SMTP measure with other document. SMTP measure is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
	 * @param other other document.
	 * @param lambda lambda.
	 * @param vars variances.
	 * @author Yung-Shen Lin, Jung-Yi Jiang, and Shie-Jue Lee.
	 * @return SMTP measure with other document.
	 */
	public double smtp(DocumentVector other, double lambda, List<Double> vars) {
		int n = this.getAttCount();
		if (n == 0) return Constants.UNUSED;

		double a = 0, b = 0;
		for (int k = 0; k < n; k++) {
			double d1 = this.getValueAsReal(k);
			double d2 = other.getValueAsReal(k);
			double var = vars.get(k);
			if (d1 * d2 > 0) {
				double bias = d1 - d2;
				a += 0.5 * (1 + Math.exp(-bias*bias / var));
				b += 1;
			}
			else if (d1 == 0 && d2 == 0) {
				a += 0;
				b += 0;
			}
			else {
				a += -lambda;
				b += 1;
			}
		}
		
		return (a/b + lambda) / (1 + lambda);
	}
	
	
	/**
	 * Computing SMTP measure with other document. SMTP measure is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue LeeSMTP measure is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
	 * @param other other document vector.
	 * @param lambda lambda.
	 * @param vars variances.
	 * @author Yung-Shen Lin, Jung-Yi Jiang, and Shie-Jue Lee.
	 * @return SMTP measure with other document vector.
	 */
	public double smtp(DocumentVector other, double lambda, double[] vars) {
		return smtp(other, lambda, DSUtil.toDoubleList(vars));
	}
	
	
//	/**
//	 * Computing Amer measure with other document. Amer measure is developed by Ali Amer, and implemented by Loc Nguyen.
//	 * @param other other document vector.
//	 * @param binThreshold binary threshold.
//	 * @param N the number of common fields.
//	 * @author Ali Amer.
//	 * @return Amer measure with other document.
//	 */
//	public double amer(DocumentVector other, double binThreshold, int N) {
//		int n = this.getAttCount();
//		if (n == 0 || N <= 0) return Constants.UNUSED;
//		
//		int Na = 0, Nb = 0, Nab = 0;
//		n = Math.min(n,  other.getAttCount());
//		for (int i = 0; i < n; i++) {
//			boolean v1 = this.getValueAsReal(i) >= binThreshold ? true : false;
//			boolean v2 = other.getValueAsReal(i) >= binThreshold ? true : false;
//			if (v1) Na++;
//			if (v2) Nb++;
//			if (v1 && v2) Nab++;
//		}
//		return 0;
//	}
	
	
	/**
	 * Subtracting this vector by specified vector.
	 * @param that specified vector.
	 * @return resulted vector from subtracting this vector by specified vector.
	 */
	public DocumentVector subtract(DocumentVector that) {
		int n = Math.min(this.getAttCount(), that.getAttCount());
		DocumentVector result = new DocumentVector(n, 0);
		for (int i = 0; i < n; i++) {
			result.setValue(i, this.getValueAsReal(i) - that.getValueAsReal(i));
		}
		
		return result;
	}

	
	/**
	 * Adding this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from adding this vector and specified vector.
	 */
	public DocumentVector add(DocumentVector that) {
		int n = Math.min(this.getAttCount(), that.getAttCount());
		DocumentVector result = new DocumentVector(n, 0);
		for (int i = 0; i < n; i++) {
			result.setValue(i, this.getValueAsReal(i) + that.getValueAsReal(i));
		}
		
		return result;
	}

	
	/**
	 * Multiplying this vector by specified number.
	 * @param alpha specified number.
	 * @return resulted vector from multiplying this vector by specified number. 
	 */
	public DocumentVector multiply(double alpha) {
		DocumentVector result = new DocumentVector(this.getAttCount(), 0);
		for (int i = 0; i < this.getAttCount(); i++) {
			result.setValue(i, alpha * this.getValueAsReal(i));
		}
		
		return result;
	}

	
	/**
	 * Dividing this vector by specified number.
	 * @param alpha specified number.
	 * @return resulted vector from dividing this vector by specified number.
	 */
	public DocumentVector divide(double alpha) {
		DocumentVector result = new DocumentVector(this.getAttCount(), 0);
		for (int i = 0; i < this.getAttCount(); i++) {
			result.setValue(i, this.getValueAsReal(i) / alpha);
		}
		
		return result;
	}

	
}
