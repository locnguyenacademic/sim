/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.cf.nb.NeighborCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.recommend.Accuracy;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.Vector2;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.DocumentVector;
import net.hudup.evaluate.ui.EvaluateGUI;

/**
 * This class sets up an advanced version of nearest neighbors collaborative filtering algorithm with more similarity measures.
 * <br>
 * There are many authors who contributed measure to this class.<br>
 * <br>
 * Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu contributed PSS measures and NHSM measure.<br>
 * <br>
 * Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi contributed BC and BCF measures.<br>
 * <br>
 * Hyung Jun Ahn contributed PIP measure.<br>
 * <br>
 * Keunho Choi and Yongmoo Suh contributed PC measure.<br>
 * <br>
 * Suryakant and Tripti Mahara contributed MMD measure and CjacMD measure.<br>
 * <br>
 * Junmei Feng, Xiaoyi Fengs, Ning Zhang, and Jinye Peng contributed Feng model.<br>
 * <br>
 * Yi Mua, Nianhao Xiao, Ruichun Tang, Liang Luo, and Xiaohan Yin contributed Mu measure.<br>
 * <br>
 * Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee contributed SMTP measure.<br>
 * <br>
 * Ali Amer contributed measures SMD, SMD2, and NNSM.<br>
 * <br>
 * Loc Nguyen contributed TA (triangle area) measure.<br>
 * <br>
 * Ali Amer and Loc Nguyen contributed quasi-TfIdf measure. Quasi-TfIdf measure is an extension of SMD2 measure and the ideology of TF and IDF.<br>
 * <br>
 * Shunpan Liang, Lin Ma, and Fuyong Yuan contributed improved Jaccard (IJ) measure.<br>
 * <br>
 * Sujoy Bag, Sri Krishna Kumar, and Manoj Kumar Tiwari contributed relevant Jaccard (RJ) measure.<br>
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class NeighborCFExt extends NeighborCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Value bins.
	 */
	protected static final String VALUE_BINS_FIELD = "value_bins";

	
	/**
	 * Default value bins.
	 */
	protected static final String VALUE_BINS_DEFAULT = "1, 2, 3, 4, 5";

	
	/**
	 * BCF median mode.
	 */
	protected static final String BCF_MEDIAN_MODE_FIELD = "bcf_median";

	
	/**
	 * Default BCF median mode.
	 */
	protected static final boolean BCF_MEDIAN_MODE_DEFAULT = true;

	
	/**
	 * Mu alpha field.
	 */
	protected static final String MU_ALPHA_FIELD = "mu_alpha";

	
	/**
	 * Default Mu alpha.
	 */
	protected static final double MU_ALPHA_DEFAULT = 0.5;

	
	/**
	 * Name of lambda field.
	 */
	protected static final String SMTP_LAMBDA_FIELD = "smtp_lambda";

	
	/**
	 * Default lambda field.
	 */
	protected static final double SMTP_LAMBDA_DEFAULT = 0.5;

	
	/**
	 * Name of general variance field.
	 */
	protected static final String SMTP_GENERAL_VAR_FIELD = "smtp_general_var";

	
	/**
	 * Default general variance field.
	 */
	protected static final boolean SMTP_GENERAL_VAR_DEFAULT = false;

	
	/**
	 * TA normalized mode.
	 */
	protected static final String TA_NORMALIZED_FIELD = "ta_normalized";

	
	/**
	 * Default TA normalized mode.
	 */
	protected static final boolean TA_NORMALIZED_DEFAULT = false;

	
	/**
	 * Value bins.
	 */
	protected List<Double> valueBins = Util.newList();
	
	
	/**
	 * Rank bins.
	 */
	protected Map<Double, Integer> rankBins = Util.newMap();
	
	
	/**
	 * Value cache.
	 */
	protected Map<Integer, Object> valueCache = Util.newMap();

	
	/**
	 * Default constructor.
	 */
	public NeighborCFExt() {

	}


	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		super.setup(dataset, params);
		
		this.valueBins = extractConfigValueBins();
		this.rankBins = convertValueBinsToRankBins(this.valueBins);
		
		this.valueCache.clear();
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		
		this.rankBins.clear();
		this.valueBins.clear();
		
		this.valueCache.clear();
	}


	@Override
	public List<String> getAllMeasures() {
		Set<String> mSet = Util.newSet();
		mSet.addAll(getMainMeasures());
		mSet.add(Measure.NHSM);
		mSet.add(Measure.BCFJ);
		mSet.add(Measure.CJACMD);
		mSet.add(Measure.SMD2J);
		mSet.add(Measure.QUASI_TFIDF_JACCARD);
		mSet.add(Measure.TAJ);
		mSet.add(Measure.AMER);
		
		List<String> measures = Util.newList();
		measures.addAll(mSet);
		Collections.sort(measures);
		return measures;
	}
	
	
	@Override
	public List<String> getMainMeasures() {
		List<String> measures = super.getMainMeasures();
		Set<String> mSet = Util.newSet();
		mSet.addAll(measures);
		mSet.add(Measure.PSS);
		mSet.add(Measure.BCF);
		mSet.add(Measure.SRC);
		mSet.add(Measure.PIP);
		mSet.add(Measure.PC);
		mSet.add(Measure.MMD);
		mSet.add(Measure.SMTP);
		mSet.add(Measure.SMD);
		mSet.add(Measure.SMD2);
		mSet.add(Measure.QUASI_TFIDF);
		mSet.add(Measure.TA);
		mSet.add(Measure.COCO);
		mSet.add(Measure.NNSM);
		mSet.add(Measure.IJ);
		mSet.add(Measure.RJ);
		
		measures.clear();
		measures.addAll(mSet);
		Collections.sort(measures);
		return measures;
	}


	/**
	 * Checking whether the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_FIELD}).
	 * @return true if the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_FIELD}). Otherwise, return false.
	 */
	public boolean requireDiscreteRatingBins() {
		return requireDiscreteRatingBins(getMeasure());
	}
	
	
	/**
	 * Given specified measure, checking whether the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_FIELD}).
	 * @param measure specified measure.
	 * @return true if the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_FIELD}). Otherwise, return false.
	 */
	protected boolean requireDiscreteRatingBins(String measure) {
		if (measure == null)
			return false;
		else if (measure.equals(Measure.BCF) || measure.equals(Measure.BCFJ) ||  measure.equals(Measure.MMD))
			return true;
		else
			return false;
	}

	
	@Override
	protected boolean isCachedSim() {
		String measure = getMeasure();
		if (measure == null)
			return false;
		else if (measure.equals(Measure.PC))
			return false;
		else
			return super.isCachedSim();
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object...params) {
		if (measure.equals(Measure.PSS))
			return pss(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.NHSM))
			return nhsm(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.BCF))
			return bcf(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.BCFJ))
			return bcfj(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.SRC))
			return src(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.PIP))
			return pip(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.PC)) {
			if ((params == null) || (params.length < 1) || !(params[0] instanceof Number))
				return Constants.UNUSED;
			else {
				int fixedColumnId = ((Number)(params[0])).intValue();
				return pc(vRating1, vRating2, profile1, profile2, fixedColumnId);
			}
		}
		else if (measure.equals(Measure.MMD))
			return mmd(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.CJACMD))
			return cosine(vRating1, vRating2, profile1, profile2) + mmd(vRating1, vRating2, profile1, profile2) + jaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.FENG))
			return feng(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.MU))
			return mu(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.SMTP))
			return smtp(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.AMER))
			return amer(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.SMD))
			return smd(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.SMD2))
			return smd2(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.SMD2J))
			return smd2j(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.QUASI_TFIDF))
			return quasiTfIdf(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.QUASI_TFIDF_JACCARD))
			return quasiTfIdfJaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.TA))
			return triangleArea(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.TAJ))
			return triangleAreaJaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.COCO))
			return coco(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.NNSM))
			return nnsm(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.IJ))
			return improvedJaccard(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.RJ))
			return relevantJaccard(vRating1, vRating2, profile1, profile2);
		else
			return super.sim0(measure, vRating1, vRating2, profile1, profile2, params);
	}

	
	@Override
	protected void updateConfig(String measure) {
		if (measure == null) return;
		
		config.removeReadOnly(VALUE_BINS_FIELD);
		config.removeReadOnly(COSINE_NORMALIZED_FIELD);
		config.removeReadOnly(MSD_FRACTION_FIELD);
		config.removeReadOnly(BCF_MEDIAN_MODE_FIELD);
		config.removeReadOnly(MU_ALPHA_FIELD);
		config.removeReadOnly(SMTP_LAMBDA_FIELD);
		config.removeReadOnly(SMTP_GENERAL_VAR_FIELD);
		config.removeReadOnly(TA_NORMALIZED_FIELD);
		if (measure.equals(Measure.PSS)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.NHSM)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.BCF)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.BCFJ)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.SRC)) {
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.PIP)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.PC)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.MMD)) {
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.CJACMD)) {
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.FENG)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.MU)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.SMTP)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.AMER)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.SMD)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.SMD2)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.SMD2J)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.QUASI_TFIDF)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.QUASI_TFIDF_JACCARD)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.TA)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
		}
		else if (measure.equals(Measure.TAJ)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
		}
		else if (measure.equals(Measure.COCO)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.NNSM)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else if (measure.equals(Measure.IJ)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
		}
		else {
			super.updateConfig(measure);
		}
	}


	/**
	 * Calculating the PSS measure between two pairs. PSS measure is developed by Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu.
	 * @return PSS measure between both two rating vectors and profiles.
	 */
	protected abstract double pss(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2);


	/**
	 * Calculating the PSS measure between two rating vectors. PSS measure is developed by Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu, and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param fieldMeans map of field means.
	 * @author Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu.
	 * @return PSS measure between two rating vectors.
	 */
	protected double pss(RatingVector vRating1, RatingVector vRating2, Map<Integer, Double> fieldMeans) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double pss = 0.0;
		for (int id : common) {
			double r1 = vRating1.get(id).value;
			double r2 = vRating2.get(id).value;
			
			double pro = 1.0 - 1.0 / (1.0 + Math.exp(-Math.abs(r1-r2)));
			//Note: I think that it is better to use mean instead of median for significant.
			//At the worst case, median is always approximate to mean given symmetric distribution like normal distribution.
			//Moreover, in fact, general user mean is equal to general item mean.
			//However, I still use rating median because of respecting authors' ideas.
			double sig = 1.0 / (1.0 + Math.exp(
					-Math.abs(r1-this.ratingMedian)*Math.abs(r2-this.ratingMedian)));
			double singular = 1.0 - 1.0 / (1.0 + Math.exp(-Math.abs((r1+r2)/2.0 - fieldMeans.get(id))));
			
			pss += pro * sig * singular;
		}
		
		return pss;
	}
	
	
	/**
	 * Calculating the NHSM measure between two pairs. NHSM measure is developed by Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu.
	 * @return NHSM measure between both two rating vectors and profiles.
	 */
	protected double nhsm(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		double urp = urp(vRating1, vRating2, profile1, profile2);
		double jaccard2 = jaccard2(vRating1, vRating2, profile1, profile2);
		return pss(vRating1, vRating2, profile1, profile2) * jaccard2 * urp;
	}


	/**
	 * Calculate the Bhattacharyya measure from specified rating vectors. BC measure is modified by Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi, and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi.
	 * @return Bhattacharyya measure from specified rating vectors.
	 */
	@NextUpdate
	protected double bc(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				List<Double> bins = valueBins;
				if (bins.isEmpty())
					bins = extractValueBins(vRating1, vRating2);
				
				Set<Integer> ids1 = vRating1.fieldIds(true);
				Set<Integer> ids2 = vRating2.fieldIds(true);
				int n1 = ids1.size();
				int n2 = ids2.size();
				if (n1 == 0 || n2 == 0) return Constants.UNUSED;
				
				double bc = 0;
				for (double bin : bins) {
					int count1 = 0, count2 = 0;
					for (int id1 : ids1) {
						if (vRating1.get(id1).value == bin)
							count1++;
					}
					for (int id2 : ids2) {
						if (vRating2.get(id2).value == bin)
							count2++;
					}
					
					bc += Math.sqrt( ((double)count1/(double)n1) * ((double)count2/(double)n2) ); 
				}
				
				return bc;
			}
		};
		
		return (double)cacheTask(vRating1.id(), vRating2.id(), this.columnSimCache, task);
	}

	
	/**
	 * Calculating the advanced BCF measure between two pairs. BCF measure is developed by Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi.
	 * @return BCF measure between both two rating vectors and profiles.
	 */
	@NextUpdate
	protected double bcf(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		Set<Integer> columnIds1 = vRating1.fieldIds(true);
		Set<Integer> columnIds2 = vRating2.fieldIds(true);
		if (columnIds1.size() == 0 || columnIds2.size() == 0)
			return Constants.UNUSED;
		
		double bcSum = 0;
		boolean medianMode = getConfig().getAsBoolean(BCF_MEDIAN_MODE_FIELD);
		for (int columnId1 : columnIds1) {
			RatingVector columnVector1 = getColumnRating(columnId1);
			if (columnVector1 == null) continue;
			double columnModule1 = bcfCalcColumnModule(columnVector1);
			if (!Util.isUsed(columnModule1) || columnModule1 == 0) continue;
			
			double value1 = medianMode? vRating1.get(columnId1).value-this.ratingMedian : vRating1.get(columnId1).value-vRating1.mean();
			for (int columnId2 : columnIds2) {
				RatingVector columnVector2 = columnId2 == columnId1 ? columnVector1 : getColumnRating(columnId2);
				if (columnVector2 == null) continue;
				double columnModule2 = bcfCalcColumnModule(columnVector2);
				if (!Util.isUsed(columnModule2) || columnModule2 == 0) continue;
				
				double bc = bc(columnVector1, columnVector2, profile1, profile2);
				if (!Util.isUsed(bc)) continue;

				double value2 = medianMode? vRating2.get(columnId2).value-this.ratingMedian : vRating2.get(columnId2).value-vRating2.mean();
				double loc = value1 * value2 / (columnModule1*columnModule2);
				if (!Util.isUsed(loc)) continue;
				
				bcSum += bc * loc;
			}
		}
		
		return bcSum;
	}

	
	/**
	 * Calculating the advanced BCFJ measure (BCF + Jaccard) between two pairs. BCF measure is developed by Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi.
	 * @return BCFJ measure between both two rating vectors and profiles.
	 */
	protected double bcfj(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return bcf(vRating1, vRating2, profile1, profile2) + jaccard(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating module (length) of column rating vector for BCF measure.
	 * @param columnVector specified column rating vector.
	 * @return module (length) of column rating vector.
	 */
	protected double bcfCalcColumnModule(RatingVector columnVector) {
		double ratingMedian = this.ratingMedian;
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				if (columnVector == null) return Constants.UNUSED;
				
				Set<Integer> fieldIds = columnVector.fieldIds(true);
				double columnModule = 0;
				boolean medianMode = getConfig().getAsBoolean(BCF_MEDIAN_MODE_FIELD);
				for (int fieldId : fieldIds) {
					double deviate = medianMode ? columnVector.get(fieldId).value-ratingMedian : columnVector.get(fieldId).value;
					columnModule += deviate * deviate;
				}
				
				return Math.sqrt(columnModule);
			}
		};
		
		return (double)cacheTask(columnVector.id(), this.valueCache, task);
	}

	
	/**
	 * Calculating the Spearman Rank Correlation (SRC) measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Spearman Rank Correlation (SRC) measure between both two rating vectors and profiles.
	 */
	protected double src(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Map<Double, Integer> bins = rankBins;
		if (bins.isEmpty())
			bins = extractRankBins(vRating1, vRating2);

		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double sum = 0;
		for (int id : common) {
			double v1 = vRating1.get(id).value;
			int r1 = bins.get(v1);
			double v2 = vRating2.get(id).value;
			int r2 = bins.get(v2);
			
			int d = r1 - r2;
			sum += d*d;
		}
		
		double n = common.size();
		return 1.0 - 6*sum/(n*(n*n-1));
	}
	
	
	/**
	 * Calculating the PIP measure between two pairs. PIP measure is developed by Hyung Jun Ahn, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Hyung Jun Ahn.
	 * @return NHSM measure between both two rating vectors and profiles.
	 */
	protected abstract double pip(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2);
	
	
	/**
	 * Calculating the PIP measure between two rating vectors. PIP measure is developed by Hyung Jun Ahn and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param fieldMeans map of field means.
	 * @author Hyung Jun Ahn
	 * @return PIP measure between two rating vectors.
	 */
	protected double pip(RatingVector vRating1, RatingVector vRating2, Map<Integer, Double> fieldMeans) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double pip = 0.0;
		for (int id : common) {
			double r1 = vRating1.get(id).value;
			double r2 = vRating2.get(id).value;
			boolean agreed = agree(r1, r2);
			
			double d = agreed ? Math.abs(r1-r2) : 2*Math.abs(r1-r2);
			double pro = (2*(getMaxRating()-getMinRating())+1) - d;
			pro = pro*pro;
			
			double impact = (Math.abs(r1-this.ratingMedian)+1) * (Math.abs(r2-this.ratingMedian)+1);
			if (!agreed)
				impact = 1 / impact;
			
			double mean = fieldMeans.get(id);
			double pop = 1;
			if ((r1 > mean && r2 > mean) || (r1 < mean && r2 < mean)) {
				double bias = (r1+r2)/2 - mean;
				pop = 1 + bias*bias;
			}
			
			pip += pro * impact * pop;
		}
		
		return pip;
	}

	
	/**
	 * Checking whether two ratings are agreed.
	 * @param rating1 first rating.
	 * @param rating2 second rating.
	 * @return true if two ratings are agreed.
	 */
	protected boolean agree(double rating1, double rating2) {
		if ( (rating1 > this.ratingMedian && rating2 < this.ratingMedian) || (rating1 < this.ratingMedian && rating2 > this.ratingMedian) )
			return false;
		else
			return true;
	}
	
	
	/**
	 * Calculating the PC measure between two rating vectors. PC measure is developed by Keunho Choi and Yongmoo Suh. It implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @param fixedColumnId fixed column identifier.
	 * @author Hyung Jun Ahn.
	 * @return PC measure between both two rating vectors and profiles.
	 */
	protected abstract double pc(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2, int fixedColumnId);
	
	
	/**
	 * Calculating the PC measure between two rating vectors. PC measure is developed by Keunho Choi and Yongmoo Suh. It implemented by Loc Nguyen.
	 * @param vRating1 the first rating vectors.
	 * @param vRating2 the second rating vectors.
	 * @param fixedColumnId fixed field (column) identifier.
	 * @param fieldMeans mean value of field ratings.
	 * @author Keunho Choi, Yongmoo Suh
	 * @return PC measure between two rating vectors.
	 */
	protected double pc(RatingVector vRating1, RatingVector vRating2, int fixedColumnId, Map<Integer, Double> fieldMeans) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;

		double vx = 0, vy = 0;
		double vxy = 0;
		for (int fieldId : common) {
			double mean = fieldMeans.get(fieldId);
			double d1 = vRating1.get(fieldId).value - mean;
			double d2 = vRating2.get(fieldId).value - mean;
			
			Task columnSimTask = new Task() {
				
				@Override
				public Object perform(Object...params) {
					RatingVector fixedColumnVector = getColumnRating(fixedColumnId);
					RatingVector columnVector = getColumnRating(fieldId);
					
					if (fixedColumnVector == null || columnVector == null)
						return Constants.UNUSED;
					else
						return fixedColumnVector.corr(columnVector);
				}
			};
			double columnSim = (double)cacheTask(fixedColumnId, fieldId, this.columnSimCache, columnSimTask);
			columnSim = columnSim * columnSim;
			
			vx  += d1 * d1 * columnSim;
			vy  += d2 * d2 * columnSim;
			vxy += d1 * d2 * columnSim;
		}
		
		if (vx == 0 || vy == 0)
			return Constants.UNUSED;
		else
			return vxy / Math.sqrt(vx * vy);
	}

	
	/**
	 * Calculating the Mean Measure of Divergence (MMD) measure between two pairs.
	 * Suryakant and Tripti Mahara proposed use of MMD for collaborative filtering. Loc Nguyen implements it.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Suryakant, Tripti Mahara
	 * @return MMD measure between both two rating vectors and profiles.
	 */
	protected double mmd(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> ids1 = vRating1.fieldIds(true);
		Set<Integer> ids2 = vRating2.fieldIds(true);
		int N1 = ids1.size();
		int N2 = ids2.size();
		if (N1 == 0 || N2 == 0) return Constants.UNUSED;
		
		List<Double> bins = valueBins;
		if (bins.isEmpty())
			bins = extractValueBins(vRating1, vRating2);
		double sum = 0;
		for (double bin : bins) {
			int n1 = 0, n2 = 0;
			for (int id1 : ids1) {
				if (vRating1.get(id1).value == bin)
					n1++;
			}
			for (int id2 : ids2) {
				if (vRating2.get(id2).value == bin)
					n2++;
			}
			
			double thetaBias = mmdTheta(n1, N1) - mmdTheta(n2, N2);
			sum += thetaBias*thetaBias - 1/(0.5+n1) - 1/(0.5+n2); 
		}
		
		return 1 / (1 + sum/bins.size());
	}
	
	
	/**
	 * Theta transformation of Mean Measure of Divergence (MMD) measure.
	 * The default implementation is Grewal transformation.
	 * @param n number of observations having a trait.
	 * @param N number of observations
	 * @return Theta transformation of Mean Measure of Devergence (MMD) measure.
	 */
	protected double mmdTheta(int n, int N) {
		return 1 / Math.sin(1-2*(n/N));
	}

	
	/**
	 * Calculating the Feng measure between two pairs.
	 * Junmei Feng, Xiaoyi Fengs, Ning Zhang, and Jinye Peng developed the Triangle measure. Loc Nguyen implements it.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Junmei Feng, Xiaoyi Fengs, Ning Zhang, Jinye Peng
	 * @return Feng measure between both two rating vectors and profiles.
	 */
	protected double feng(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		double s1 = coj(vRating1, vRating2, profile1, profile2);

		Set<Integer> ids1 = vRating1.fieldIds(true);
		Set<Integer> ids2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(ids1);
		common.retainAll(ids2);
		double s2 = 1 / ( 1 + Math.exp(-common.size()*common.size()/(ids1.size()*ids2.size())) );
		
		double s3 = urp(vRating1, vRating2, profile1, profile2);
		
		return s1 * s2 * s3;
	}
	
	
	/**
	 * Calculating the Mu measure between two pairs.
	 * Yi Mua, Nianhao Xiao, Ruichun Tang, Liang Luo, and Xiaohan Yin developed Mu measure. Loc Nguyen implements it.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Yi Mua, Nianhao Xiao, Ruichun Tang, Liang Luo, Xiaohan Yin
	 * @return Mu measure between both two rating vectors and profiles.
	 */
	@NextUpdate
	protected double mu(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		double alpha = config.getAsReal(MU_ALPHA_FIELD);
		double pearson = corr(vRating1, vRating2, profile1, profile2);
		double hg = 1 - bc(vRating1, vRating2, profile1, profile2);
//		double hg = bc(vRating1, vRating2, profile1, profile2);
		double jaccard = jaccard(vRating1, vRating2, profile1, profile2);
		
		return alpha*pearson + (1-alpha)*(hg+jaccard);
	}
	
	
	/**
	 * Calculating the SMTP measure between two pairs. SMTP is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee.
	 * @return SMTP measure between both two rating vectors.
	 */
	protected double smtp(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		List<Integer> common = commonFieldIdsAsList(vRating1, vRating2);
		common.retainAll(getItemVars().keySet());
		if (common.size() == 0) return Constants.UNUSED;
		
		double[] data1 = new double[common.size()];
		double[] data2 = new double[common.size()];
		double[] vars = new double[common.size()];
		boolean useGeneralVar = getConfig().getAsBoolean(SMTP_GENERAL_VAR_FIELD);
		for (int i = 0; i < common.size(); i++) {
			int id = common.get(i);
			
			data1[i] = vRating1.get(id).value; 
			data2[i] = vRating2.get(id).value;
			if (useGeneralVar)
				vars[i] = getRatingVar();
			else
				vars[i] = getItemVars().get(id);
		}

		DocumentVector vector1 = new DocumentVector(data1);
		DocumentVector vector2 = new DocumentVector(data2);
		
		double lamda = getConfig().getAsReal(SMTP_LAMBDA_FIELD);
		return vector1.smtp(vector2, lamda, vars);
	}
	
	
	/**
	 * Calculating the SMD measure between two pairs. SMD measure is developed by Ali Amer, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer.
	 * @return SMD measure between both two rating vectors and profiles.
	 */
	protected double smd(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> fieldIds1 = vRating1.fieldIds(true);
		Set<Integer> fieldIds2 = vRating2.fieldIds(true);
		Set<Integer> union = Util.newSet(fieldIds1.size());
		union.addAll(fieldIds1);
		union.addAll(fieldIds2);
		
		int Nab = 0;
		for (int itemId : union) {
			boolean rated1 = vRating1.isRated(itemId);
			boolean rated2 = vRating2.isRated(itemId);
			
			if (rated1 == rated2) Nab++;
		}
		
		double M = fieldIds1.size() + fieldIds2.size();
		double N = union.size();
		return Nab * (1/M + 0.5/N);
	}
	
	
//	/**
//	 * Calculating the Amer-Threshold measure between two pairs. Amer measure is developed by Ali Amer, and converted by Loc Nguyen.
//	 * The first pair includes the first rating vector and the first profile.
//	 * The second pair includes the second rating vector and the second profile.
//	 * 
//	 * @param vRating1 first rating vector.
//	 * @param vRating2 second rating vector.
//	 * @param profile1 first profile.
//	 * @param profile2 second profile.
//	 * @param itemIds set of all item identifiers
//	 * @author Ali Amer, Loc Nguyen
//	 * @return Amer-Threshold measure between both two rating vectors and profiles.
//	 */
//	protected double amerThreshold(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
//		double sim = 0;
//		double sum = 0;
//		Set<Integer> itemIds = unionFieldIds(vRating1, vRating2);
//		for (int itemId : itemIds) {
//			boolean rated1 = vRating1.isRated(itemId);
//			boolean rated2 = vRating2.isRated(itemId);
//			
//			if (rated1 == rated2) {
//				double sim0 = 1 + 1/(1 + Math.abs(vRating1.get(itemId).value-vRating2.get(itemId).value));
//				sim += sim0;
//				sum += sim0;
//			}
//			else
//				sum += 1;
//		}
//		
//		return sim / sum;
//	}
	
	
	/**
	 * Calculating the Amer measure between two pairs. Amer measure is developed by Ali Amer, and converted by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer, Loc Nguyen
	 * @return Amer measure between both two rating vectors and profiles.
	 */
	protected double amer(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> fieldIds1 = vRating1.fieldIds(true);
		Set<Integer> fieldIds2 = vRating2.fieldIds(true);
		Set<Integer> union = Util.newSet(fieldIds1.size());
		union.addAll(fieldIds1);
		union.addAll(fieldIds2);
		
		int Nab = 0;
		for (int fieldId : union) {
			boolean rated1 = vRating1.isRated(fieldId);
			boolean rated2 = vRating2.isRated(fieldId);
			if (rated1 == rated2)
				Nab++;
			else
				continue;
			
			boolean relevant1 = Accuracy.isRelevant(vRating1.get(fieldId).value, ratingMedian);
			boolean relevant2 = Accuracy.isRelevant(vRating2.get(fieldId).value, ratingMedian);
			if (relevant1 == relevant2) Nab++;
		}
		
		double M = fieldIds1.size() + fieldIds2.size();
		double N = union.size();
		return Nab * (0.5/M + 0.25/N);
	}

	
	/**
	 * Calculating the Amer2 measure between two pairs. Amer2 measure is developed by Ali Amer.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer
	 * @return Amer2 measure between both two rating vectors and profiles.
	 */
	protected double amer2(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> fieldIds1 = vRating1.fieldIds(true);
		Set<Integer> fieldIds2 = vRating2.fieldIds(true);
		Set<Integer> union = Util.newSet(fieldIds1.size());
		union.addAll(fieldIds1);
		union.addAll(fieldIds2);
		
		int Nab = 0, Na = 0, Nb = 0;
		for (int fieldId : union) {
			boolean rated1 = vRating1.isRated(fieldId);
			boolean rated2 = vRating2.isRated(fieldId);
			
			if (rated1) {
				boolean relevant1 = Accuracy.isRelevant(vRating1.get(fieldId).value, ratingMedian);
				if (rated2) {
					boolean relevant2 = Accuracy.isRelevant(vRating2.get(fieldId).value, ratingMedian);
					if (relevant2) {
						Na++;
						Nb++;
						if (relevant1) Nab++;
					}
					else if (relevant1)
						Nb++;
				}
				else if (relevant1)
					Na++;
			}
			else if (rated2) {
				boolean relevant2 = Accuracy.isRelevant(vRating2.get(fieldId).value, ratingMedian);
				if (relevant2) Nb++;
			}
		}
		
		return 2.0*Nab / (double)(Na + Nb);
	}

	
	/**
	 * Calculating the Amer3 measure between two pairs. Amer3 measure is developed by Ali Amer, and converted by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer, Loc Nguyen
	 * @return Amer3 measure between both two rating vectors and profiles.
	 */
	protected double amer3(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> fieldIds1 = vRating1.fieldIds(true);
		Set<Integer> fieldIds2 = vRating2.fieldIds(true);
		Set<Integer> union = Util.newSet(fieldIds1.size());
		union.addAll(fieldIds1);
		union.addAll(fieldIds2);
		
		int Nab = 0;
		for (int fieldId : union) {
			boolean rated1 = vRating1.isRated(fieldId);
			boolean rated2 = vRating2.isRated(fieldId);
			if (rated1 == rated2)
				Nab++;
			else
				continue;
			
			boolean relevant1 = Accuracy.isRelevant(vRating1.get(fieldId).value, ratingMedian);
			boolean relevant2 = Accuracy.isRelevant(vRating2.get(fieldId).value, ratingMedian);
			if (relevant1 == relevant2) Nab++;
		}
		
		return 2.0 * Nab / (double)union.size();
	}

	
	/**
	 * Calculating the SMD2 measure between two pairs. SMD2 measure is developed by Ali Amer, and implemented by Loc Nguyen.
	 * SMD2 measure is only applied into positive ratings.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer.
	 * @return SMD2 measure between both two rating vectors and profiles.
	 */
	protected double smd2(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> fieldIds = unionFieldIds(vRating1, vRating2);
		if (fieldIds.size() == 0) return Constants.UNUSED;
		
		double X = 0, Y = 0, U = 0, V = 0;
		for (int fieldId : fieldIds) {
			boolean rated1 = vRating1.isRated(fieldId);
			boolean rated2 = vRating2.isRated(fieldId);
			
			if (rated1) {
				double value = vRating1.get(fieldId).value;
				U += value;
				if (!rated2) X += value;
			}
			if (rated2) {
				double value = vRating2.get(fieldId).value;
				V += value;
				if (!rated1) Y += value;
			}
		}
		
		double F = X * Y;
		double N = U * V;
		return 1.0 - (F + 1.0) / N;
	}

	
	/**
	 * Calculating the SMD2 + Jaccard measure between two pairs. SMD2 measure is developed by Ali Amer, and implemented by Loc Nguyen.
	 * SMD2 + Jaccard measure is only applied into positive ratings.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer.
	 * @return SMD2 + Jaccard measure between both two rating vectors and profiles.
	 */
	protected double smd2j(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return smd2(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the quasi-TfIdf measure between two pairs. Quasi-TfIdf measure is developed by Ali Amer and Loc Nguyen.
	 * Quasi-TfIdf measure is an extension of SMD2 measure and the ideology of TF and IDF.
	 * Quasi-TfIdf measure is only applied into positive ratings.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer, Loc Nguyen.
	 * @return Quasi-TfIdf measure between both two rating vectors.
	 */
	protected double quasiTfIdf(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> fieldIds = unionFieldIds(vRating1, vRating2);
		if (fieldIds.size() == 0) return Constants.UNUSED;
		
		double X1 = 0, Y1 = 0, X2 = 0, Y2 = 0, U = 0, V = 0;
		for (int fieldId : fieldIds) {
			boolean rated1 = vRating1.isRated(fieldId);
			boolean rated2 = vRating2.isRated(fieldId);
			
			if (rated1) {
				double value1 = vRating1.get(fieldId).value;
				U += value1;
				if (rated2)
					X1 += value1;
				else if (!rated2)
					X2 += value1;
			}
			
			if (rated2) {
				double value2 = vRating2.get(fieldId).value;
				V += value2;
				if (rated1)
					Y1 += value2;
				else if (!rated1)
					Y2 += value2;
			}
		}
		
		double N = U * V;
		return ((X1*Y1)/N) * (1.0 - (X2*Y2)/N);
	}

	
	/**
	 * Calculating the quasi-TfIdf + Jaccard measure between two pairs. Quasi-TfIdf measure is developed by Ali Amer and Loc Nguyen.
	 * Quasi-TfIdf + Jaccard measure is an extension of SMD2 measure and the ideology of TF and IDF.
	 * Quasi-TfIdf + Jaccard measure is only applied into positive ratings.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer, Loc Nguyen.
	 * @return Quasi-TfIdf + Jaccard measure between both two rating vectors.
	 */
	protected double quasiTfIdfJaccard(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> fieldIds = unionFieldIds(vRating1, vRating2);
		if (fieldIds.size() == 0) return Constants.UNUSED;
		
		double X1 = 0, Y1 = 0, X2 = 0, Y2 = 0, U = 0, V = 0;
		int commonCount = 0;
		for (int fieldId : fieldIds) {
			boolean rated1 = vRating1.isRated(fieldId);
			boolean rated2 = vRating2.isRated(fieldId);
			
			if (rated1) {
				double value1 = vRating1.get(fieldId).value;
				U += value1;
				if (rated2) {
					X1 += value1;
					commonCount ++;
				}
				else if (!rated2)
					X2 += value1;
			}
			
			if (rated2) {
				double value2 = vRating2.get(fieldId).value;
				V += value2;
				if (rated1)
					Y1 += value2;
				else if (!rated1)
					Y2 += value2;
			}
		}
		
		double N = U * V;
		double jac = (double)commonCount / (double)fieldIds.size();
		return ((X1*Y1)*jac/N) * (1.0 - (X2*Y2)*(1.0-jac)/N);
	}

	
	/**
	 * Calculating the TA (triangle area) measure between two pairs. TA is developed by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * The current version does not support positive cosine. The next version will fix it.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Loc Nguyen.
	 * @return TA measure between both two rating vectors and profiles.
	 */
	protected double triangleArea(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		Vector2 v1 = new Vector2(common.size(), 0);
		Vector2 v2 = new Vector2(common.size(), 0);
		boolean normalized = getConfig().getAsBoolean(TA_NORMALIZED_FIELD);
		if (normalized) {//Normalized mode
			for (int id : common) {
				v1.add(vRating1.get(id).value - this.ratingMedian);
				v2.add(vRating2.get(id).value - this.ratingMedian);
			}
		}
		else {
			for (int id : common) {
				v1.add(vRating1.get(id).value);
				v2.add(vRating2.get(id).value);
			}
		}
		
		double a = v1.module();
		double b = v2.module();
		if (a == 0 || b == 0) return Constants.UNUSED;
		
		double p = v1.product(v2);
		if (p >= 0) {
			if (a < b)
				return p*p / (a*b*b*b);
			else
				return p*p / (a*a*a*b);
		}
		else {
			if (a < b)
				return p / (b*b);
			else
				return p / (a*a);
		}
	}

	
	/**
	 * Calculating the TAJ (triangle area + Jaccard) measure between two pairs. TAJ is developed by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * The current version does not support positive cosine. The next version will fix it.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Loc Nguyen
	 * @return TAJ measure between both two rating vectors and profiles.
	 */
	protected double triangleAreaJaccard(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return triangleArea(vRating1, vRating2, profile1, profile2) * jaccard(vRating1, vRating2, profile1, profile2);
	}

	
	/**
	 * Calculating the circle dot product of two rating vectors given two sets of field identifiers.
	 * @param vRating1 rating vector 1.
	 * @param vRating2 rating vector 1.
	 * @param fieldIds1 set 1 of field identifiers.
	 * @param fieldIds2 set 1 of field identifiers.
	 * @return circle dot product of two rating vectors given two sets of field identifiers.
	 */
	private double circleDotProduct0(RatingVector vRating1, RatingVector vRating2, Set<Integer> fieldIds1, Set<Integer> fieldIds2) {
		double product = 0;
		for (int fieldId1 : fieldIds1) {
			double value1 = vRating1.get(fieldId1).value;
			
			for (int fieldId2 : fieldIds2) {
				double value2 = vRating2.get(fieldId2).value;
				product += value1 * value2;
			}
		}
		
		return product;
	}
	
	
	/**
	 * Calculating the circle dot product of two rating vectors given two sets of field identifiers.
	 * @param vRating1 rating vector 1.
	 * @param vRating2 rating vector 1.
	 * @param fieldIds1 set 1 of field identifiers.
	 * @param fieldIds2 set 1 of field identifiers.
	 * @return circle dot product of two rating vectors given two sets of field identifiers.
	 */
	protected double circleDotProduct(RatingVector vRating1, RatingVector vRating2, Set<Integer> fieldIds1, Set<Integer> fieldIds2) {
		double product = 0;
		for (int fieldId1 : fieldIds1) {
			if (!vRating1.isRated(fieldId1)) continue;
			
			double value1 = vRating1.get(fieldId1).value;
			for (int fieldId2 : fieldIds2) {
				if (!vRating2.isRated(fieldId2)) continue;

				double value2 = vRating2.get(fieldId2).value;
				product += value1 * value2;
			}
		}
		
		return product;
	}

	
	/**
	 * Calculating the circle dot product of two rating vectors.
	 * @param vRating1 rating vector 1.
	 * @param vRating2 rating vector 1.
	 * @return circle dot product of two rating vectors.
	 */
	protected double circleDotProduct(RatingVector vRating1, RatingVector vRating2) {
		return circleDotProduct0(vRating1, vRating2,
				vRating1.fieldIds(true), vRating2.fieldIds(true));
	}

	
	/**
	 * Calculating the circle dot length of specified rating vector given field identifiers.
	 * @param vRating specified rating vector.
	 * @param fieldIds given field identifiers.
	 * @return circle dot length of specified rating vector given field identifiers.
	 */
	private double circleLength0(RatingVector vRating, Set<Integer> fieldIds) {
		double length = 0;
		for (int fieldId : fieldIds) {
			double value = vRating.get(fieldId).value;
			length += value*value;
		}
		
		return Math.sqrt(length);
	}

	
	/**
	 * Calculating the circle length of specified rating vector given field identifiers.
	 * @param vRating specified rating vector.
	 * @param fieldIds given field identifiers.
	 * @return circle length of specified rating vector given field identifiers.
	 */
	protected double circleLength(RatingVector vRating, Set<Integer> fieldIds) {
		double length = 0;
		for (int fieldId : fieldIds) {
			if (!vRating.isRated(fieldId)) continue;
			
			double value = vRating.get(fieldId).value;
			length += value*value;
		}
		
		return Math.sqrt(length);
	}

	
	/**
	 * Calculating the circle length of specified rating vector.
	 * @param vRating specified rating vector.
	 * @return circle length of specified rating vector.
	 */
	protected double circleLength(RatingVector vRating) {
		return circleLength0(vRating, vRating.fieldIds(true));
	}

	
	/**
	 * Calculating the coco measure of two rating vectors given two sets of field identifiers.
	 * @param vRating1 rating vector 1.
	 * @param vRating2 rating vector 1.
	 * @param fieldIds1 set 1 of field identifiers.
	 * @param fieldIds2 set 1 of field identifiers.
	 * @return coco measure of two rating vectors given two sets of field identifiers.
	 */
	protected double coco(RatingVector vRating1, RatingVector vRating2, Set<Integer> fieldIds1, Set<Integer> fieldIds2) {
		return circleDotProduct(vRating1, vRating2, fieldIds1, fieldIds2) /
				(circleLength(vRating1, fieldIds1)*circleLength(vRating2, fieldIds2));
	}

	
	/**
	 * Calculating the Coco measure between two pairs. Coco is developed by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Loc Nguyen
	 * @return Coco measure between both two rating vectors and profiles.
	 */
	protected double coco(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> fieldIds1 = vRating1.fieldIds(true);
		double length1 = 0;
		for (int fieldId1 : fieldIds1) {
			double value1 = vRating1.get(fieldId1).value;
			length1 += value1*value1;
		}
		
		Set<Integer> fieldIds2 = vRating2.fieldIds(true);
		double length2 = 0;
		for (int fieldId2 : fieldIds2) {
			double value2 = vRating2.get(fieldId2).value;
			length2 += value2*value2;
		}
		
		return (vRating1.sum()*vRating2.sum()) / Math.sqrt(length1*length2);
	}

	
	/**
	 * Calculating the numerical nearby similarity measure (NNSM) between two pairs. NNSM is developed by Ali Amer.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer
	 * @return numerical nearby similarity measure (NNSM) between both two rating vectors and profiles.
	 */
	protected double nnsm(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		Set<Integer> fieldIds = commonFieldIds(vRating1, vRating2);
		int n = fieldIds.size();
		double product = 0;
		for (int fieldId : fieldIds) {
			double value1 = vRating1.get(fieldId).value;
			double value2 = vRating2.get(fieldId).value;
			product += value1 * value2;
		}
		
		double sum1 = vRating1.sum();
		int n1 = vRating1.count(true);
		double sum2 = vRating2.sum();
		int n2 = vRating2.count(true);
		
		return (n*product) / (n1*sum1+n2*sum2);
	}
	
	
	/**
	 * Calculating the improved Jaccard (IJ) measure between two pairs.
	 * Shunpan Liang, Lin Ma, and Fuyong YuanShunpan Liang, Lin Ma, and Fuyong Yuan developed the improved Jaccard (IJ) measure. Loc Nguyen implements it.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Improved Jaccard (IJ) measure between both two rating vectors and profiles.
	 * @author Shunpan Liang, Lin Ma, Fuyong Yuan
	 */
	protected double improvedJaccard(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> PA = Util.newSet(), NA = Util.newSet(), D = Util.newSet(), PO = Util.newSet(), NO = Util.newSet();
		Set<Integer> ids = unionFieldIds(vRating1, vRating2);
		if (ids.size() == 0) return Constants.UNUSED;
		
		for (int id : ids) {
			if (vRating1.isRated(id) && vRating2.isRated(id)) {
				double v1 = vRating1.get(id).value;
				double v2 = vRating2.get(id).value;
				if (Accuracy.isRelevant(v1, this.ratingMedian) && Accuracy.isRelevant(v2, this.ratingMedian))
					PA.add(id);
				else if ((!Accuracy.isRelevant(v1, this.ratingMedian)) && (!Accuracy.isRelevant(v2, this.ratingMedian)))
					NA.add(id);
				else
					D.add(id);
			}
			else {
				double v1 = vRating1.isRated(id) ? vRating1.get(id).value : Constants.UNUSED;
				double v2 = vRating2.isRated(id) ? vRating2.get(id).value : Constants.UNUSED;
				if (Util.isUsed(v1)) {
					if (Accuracy.isRelevant(v1, this.ratingMedian))
						PO.add(id);
					else
						NO.add(id);
				}
				else if (Util.isUsed(v2)) {
					if (Accuracy.isRelevant(v2, this.ratingMedian))
						PO.add(id);
					else
						NO.add(id);
				}
			}
		}
		
		double numerator = 0;
		for (int id : PA) {
			double[] PNE = improvedJaccardCalcSingularities(id);
			if (PNE == null) continue;
			numerator += PNE[0];
		}
		for (int id : NA) {
			double[] PNE = improvedJaccardCalcSingularities(id);
			if (PNE == null) continue;
			numerator += PNE[1];
		}
		for (int id : D) {
			double[] PNE = improvedJaccardCalcSingularities(id);
			if (PNE == null) continue;
			numerator += Math.sqrt(PNE[0]*PNE[1]);
		}

		double denominator = numerator;
		for (int id : PO) {
			double[] PNE = improvedJaccardCalcSingularities(id);
			if (PNE == null) continue;
			numerator += Math.sqrt(PNE[0]*PNE[2]);
		}
		for (int id : NO) {
			double[] PNE = improvedJaccardCalcSingularities(id);
			if (PNE == null) continue;
			numerator += Math.sqrt(PNE[1]*PNE[2]);
		}
		
		return numerator / denominator;
	}

	
	/**
	 * Calculating singularities for improved Jaccard measure given column identifier.
	 * The improved Jaccard measure was developed by Shunpan Liang, Lin Ma, Fuyong Yuan.
	 * @param columnId given column identifier.
	 * @return singularities for improved Jaccard measure.
	 * @author Shunpan Liang, Lin Ma, Fuyong Yuan
	 */
	protected double[] improvedJaccardCalcSingularities(int columnId) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				RatingVector columnVector = getColumnRating(columnId);
				if (columnVector == null || columnVector.size() == 0)
					return null;
				
				Set<Integer> columnIds = getColumnIds();
				double total = columnIds.size();
				if (total == 0) return null;
				int P = 0, N = 0, E = 0;
				for (int columnId : columnIds) {
					if (columnVector.isRated(columnId)) {
						double rating = columnVector.get(columnId).value;
						if (Accuracy.isRelevant(rating, ratingMedian))
							P++;
						else
							N++;
					}
					else
						E++;
				}
				
				return new double[] {1.0-(double)P/total, 1.0-(double)N/total, 1.0-(double)E/total};
			}
		};
		
		return (double[])cacheTask(columnId, this.valueCache, task);
	}

	
	/**
	 * Calculating the relevant Jaccard (RJ) measure between two pairs.
	 * Sujoy Bag, Sri Krishna Kumar, and Manoj Kumar Tiwari developed the relevant Jaccard (RJ) measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Relevant Jaccard (RJ) measure between both two rating vectors and profiles.
	 * @author Sujoy Bag, Sri Krishna Kumar, Manoj Kumar Tiwari
	 */
	protected double relevantJaccard(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> set1 = vRating1.fieldIds(true);
		Set<Integer> set2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(set1);
		common.retainAll(set2);
		
		double n = common.size();
		if (n == 0 && (set1.size() != 0 || set2.size() != 0)) return 0;
		double n1 = set1.size() - n;
		double n2 = set2.size() - n;
		
		return 1 / (1 + 1/n + n1/(1+n1) + 1/(1+n2));
	}
	
	
//	protected double relevantJaccard(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
//		Set<Integer> set1 = vRating1.fieldIds(true);
//		Set<Integer> set2 = vRating2.fieldIds(true);
//		Set<Integer> common = Util.newSet();
//		common.addAll(set1);
//		common.retainAll(set2);
//
//		double n = common.size();
//		double N = set1.size() + set2.size() - n;
//		double M = getColumnIds().size();
//		
//		double a = n/N;
//		double b = (M-N)/(M-n);
//		return (n/N + (M-N)/(M-n)) / 2;
//	}
	
	
	/**
	 * Computing common field IDs of two rating vectors as list.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return common field IDs of two rating vectors.
	 */
	private static List<Integer> commonFieldIdsAsList(RatingVector vRating1, RatingVector vRating2) {
		List<Integer> common = Util.newList();
		common.addAll(vRating1.fieldIds(true));
		common.retainAll(vRating2.fieldIds(true));
		return common;
	}

	
	/**
	 * Converting value bins into rank bins.
	 * @param valueBins value bins
	 * @return rank bins.
	 */
	protected static Map<Double, Integer> convertValueBinsToRankBins(List<Double> valueBins) {
		if (valueBins == null || valueBins.size() == 0)
			return Util.newMap();
		
		Collections.sort(valueBins);
		Map<Double, Integer> rankBins = Util.newMap();
		int n = valueBins.size();
		for (int i = 0; i < n; i++) {
			rankBins.put(valueBins.get(i), n-i);
		}
		
		return rankBins;
	}
	
	
	/**
	 * Extracting value bins from two specified rating vectors.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return Extracted value bins from two specified rating vectors.
	 */
	protected static List<Double> extractValueBins(RatingVector vRating1, RatingVector vRating2) {
		Set<Double> values = Util.newSet();
		
		Set<Integer> ids1 = vRating1.fieldIds(true);
		for (int id1 : ids1) {
			double value1 = vRating1.get(id1).value;
			values.add(value1);
		}
		
		Set<Integer> ids2 = vRating2.fieldIds(true);
		for (int id2 : ids2) {
			double value2 = vRating2.get(id2).value;
			values.add(value2);
		}
		
		List<Double> bins = DSUtil.toDoubleList(values);
		Collections.sort(bins);
		return bins;
	}
	
	
	/**
	 * Extracting rank bins from two specified rating vectors.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return Extracted rank bins from two specified rating vectors.
	 */
	protected static Map<Double, Integer> extractRankBins(RatingVector vRating1, RatingVector vRating2) {
		List<Double> valueBins = extractValueBins(vRating1, vRating2);
		return convertValueBinsToRankBins(valueBins);
	}
	
	
	/**
	 * Extracting value bins from configuration.
	 * @return extracted value bins from configuration.
	 */
	protected List<Double> extractConfigValueBins() {
		if (!getConfig().containsKey(VALUE_BINS_FIELD))
			return Util.newList();
		
		return TextParserUtil.parseListByClass(
				getConfig().getAsString(VALUE_BINS_FIELD),
				Double.class,
				",");
	}
	
	
	/**
	 * Extracting rank bins from configuration.
	 * @return extracted SRC rank bins from configuration.
	 */
	protected Map<Double, Integer> extractConfigRankBins() {
		List<Double> valueBins = extractConfigValueBins();
		return convertValueBinsToRankBins(valueBins);
	}

	
	@Override
	public Inspector getInspector() {
		return EvaluateGUI.createInspector(this);
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(VALUE_BINS_FIELD, VALUE_BINS_DEFAULT);
		config.put(BCF_MEDIAN_MODE_FIELD, BCF_MEDIAN_MODE_DEFAULT);
		config.put(MU_ALPHA_FIELD, MU_ALPHA_DEFAULT);
		config.put(SMTP_LAMBDA_FIELD, SMTP_LAMBDA_DEFAULT);
		config.put(SMTP_GENERAL_VAR_FIELD, SMTP_GENERAL_VAR_DEFAULT);
		config.put(TA_NORMALIZED_FIELD, TA_NORMALIZED_DEFAULT);
		
		return config;
	}


}
