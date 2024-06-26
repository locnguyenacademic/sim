package net.temp;

import net.hudup.core.logistic.MathUtil;

/**
 * This is temporal testing class.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
public class Test {

	
//	@Override
//	protected Object expectation(Object currentParameter, Object... info) throws Exception {
//		@SuppressWarnings("unchecked")
//		List<ExchangedParameter> parameters = (List<ExchangedParameter>)currentParameter;
//		@SuppressWarnings("unchecked")
//		List<LargeStatistics> stats = (List<LargeStatistics>)super.expectation(currentParameter, info);
//		
//		//Adjusting large statistics.
//		int N = stats.get(0).getZData().size(); //Suppose all models have the same data.
//		int n = stats.get(0).getXData().get(0).length;  //Suppose all models have the same data.
//		List<double[]> xData = Util.newList(N);
//		List<double[]> zData = Util.newList(N);
//		List<double[]> xDataTemp = Util.newList(N);
//		for (int i = 0; i < N; i++) {
//			double[] xVector = new double[n];
//			Arrays.fill(xVector, 0.0);
//			xVector[0] = 1;
//			xData.add(xVector);
//			
//			double[] xVectorTemp = new double[n];
//			Arrays.fill(xVectorTemp, 0.0);
//			xVectorTemp[0] = 1;
//			xDataTemp.add(xVectorTemp);
//
//			double[] zVector = new double[2];
//			zVector[0] = 1;
//			zVector[1] = 0;
//			zData.add(zVector);
//		}
//		for (int k = 0; k < this.rems.size(); k++) {
//			double coeff = parameters.get(k).getCoeff();
//			LargeStatistics stat = stats.get(k);
//			
//			for (int i = 0; i < N; i++) {
//				double[] zVector = zData.get(i);
//				double zValue = stat.getZData().get(i)[1];
//				if (!Util.isUsed(this.data.getZData().get(i)[1]))
//					zVector[1] += coeff * zValue;
//				else
//					zVector[1] = zValue; 
//			}
//		}
//		for (int k = 0; k < this.rems.size(); k++) {
//			List<double[]> betas = parameters.get(k).getBetas(); //All PRMs have the same betas coefficients.
//			for (int i = 0; i < N; i++) {
//				double[] xVectorTemp = xDataTemp.get(i);
//				double[] zVector = zData.get(i);
//				for (int j = 1; j < n; j++) {
//					if (!Util.isUsed(this.data.getXData().get(i)[j]))
//						xVectorTemp[j] = betas.get(j)[0] + betas.get(j)[1] * zVector[1];
//					else
//						xVectorTemp[j] = this.data.getXData().get(i)[j];
//				}
//			}
//		}
//		for (int k = 0; k < this.rems.size(); k++) {
//			double coeff = parameters.get(k).getCoeff();
//			
//			for (int i = 0; i < N; i++) {
//				double[] xVector = xData.get(i);
//				double[] xVectorTemp = xDataTemp.get(i);
//				for (int j = 1; j < n; j++) {
//					xVector[j] += coeff * xVectorTemp[j]; // This assignment is right with assumption of same P(Y=k). 
//				}
//			}
//		}
//		
//		//All regression models have the same large statistics.
//		stats.clear();
//		LargeStatistics stat = new LargeStatistics(xData, zData);
//		for (RegressionEMImpl rem : this.rems) {
//			rem.setStatistics(stat);
//			stats.add(stat);
//		}
//		
//		return stats;
//	}

	
	/**
	 * Main method.
	 * @param args argument parameter.
	 * @throws Exception if any error raises.
	 */
	public static void main(String[] args) throws Exception {
//		RegressionEvaluator.main(args);
//		double a = Double.MAX_VALUE;
//		System.out.println(net.hudup.regression.em.ExchangedParameter.normalPDF(0, 0, 0));
		
		
//		DocumentVector d1 = new DocumentVector(new double[] {1, 0, 1});
//		DocumentVector d2 = new DocumentVector(new double[] {0, 1, 0});
//		System.out.println("SMTP of <1, 0, 1> and <1, 1, 0> is " + 
//				d1.smtp(d2, 0, new double[] {1, 1, 1}));

		
//		List<double[]> alphas = Util.newList(2);
//		alphas.add(new double[] {0, 1});
//		alphas.add(new double[] {1, -1});
//		List<Double> probs = Util.newList(2);
//		probs.add(0.5);
//		probs.add(0.5);
//		List<Double> variances = Util.newList(2);
//		variances.add(0.001);
//		variances.add(0.001);
//		
//		LargeStatistics stats = RMAbstract.generate2DRegressiveGaussianDataWithXIntervals(alphas, probs, variances, 1000);
//		UriAssoc uriAssoc = Util.getFactory().createUriAssoc(xURI.create(new File(".")));
//		Writer writer = uriAssoc.getWriter(xURI.create(new File("/a.csv")), false);
//		stats.save(writer, -1);
//		writer.close();
		
//		//NormalDistribution n = new NormalDistribution(0, 0);
//		System.out.println(Float.MIN_VALUE);
//		System.out.println(ExchangedParameter.normalPDF(0, 0, Double.MIN_VALUE));
//		System.out.println(ExchangedParameter.normalPDF(0, 0, 0));
//		System.out.println(ExchangedParameter.normalPDF(100, 100, 0));
//		System.out.println(ExchangedParameter.normalPDF(100, 100, 0));
//		System.out.println(1.0 / Float.MIN_VALUE);
		
//		System.out.println(Math.log(Float.MIN_VALUE));
//		System.out.println(Math.log(2 + Float.MIN_VALUE));
		
		System.out.println(MathUtil.format(0001000.4543623627));

	}

	
}
