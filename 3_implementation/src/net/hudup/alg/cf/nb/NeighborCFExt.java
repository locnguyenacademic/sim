/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

import java.awt.Component;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

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
import net.hudup.data.DocumentVector;
import net.hudup.evaluate.ui.EvaluateGUI;

/**
 * This class sets up an extended version of nearest neighbors collaborative filtering algorithm with more similarity measures.
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
 * Yi Mu, Nianhao Xiao, Ruichun Tang, Liang Luo, and Xiaohan Yin contributed Mu measure.<br>
 * <br>
 * Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee contributed SMTP measure.<br>
 * <br>
 * Ali Amer contributed measures SMD, HSMD, and NNSM.<br>
 * <br>
 * Loc Nguyen contributed TA (triangle area) measure.<br>
 * <br>
 * Ali Amer and Loc Nguyen contributed quasi-TfIdf measure. Quasi-TfIdf measure is an extension of HSMD measure and the ideology of TF and IDF.<br>
 * <br>
 * Sujoy Bag, Sri Krishna Kumar, and Manoj Kumar Tiwari contributed relevant Jaccard (RJ) measure.<br>
 * <br>
 * Mubbashir Ayub1, Mustansar Ali Ghazanfar1, Tasawer Khan1, Asjad Saleem contributed rating Jaccard measure.
 * <br>
 * Soojung Lee contributed indexed Jaccard measure.<br>
 * <br>
 * Ali Amer contributed ESim measure.<br>
 * <br>
 * Manochandar and Punniyamoorthy contributed MPIP measure.<br>
 * <br>
 * Zhenhua Tan and Liangliang He contributed RES measure.<br>
 * <br>
 * Jesús Bobadilla, Fernando Ortega, and Antonio Hernando contributed singularity measure (SM).<br>
 * <br>
 * Jiangzhou Deng, Yong Wang, Junpeng Guo, Yongheng Deng, Jerry Gao, and Younghee Park contributed Kullback–Leibler divergence measure (KL).<br>
 * <br>
 * Yitong Meng, Xinyan Dai, Xiao Yan, James Cheng, Weiwen Liu, Jun Guo, Benben Liao, and Guangyong Chen contributed Preference Mover Distance (PMD) measure.<br>
 * <br>
 * Ali Amer contributed STB measure.<br>
 * <br>
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
	 * Extended Jaccard type.
	 */
	protected static final String JACCARD_EXT_TYPE = "jaccard_ext_type";

	
	/**
	 * Dual Jaccard.
	 */
	public static final String JACCARD_EXT_TYPE_DUAL = "dual";

	
	/**
	 * EDS Jaccard.
	 */
	public static final String JACCARD_EXT_TYPE_EDS = "eds";

	
	/**
	 * PSS type.
	 */
	protected static final String PSS_TYPE = "pss_type";

	
	/**
	 * Normal PSS.
	 */
	protected static final String PSS_TYPE_NORMAL = "pss";

	
	/**
	 * NHSM is an advanced version of PSS measure.
	 */
	protected static final String PSS_TYPE_NHSM = "nhsm";

	
	/**
	 * Value bins count.
	 */
	protected static final String VALUE_BINS_COUNT_FIELD = "value_bins_count";

	
	/**
	 * Default value bins count.
	 */
	protected static final int VALUE_BINS_COUNT_DEFAULT = 5;

	
	/**
	 * BCF type.
	 */
	protected static final String BCF_TYPE = "bcf_type";

	
	/**
	 * Normal BCF.
	 */
	protected static final String BCF_TYPE_NORMAL = "bcf";

	
	/**
	 * Jaccard BCF (BCFJ).
	 */
	protected static final String BCF_TYPE_JACCARD = "bcfj";

	
	/**
	 * BCF median mode.
	 */
	protected static final String BCF_MEDIAN_MODE_FIELD = "bcf_median";

	
	/**
	 * Default BCF median mode.
	 */
	protected static final boolean BCF_MEDIAN_MODE_DEFAULT = true;

	
	/**
	 * PIP type.
	 */
	protected static final String PIP_TYPE = "pip_type";

	
	/**
	 * Normal PIP.
	 */
	protected static final String PIP_TYPE_NORMAL = "pip";

	
	/**
	 * MPIP.
	 */
	protected static final String PIP_TYPE_MPIP = "mpip";

	
	/**
	 * MMD type.
	 */
	protected static final String MMD_TYPE = "mmd_type";

	
	/**
	 * Normal MMD.
	 */
	protected static final String MMD_TYPE_NORMAL = "mmd";

	
	/**
	 * CjacMD measure which is developed by Suryakant and Tripti Mahara.
	 */
	protected static final String MMD_TYPE_CJACMD = "cjacmd";
	
	
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
	 * Triangle area type.
	 */
	protected static final String TA_TYPE = "ta_type";

	
	/**
	 * Normal triangle area.
	 */
	protected static final String TA_TYPE_NORMAL = "ta";

	
	/**
	 * Triangle area + Jaccard measure..
	 */
	protected static final String TA_TYPE_JACCARD = "taj";
	
	
	/**
	 * TA normalized mode.
	 */
	protected static final String TA_NORMALIZED_FIELD = "ta_normalized";

	
	/**
	 * Default TA normalized mode.
	 */
	protected static final boolean TA_NORMALIZED_DEFAULT = false;

	
	/**
	 * HSMD type.
	 */
	protected static final String HSMD_TYPE = "hsmd_type";

	
	/**
	 * Normal HSMD.
	 */
	protected static final String HSMD_TYPE_NORMAL = "hsmd";

	
	/**
	 * HSMD + Jaccard measure.
	 */
	protected static final String HSMD_TYPE_JACCARD = "hsmdj";

	
	/**
	 * Quasi-TfIdf type.
	 */
	protected static final String QUASI_TFIDF_TYPE = "qti_type";

	
	/**
	 * Normal Quasi-TfIdf.
	 */
	protected static final String QUASI_TFIDF_TYPE_NORMAL = "qti";

	
	/**
	 * Quasi-TfIdf + Jaccard measure.
	 */
	protected static final String QUASI_TFIDF_TYPE_JACCARD = "qtij";

	
	/**
	 * Type of ESim measure.
	 */
	protected static final String ESIM_TYPE = "esim_type";

	
	/**
	 * ESim type: ESIM.
	 */
	protected static final String ESIM_TYPE_ESIM = "esim";

	
	/**
	 * ESim type: ESIM2.
	 */
	protected static final String ESIM_TYPE_ESIM2 = "esim2";

	
	/**
	 * ESim type: ESIM3.
	 */
	protected static final String ESIM_TYPE_ESIM3 = "esim3";

	
	/**
	 * ESim type: ZSIM.
	 */
	protected static final String ESIM_TYPE_ZSIM = "zsim";

	
	/**
	 * KL type.
	 */
	protected static final String KL_TYPE = "kl_type";

	
	/**
	 * Normal KL.
	 */
	protected static final String KL_TYPE_NORMAL = "kl";

	
	/**
	 * KL + Jaccard measure.
	 */
	protected static final String KL_TYPE_ADVANCED = "kl_advanced";

	
	/**
	 * Value bins.
	 */
	protected List<Double> valueBins = Util.newList();
	
	
	/**
	 * Rank bins.
	 */
	protected Map<Integer, Double> rankBins = Util.newMap();
	
	
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
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		
		this.rankBins.clear();
		this.valueBins.clear();
	}


	@Override
	public List<String> getAllMeasures() {
		Set<String> mSet = Util.newSet();
		mSet.addAll(getMainMeasures());
		
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
		mSet.add(Measure.JACCARD_EXT);
		mSet.add(Measure.PSS);
		mSet.add(Measure.BCF);
		mSet.add(Measure.SRC);
		mSet.add(Measure.PIP);
		mSet.add(Measure.PC);
		mSet.add(Measure.MMD);
		mSet.add(Measure.FENG);
		mSet.add(Measure.MU);
		mSet.add(Measure.SMTP);
		mSet.add(Measure.SMD);
		mSet.add(Measure.HSMD);
		mSet.add(Measure.QUASI_TFIDF);
		mSet.add(Measure.TA);
		mSet.add(Measure.COCO);
		mSet.add(Measure.NNSM);
		mSet.add(Measure.AMER);
		mSet.add(Measure.ESIM);
		mSet.add(Measure.RES);
		mSet.add(Measure.SM);
		mSet.add(Measure.KL);
		mSet.add(Measure.PMD);
		mSet.add(Measure.STB);
		
		measures.clear();
		measures.addAll(mSet);
		Collections.sort(measures);
		return measures;
	}


	/**
	 * Checking whether the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_COUNT_FIELD}).
	 * @return true if the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_COUNT_FIELD}). Otherwise, return false.
	 */
	public boolean requireDiscreteRatingBins() {
		return requireDiscreteRatingBins(getMeasure());
	}
	
	
	/**
	 * Given specified measure, checking whether the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_COUNT_FIELD}).
	 * @param measure specified measure.
	 * @return true if the similarity measure requires to declare discrete bins in configuration ({@link #VALUE_BINS_COUNT_FIELD}). Otherwise, return false.
	 */
	protected boolean requireDiscreteRatingBins(String measure) {
		if (measure == null)
			return false;
		else if (measure.equals(Measure.BCF) ||  measure.equals(Measure.MMD))
			return true;
		else
			return false;
	}

	
	@Override
	public boolean isSymmetric() {
		String measure = getMeasure();
		if (measure == null)
			return true;
		else if (measure.equals(Measure.TA))
			return false;
		else
			return true;
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
		if (measure.equals(Measure.JACCARD_EXT))
			return jaccardExt(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.PSS))
			return pss(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.BCF))
			return bcf(vRating1, vRating2, profile1, profile2);
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
		else if (measure.equals(Measure.HSMD))
			return hsmd(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.QUASI_TFIDF))
			return quasiTfIdf(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.TA))
			return triangleArea(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.COCO))
			return coco(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.NNSM))
			return nnsm(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.ESIM))
			return esim(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.RES))
			return res(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.SM))
			return sm(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.KL))
			return kl(vRating1, vRating2, profile1, profile2);
		else if (measure.equals(Measure.STB))
			return stb(vRating1, vRating2, profile1, profile2);
		else
			return super.sim0(measure, vRating1, vRating2, profile1, profile2, params);
	}

	
	@Override
	protected void updateConfig(String measure) {
		if (measure == null) return;
		
		config.addReadOnly(VALUE_BINS_COUNT_FIELD);
		config.addReadOnly(COSINE_NORMALIZED_FIELD);
		config.addReadOnly(COSINE_WEIGHTED_FIELD);
		config.addReadOnly(COSINE_RA_FIELD);
		config.addReadOnly(PEARSON_WEIGHTED_FIELD);
		config.addReadOnly(PEARSON_RA_FIELD);
		config.addReadOnly(MSD_FRACTION_FIELD);
		config.addReadOnly(ENTROPY_SUPPORT_FIELD);
		config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
		config.addReadOnly(MU_ALPHA_FIELD);
		config.addReadOnly(SMTP_LAMBDA_FIELD);
		config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
		config.addReadOnly(TA_NORMALIZED_FIELD);
		config.addReadOnly(RATINGJ_THRESHOLD_FIELD);
		config.addReadOnly(INDEXEDJ_INTERVALS_FIELD);
		config.addReadOnly(ESIM_TYPE);
		config.addReadOnly(JACCARD_TYPE);
		config.addReadOnly(JACCARD_EXT_TYPE);
		config.addReadOnly(COSINE_TYPE);
		config.addReadOnly(PEARSON_TYPE);
		config.addReadOnly(MSD_TYPE);
		config.addReadOnly(TRIANGLE_TYPE);
		config.addReadOnly(PSS_TYPE);
		config.addReadOnly(BCF_TYPE);
		config.addReadOnly(PIP_TYPE);
		config.addReadOnly(MMD_TYPE);
		config.addReadOnly(TA_TYPE);
		config.addReadOnly(HSMD_TYPE);
		config.addReadOnly(QUASI_TFIDF_TYPE);
		config.addReadOnly(IPWR_ALPHA_FIELD);
		config.addReadOnly(IPWR_BETA_FIELD);
		config.addReadOnly(KL_TYPE);
		
		if (measure.equals(Measure.JACCARD_EXT)) {
			config.removeReadOnly(JACCARD_EXT_TYPE);
		}
		else if (measure.equals(Measure.PSS)) {
			config.removeReadOnly(PSS_TYPE);
		}
		else if (measure.equals(Measure.BCF)) {
			config.removeReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.removeReadOnly(BCF_TYPE);
		}
		else if (measure.equals(Measure.SRC)) {
			config.removeReadOnly(VALUE_BINS_COUNT_FIELD);
		}
		else if (measure.equals(Measure.PIP)) {
			config.removeReadOnly(PIP_TYPE);
		}
		else if (measure.equals(Measure.PC)) {
		}
		else if (measure.equals(Measure.MMD)) {
			config.removeReadOnly(VALUE_BINS_COUNT_FIELD);
			config.removeReadOnly(MMD_TYPE);
		}
		else if (measure.equals(Measure.FENG)) {
		}
		else if (measure.equals(Measure.MU)) {
			config.removeReadOnly(MU_ALPHA_FIELD);
		}
		else if (measure.equals(Measure.SMTP)) {
			config.removeReadOnly(SMTP_LAMBDA_FIELD);
			config.removeReadOnly(SMTP_GENERAL_VAR_FIELD);
		}
		else if (measure.equals(Measure.AMER)) {
		}
		else if (measure.equals(Measure.SMD)) {
		}
		else if (measure.equals(Measure.HSMD)) {
			config.removeReadOnly(HSMD_TYPE);
		}
		else if (measure.equals(Measure.QUASI_TFIDF)) {
			config.removeReadOnly(QUASI_TFIDF_TYPE);
		}
		else if (measure.equals(Measure.TA)) {
			config.removeReadOnly(TA_NORMALIZED_FIELD);
			config.removeReadOnly(TA_TYPE);
		}
		else if (measure.equals(Measure.COCO)) {
		}
		else if (measure.equals(Measure.NNSM)) {
		}
		else if (measure.equals(Measure.ESIM)) {
			config.removeReadOnly(ESIM_TYPE);
		}
		else if (measure.equals(Measure.RES)) {
		}
		else if (measure.equals(Measure.SM)) {
		}
		else if (measure.equals(Measure.KL)) {
			config.removeReadOnly(KL_TYPE);
		}
		else if (measure.equals(Measure.PMD)) {
		}
		else {
			super.updateConfig(measure);
		}
	}


	/**
	 * Calculating the extended Jaccard measure between two pairs.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return extended Jaccard measure between both two rating vectors and profiles.
	 */
	protected double jaccardExt(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String jexttype = config.getAsString(JACCARD_EXT_TYPE);
		if (jexttype.equals(JACCARD_EXT_TYPE_DUAL))
			return jaccardExtDual(vRating1, vRating2, profile1, profile2);
		else if (jexttype.equals(JACCARD_EXT_TYPE_EDS))
			return jaccardExtEDS(vRating1, vRating2, profile1, profile2);
		else
			return jaccardNormal(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the dual Jaccard measure between two pairs.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return dual Jaccard measure between both two rating vectors and profiles.
	 */
	protected double jaccardExtDual(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> set1 = vRating1.fieldIds(true);
		Set<Integer> set2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet(); common.addAll(set1); common.retainAll(set2);
		Set<Integer> union = Util.newSet(); union.addAll(set1); union.addAll(set2);
		
		double n = common.size(), N = union.size();
		if (N == 0) return Constants.UNUSED;
		double concernCoeff = 0.5 * n / N;
		
		set1.clear();
		set2.clear();
		for (int fieldId : union) {
			if (vRating1.isRated(fieldId)) {
				double v1 = vRating1.get(fieldId).value;
				if (Accuracy.isRelevant(v1, this.ratingMedian)) set1.add(fieldId);
			}
			if (vRating2.isRated(fieldId)) {
				double v2 = vRating2.get(fieldId).value;
				if (Accuracy.isRelevant(v2, this.ratingMedian)) set2.add(fieldId);
			}
		}
		common.clear(); common.addAll(set1); common.retainAll(set2);
		
		double a = common.size();
		double b = set1.size() + set2.size() - a;
		if (a == 0 || b == 0)  return concernCoeff;
		return concernCoeff * (a/b + (N-b)/(N-a));
	}

	
	/**
	 * Calculating the EDS Jaccard measure between two pairs.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer
	 * @return EDS Jaccard measure between both two rating vectors and profiles.
	 */
	protected double jaccardExtEDS(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> union = getColumnIds();
		
		int n = 0;
		for (int fieldId : union) {
			boolean rated1 = vRating1.isRated(fieldId);
			boolean rated2 = vRating2.isRated(fieldId);
			if (rated1 == rated2) n += 2;
		}
		
		return (double)n / (2.0*union.size());
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
	protected double pss(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String ttype = config.getAsString(PSS_TYPE);
		if (ttype.equals(PSS_TYPE_NORMAL))
			return pssNormal(vRating1, vRating2, profile1, profile2);
		else if (ttype.equals(PSS_TYPE_NHSM))
			return nhsm(vRating1, vRating2, profile1, profile2);
		else
			return pssNormal(vRating1, vRating2, profile1, profile2);
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
	protected abstract double pssNormal(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2);


	/**
	 * Calculating the PSS measure between two rating vectors. PSS measure is developed by Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu, and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param fieldMeans map of field means.
	 * @author Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu.
	 * @return PSS measure between two rating vectors.
	 */
	protected double pssNormal(RatingVector vRating1, RatingVector vRating2, Map<Integer, Double> fieldMeans) {
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
		double jaccard = jaccardMulti(vRating1, vRating2, profile1, profile2);
		return pssNormal(vRating1, vRating2, profile1, profile2) * jaccard * urp;
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
	protected double bcf(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String btype = config.getAsString(BCF_TYPE);
		if (btype.equals(BCF_TYPE_NORMAL))
			return bcfNormal(vRating1, vRating2, profile1, profile2);
		else if (btype.equals(BCF_TYPE_JACCARD))
			return bcfj(vRating1, vRating2, profile1, profile2);
		else
			return bcfNormal(vRating1, vRating2, profile1, profile2);
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
	protected double bcfNormal(RatingVector vRating1, RatingVector vRating2,
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
		return bcfNormal(vRating1, vRating2, profile1, profile2) + jaccardNormal(vRating1, vRating2, profile1, profile2);
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
		Map<Integer, Double> bins = rankBins;
		if (bins.isEmpty()) bins = extractRankBins(vRating1, vRating2);

		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double sum = 0;
		int n = 0;
		for (int id : common) {
			int r1 = findClosestRankOfValue(vRating1.get(id).value, bins);
			int r2 = findClosestRankOfValue(vRating2.get(id).value, bins);
			if (r1 < 0 || r2 < 0) continue;
			
			int d = r1 - r2;
			sum += d*d;
			n++;
		}
		
		if (n == 0) return Constants.UNUSED;
		return 1.0 - 6*sum/(n*(n*n-1));
	}
	
	
	/**
	 * Calculating the general PIP measure between two pairs. 
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return PIP measure between both two rating vectors and profiles.
	 */
	protected double pip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String ttype = config.getAsString(PIP_TYPE);
		if (ttype.equals(PIP_TYPE_NORMAL))
			return pipNormal(vRating1, vRating2, profile1, profile2);
		else if (ttype.equals(PIP_TYPE_MPIP))
			return mpip(vRating1, vRating2, profile1, profile2);
		else
			return pipNormal(vRating1, vRating2, profile1, profile2);
	}

	
	/**
	 * Calculating the PIP measure between two pairs. PIP measure is developed by Hyung Jun Ahn, and implemented by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Hyung Jun Ahn.
	 * @return PIP measure between both two rating vectors and profiles.
	 */
	protected abstract double pipNormal(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2);
	
	
	/**
	 * Calculating the PIP measure between two rating vectors. PIP measure is developed by Hyung Jun Ahn and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param fieldMeans map of field means.
	 * @author Hyung Jun Ahn
	 * @return PIP measure between two rating vectors.
	 */
	protected double pipNormal(RatingVector vRating1, RatingVector vRating2, Map<Integer, Double> fieldMeans) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double pip = 0.0;
		double range = getMaxRating()-getMinRating();
		for (int id : common) {
			double r1 = vRating1.get(id).value;
			double r2 = vRating2.get(id).value;
			boolean agreed = agree(r1, r2);
			
			double d = agreed ? Math.abs(r1-r2) : 2*Math.abs(r1-r2);
			double pro = (2*range + 1) - d;
			pro = pro*pro;
			
			double impact = (Math.abs(r1-this.ratingMedian)+1) * (Math.abs(r2-this.ratingMedian)+1);
			if (!agreed) impact = 1 / impact;
			
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
	 * Calculating the MPIP measure between two pairs.
	 * Manochandar and Punniyamoorthy developed the MPIP. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return MPIP measure between both two rating vectors and profiles.
	 * @author Manochandar, Punniyamoorthy
	 */
	protected abstract double mpip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2);

	
	/**
	 * Calculating the MPIP measure between two pairs.
	 * Manochandar and Punniyamoorthy developed the MPIP. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param fieldMeans means of fields.
	 * @return MPIP measure between both two rating vectors and profiles.
	 * @author Manochandar, Punniyamoorthy
	 */
	protected double mpip(RatingVector vRating1, RatingVector vRating2, Map<Integer, Double> fieldMeans) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double mpip = 0.0;
		double range = getMaxRating() - getMinRating();
		for (int id : common) {
			double r1 = vRating1.get(id).value;
			double r2 = vRating2.get(id).value;
			boolean agreed = agree(r1, r2);
			
			double pro = 0;
			if (agreed) {
				double d = (Math.abs(r1-r2) - ratingMedian) / range;
				pro = d*d;
			}
			else {
				double bias = Math.abs(r1 - r2);
				double d = 1 / (bias*range);
				if (bias > ratingMedian)
					pro = 0.75*d*d;
				else if (bias == ratingMedian)
					pro = 0.5*d*d;
				else
					pro = 0.25*d*d;
			}
			
			double impact = (Math.abs(r1-this.ratingMedian)+1) * (Math.abs(r2-this.ratingMedian)+1);
			impact = agreed ? Math.exp(-1/impact) : 1/impact;
			
			double mean = fieldMeans.get(id);
			double pop = 0.3010;
			if ((r1 > mean && r2 > mean) || (r1 < mean && r2 < mean)) {
				double bias = (r1+r2)/2 - mean;
				pop = Math.log10(2 + bias*bias);
			}
			
			mpip += pro * impact * pop;
		}
		
		return mpip;
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
	protected double mmd(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String mtype = config.getAsString(MMD_TYPE);
		if (mtype.equals(MMD_TYPE_NORMAL))
			return mmdNormal(vRating1, vRating2, profile1, profile2);
		else if (mtype.equals(MMD_TYPE_CJACMD))
			return cosineNormal(vRating1, vRating2, profile1, profile2) + mmdNormal(vRating1, vRating2, profile1, profile2) + jaccardNormal(vRating1, vRating2, profile1, profile2);
		else
			return mmdNormal(vRating1, vRating2, profile1, profile2);
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
	protected double mmdNormal(RatingVector vRating1, RatingVector vRating2,
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
		double pearson = pearson(vRating1, vRating2, profile1, profile2);
		double hg = 1 - bc(vRating1, vRating2, profile1, profile2);
//		double hg = bc(vRating1, vRating2, profile1, profile2);
		double jaccard = jaccardNormal(vRating1, vRating2, profile1, profile2);
		
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
	 * Calculating the general HSMD measure between two pairs. HSMD measure is developed by Ali Amer, and implemented by Loc Nguyen.
	 * HSMD measure is only applied into positive ratings.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer.
	 * @return General HSMD measure between both two rating vectors and profiles.
	 */
	protected double hsmd(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		String stype = config.getAsString(HSMD_TYPE);
		if (stype.equals(HSMD_TYPE_NORMAL))
			return hsmdNormal(vRating1, vRating2, profile1, profile2);
		else if (stype.equals(HSMD_TYPE_JACCARD))
			return hsmdj(vRating1, vRating2, profile1, profile2);
		else
			return hsmdNormal(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the HSMD measure between two pairs. HSMD measure is developed by Ali Amer, and implemented by Loc Nguyen.
	 * HSMD measure is only applied into positive ratings.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer.
	 * @return HSMD measure between both two rating vectors and profiles.
	 */
	protected double hsmdNormal(RatingVector vRating1, RatingVector vRating2,
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
	 * Calculating the HSMD + Jaccard measure between two pairs. HSMD measure is developed by Ali Amer, and implemented by Loc Nguyen.
	 * HSMD + Jaccard measure is only applied into positive ratings.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer.
	 * @return HSMD + Jaccard measure between both two rating vectors and profiles.
	 */
	protected double hsmdj(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return hsmdNormal(vRating1, vRating2, profile1, profile2) * jaccardNormal(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the quasi-TfIdf measure between two pairs. Quasi-TfIdf measure is developed by Ali Amer and Loc Nguyen.
	 * Quasi-TfIdf measure is an extension of Quasi-TfIdf measure and the ideology of TF and IDF.
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
	protected double quasiTfIdf(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		String stype = config.getAsString(QUASI_TFIDF_TYPE);
		if (stype.equals(QUASI_TFIDF_TYPE_NORMAL))
			return quasiTfIdfNormal(vRating1, vRating2, profile1, profile2);
		else if (stype.equals(QUASI_TFIDF_TYPE_JACCARD))
			return quasiTfIdfJaccard(vRating1, vRating2, profile1, profile2);
		else
			return quasiTfIdfNormal(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the quasi-TfIdf measure between two pairs. Quasi-TfIdf measure is developed by Ali Amer and Loc Nguyen.
	 * Quasi-TfIdf measure is an extension of Quasi-TfIdf measure and the ideology of TF and IDF.
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
	protected double quasiTfIdfNormal(RatingVector vRating1, RatingVector vRating2,
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
	 * Quasi-TfIdf + Jaccard measure is an extension of Quasi-TfIdf measure and the ideology of TF and IDF.
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
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Loc Nguyen.
	 * @return TA measure between both two rating vectors and profiles.
	 */
	protected double triangleArea(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String ttype = config.getAsString(TA_TYPE);
		if (ttype.equals(TA_TYPE_NORMAL))
			return triangleAreaNormal(vRating1, vRating2, profile1, profile2);
		else if (ttype.equals(TA_TYPE_JACCARD))
			return triangleAreaJaccard(vRating1, vRating2, profile1, profile2);
		else
			return triangleAreaNormal(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the TA (triangle area) measure between two pairs. TA is developed by Loc Nguyen.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * The current version does not support positive cosine. The next version will fix it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Loc Nguyen.
	 * @return TA measure between both two rating vectors and profiles.
	 */
	protected double triangleAreaNormal(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
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
		return triangleAreaNormal(vRating1, vRating2, profile1, profile2) * jaccardNormal(vRating1, vRating2, profile1, profile2);
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
	protected double nnsm(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		Set<Integer> fieldIds = commonFieldIds(vRating1, vRating2);
		int n = fieldIds.size();
		if (n == 0) return Constants.UNUSED;
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
	 * Calculating the ESim measure between two pairs.
	 * Ali Amer developed the ESim measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return ESim measure between both two rating vectors and profiles.
	 * @author Ali Amer
	 */
	protected double esim(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String type = config.getAsString(ESIM_TYPE);
		double product = 0;
		double length1 = 0;
		double length2 = 0;
		Set<Integer> union = unionFieldIds(vRating1, vRating2);
		if (type.equals(ESIM_TYPE_ESIM)) { //ESim
			for (int id : union) {
				boolean rated1 = vRating1.isRated(id);
				boolean rated2 = vRating2.isRated(id);
				if (rated1 && rated2)
					product += vRating1.get(id).value * vRating2.get(id).value;
				else if (rated1)
					length1 += vRating1.get(id).value;
				else
					length2 += vRating2.get(id).value;
			}
			
			return product / (length1+length2);
		}
		else if (type.equals(ESIM_TYPE_ZSIM)) { //ZSM
			int n = 0;
			for (int id : union) {
				boolean rated1 = vRating1.isRated(id);
				boolean rated2 = vRating2.isRated(id);
				if (rated1 && rated2) {
					product += vRating1.get(id).value * vRating2.get(id).value;
					n++;
				}
				else if (rated1)
					length1 += vRating1.get(id).value;
				else
					length2 += vRating2.get(id).value;
			}

			return n*product / (length1*length2);
		}
		else if (type.equals(ESIM_TYPE_ESIM2)) { //ESim2
			int n = 0;
			for (int id : union) {
				boolean rated1 = vRating1.isRated(id);
				boolean rated2 = vRating2.isRated(id);
				if (rated1 && rated2) {
					product += vRating1.get(id).value * vRating2.get(id).value;
					n++;
				}
				else if (rated1) {
					double value = vRating1.get(id).value;
					length1 += value;
					product += value;
					n++;
				}
				else {
					double value = vRating2.get(id).value;
					length2 += value;
					product += value;
					n++;
				}
			}

			return n*product / (length1*length2);
		}
		else if (type.equals(ESIM_TYPE_ESIM3)) { //ESim3
			int n = 0;
			for (int id : union) {
				boolean rated1 = vRating1.isRated(id);
				boolean rated2 = vRating2.isRated(id);
				if (rated1 && rated2) {
					product += vRating1.get(id).value * vRating2.get(id).value;
					n++;
				}
				else if (rated1) {
					length1 += vRating1.get(id).value;
					n++;
				}
				else {
					length2 += vRating2.get(id).value;
					n++;
				}
			}

			return n*product / (length1*length2);
		}
		else
			return Constants.UNUSED;
	}
	
	
	/**
	 * Calculating the RES measure between two pairs.
	 * Zhenhua Tan and Liangliang He developed the RES measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return RES measure between both two rating vectors and profiles.
	 * @author Zhenhua Tan, Liangliang He
	 */
	protected double res(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return Constants.UNUSED;
		
		double range = Math.PI / (getMaxRating()-getMinRating());
		double mean = getRatingMean();
		double mean1 = vRating1.mean() - mean;
		double mean2 = vRating2.mean() - mean;
		double res = 0;
		for (int id : common) {
			double v1 = vRating1.get(id).value;
			double v2 = vRating2.get(id).value;
			double phibase1 = v1 - ratingMedian;
			double phibase2 = v2 - ratingMedian;
			
			double phi1 = Constants.UNUSED;
			if (phibase1 * mean1 >= 0)
				phi1 = range * (1/(1+Math.abs(mean1))) * phibase1;
			else
				phi1 = range * (1+Math.abs(mean1)/ratingMedian) * phibase1;
			
			double phi2 = Constants.UNUSED;
			if (phibase2 * mean2 >= 0)
				phi2 = range * (1/(1+Math.abs(mean2))) * phibase2;
			else
				phi2 = range * (1+Math.abs(mean2)/ratingMedian) * phibase2;
			
			double C = Math.sqrt(0.5 + 0.5*Math.cos(phi1-phi2));
			
			double rj = getColumnRating(id).mean();
			double D = Constants.UNUSED;
			if ((v1-rj)*(v2-rj) >= 0) {
				double d1 = Math.exp(-Math.abs(v1-v2));
				double d2 = Math.exp(0.5 * (Math.abs(v1-rj)+Math.abs(v2-rj)));
				D = d1*d2;
			}
			else {
				double d1 = Math.exp(-Math.abs(v1-v2));
				D = d1;
			}
			
			res += C*D;
		}
		
		return Math.atan(res) / (0.5*Math.PI);
	}
	
	
	/**
	 * Calculating the singularity measure (SM) between two pairs.
	 * Jesús Bobadilla, Fernando Ortega, and Antonio Hernando developed the SM. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return singularity measure (SM) between both two rating vectors and profiles.
	 * @author Jesús Bobadilla, Fernando Ortega, Antonio Hernando
	 */
	protected double sm(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> PA = Util.newSet(), NA = Util.newSet(), D = Util.newSet();
		Set<Integer> ids = commonFieldIds(vRating1, vRating2);
		if (ids.size() == 0) return Constants.UNUSED;
		
		for (int id : ids) {
			double v1 = vRating1.get(id).value;
			double v2 = vRating2.get(id).value;
			if (Accuracy.isRelevant(v1, this.ratingMedian) && Accuracy.isRelevant(v2, this.ratingMedian))
				PA.add(id);
			else if ((!Accuracy.isRelevant(v1, this.ratingMedian)) && (!Accuracy.isRelevant(v2, this.ratingMedian)))
				NA.add(id);
			else
				D.add(id);
		}
		
		double range = getMaxRating() - getMinRating();
		double paSum = 0;
		for (int id : PA) {
			double[] PNE = improvedJaccardCalcSingularities(id);
			if (PNE == null) continue;
			
			double d = (vRating1.get(id).value - vRating2.get(id).value) / range;
			d = 1.0 - d*d;
			paSum += d*PNE[0]*PNE[0];
		}
		paSum = PA.size() > 0 ? paSum/(double)PA.size() : paSum;
		
		double naSum = 0;
		for (int id : NA) {
			double[] PNE = improvedJaccardCalcSingularities(id);
			if (PNE == null) continue;
			
			double d = (vRating1.get(id).value - vRating2.get(id).value) / range;
			d = 1.0 - d*d;
			naSum += d*PNE[1]*PNE[1];
		}
		naSum = NA.size() > 0 ? naSum/(double)NA.size() : naSum;
		
		double dSum = 0;
		for (int id : D) {
			double[] PNE = improvedJaccardCalcSingularities(id);
			if (PNE == null) continue;
			
			double d = (vRating1.get(id).value - vRating2.get(id).value) / range;
			d = 1.0 - d*d;
			dSum += d*PNE[0]*PNE[1];
		}
		dSum = D.size() > 0 ? dSum/(double)D.size() : dSum;
		
		return (paSum + naSum + dSum) / 3.0;
	}
	
	
	/**
	 * Calculating the Kullback–Leibler divergence measure (KL) between two pairs.
	 * Jiangzhou Deng, Yong Wang, Junpeng Guo, Yongheng Deng, Jerry Gao, and Younghee Park developed the SM. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Kullback–Leibler divergence measure (KL) between both two rating vectors and profiles.
	 * @author Jiangzhou Deng, Yong Wang, Junpeng Guo, Yongheng Deng, Jerry Gao, Younghee Park
	 */
	protected double kl(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String ttype = config.getAsString(KL_TYPE);
		if (ttype.equals(KL_TYPE_NORMAL))
			return klNormal(vRating1, vRating2, profile1, profile2);
		else if (ttype.equals(KL_TYPE_ADVANCED))
			return klAdvanced(vRating1, vRating2, profile1, profile2);
		else
			return klNormal(vRating1, vRating2, profile1, profile2);
	}
	
	
	/**
	 * Calculating the normal Kullback–Leibler divergence measure (KL) between two pairs.
	 * Jiangzhou Deng, Yong Wang, Junpeng Guo, Yongheng Deng, Jerry Gao, and Younghee Park developed the SM. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Normal Kullback–Leibler divergence measure (KL) between both two rating vectors and profiles.
	 * @author Jiangzhou Deng, Yong Wang, Junpeng Guo, Yongheng Deng, Jerry Gao, Younghee Park
	 */
	protected double klNormal(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return 1 / (1 + calcKL(vRating1.id(), vRating2.id(), true, null));
	}

	
	/**
	 * Calculating the advanced Kullback–Leibler divergence measure (KL) between two pairs.
	 * Jiangzhou Deng, Yong Wang, Junpeng Guo, Yongheng Deng, Jerry Gao, and Younghee Park developed the SM. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Advanced Kullback–Leibler divergence measure (KL) between both two rating vectors and profiles.
	 * @author Jiangzhou Deng, Yong Wang, Junpeng Guo, Yongheng Deng, Jerry Gao, Younghee Park
	 */
	protected double klAdvanced(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> ids1 = vRating1.fieldIds(true);
		Set<Integer> ids2 = vRating2.fieldIds(true);
		
		double mean1 = vRating1.mean();
		double mean2 = vRating1.mean();
		double sim = 0;
		for (int id1 : ids1) {
			double value1 = vRating1.get(id1).value;
			for (int id2 : ids2) {
				double value2 = vRating2.get(id2).value;
				double s = 1 / (1 + Math.exp(-(value1-mean1)*(value2-mean2)));
				double kl1 = calcKLSingle(id1, id2, false, valueCache, isCached());
				double kl2 = calcKLSingle(id2, id1, false, valueCache, isCached());
				double kl = 1 / (1+ (kl1+kl2)/2);
				
				sim += s*kl;
			}
		}
		
		
		return sim;
	}

	
	/**
	 * Calculating the Kullback–Leibler divergence (KL) of rating vectors given main identifier.
	 * @param mainId main identifier.
	 * @param auxId auxiliary identifier.
	 * @param isRow flag to indicate whether the specified identifier is of row rating vector.
	 * @param cachedMap cached map.
	 * @param cached flag to indicate whether to cache the entropy.
	 * @return the Kullback–Leibler divergence (KL) of rating vectors given main identifiers.
	 */
	protected double calcKLSingle(int mainId, int auxId, boolean isRow,  Map<Integer, Object> cachedMap, boolean cached) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				double minRating = getMinRating();
				double maxRating = getMaxRating();
				double value = minRating;
				double count1 = count(mainId, isRow);
				double count2 = count(auxId, isRow);
				double lambda1 = count1 / (count1+count2);
				double kl = 0;
				while (value <= maxRating) {
					double prob1 = prob(mainId, value, isRow);
					double prob2 = prob(auxId, value, isRow);

					double a = lambda1*prob1;
					double b = (1-lambda1)*prob2;
					kl += a * Math.log(a/b+Float.MIN_VALUE);

					value = value + 1;
				}
				
				return kl;
			}
		};
		
		if (cached && cachedMap != null)
			return (double)cacheTask(mainId, cachedMap, task);
		else
			return (Double)task.perform();
	}

	
	/**
	 * Calculating the Kullback–Leibler divergence (KL) of rating vectors given two identifiers.
	 * @param id1 given identifier 1.
	 * @param id2 given identifier 2.
	 * @param isRow flag to indicate whether the specified identifier is of row rating vector.
	 * @param cachedMap cached map.
	 * @return the Kullback–Leibler divergence (KL) of rating vectors given two identifiers.
	 */
	protected double calcKL(int id1, int id2, boolean isRow,  Map<Integer, Map<Integer, Object>> cachedMap) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				double minRating = getMinRating();
				double maxRating = getMaxRating();
				double value = minRating;
				double count1 = count(id1, isRow);
				double count2 = count(id2, isRow);
				double lambda1 = count1 / (count1+count2);
				double lambda2 = count2 / (count1+count2);
				double kl1 = 0;
				double kl2 = 0;
				while (value <= maxRating) {
					double prob1 = prob(id1, value, isRow);
					double prob2 = prob(id2, value, isRow);

					double a = lambda1*prob1;
					double b = (1-lambda1)*prob2;
					kl1 += a * Math.log(a/b+Float.MIN_VALUE);
					
					double c = lambda2*prob2;
					double d = (1-lambda2)*prob1;
					kl1 += c * Math.log(c/d+Float.MIN_VALUE);

					value = value + 1;
				}
				
				return (kl1 + kl2) / 2.0;
			}
		};
		
		if (cachedMap != null)
			return (double)cacheTask(id1, id2, cachedMap, task);
		else
			return (Double)task.perform();
	}

	
	/**
	 * Calculating the Preference Mover Distance (PMD) measure between two pairs.
	 * Yitong Meng, Xinyan Dai, Xiao Yan, James Cheng, Weiwen Liu, Jun Guo, Benben Liao, and Guangyong Chen developed the Preference Mover Distance (PMD) measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return Preference Mover Distance (PMD) measure between both two rating vectors and profiles.
	 * @author Yitong Meng, Xinyan Dai, Xiao Yan, James Cheng, Weiwen Liu, Jun Guo, Benben Liao, Guangyong Chen
	 */
	@Deprecated
	protected double pmd(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return Constants.UNUSED;
	}

	
	/**
	 * Calculating the STB measure between two pairs. STB measure is developed by Ali Amer, and implemented by Loc Nguyen.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @author Ali Amer
	 * @return STB measure between both two rating vectors and profiles.
	 */
	protected double stb(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		Set<Integer> fieldIds = unionFieldIds(vRating1, vRating2);
		if (fieldIds.size() == 0) return Constants.UNUSED;
		
		double X1 = 0, X2 = 0, Y1 = 0, Y2 = 0, Z1 = 0, Z2 = 0;
		for (int fieldId : fieldIds) {
			boolean rated1 = vRating1.isRated(fieldId);
			boolean rated2 = vRating2.isRated(fieldId);
			
			if (rated1 && rated2) {
				double value1 = vRating1.get(fieldId).value;
				double value2 = vRating2.get(fieldId).value;
				X1 += value1; X2 += value2;
				Z1 += value1; Z2 += value2;
			}
			else if (rated1) {
				double value1 = vRating1.get(fieldId).value;
				X1 += value1;
				Y1 += value1;
				Z1 += value1;
			}
			else if (rated2) {
				double value2 = vRating2.get(fieldId).value;
				X2 += value2;
				Y2 += value2;
				Z2 += value2;
			}
		}
		
		return (X1+X2) * (Z1+Z2-Y1-Y2) / (Z1+Z2);
	}

	
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
	 * Finding closest rank of given value.
	 * @param value given value.
	 * @param rankBins rank bins.
	 * @return closest rank of given value.
	 */
	protected static int findClosestRankOfValue(double value, Map<Integer, Double> rankBins) {
		Set<Integer> ranks = rankBins.keySet();
		int closestRank = -1;
		double minBias = Double.MAX_VALUE;
		for (int rank : ranks) {
			double rankValue = rankBins.get(rank);
			double bias = Math.abs(value - rankValue);
			if (bias < minBias) {
				closestRank = rank;
				minBias = bias;
			}
		}
		
		return closestRank;
	}
	
	
	/**
	 * Converting value bins into rank bins.
	 * @param valueBins value bins
	 * @return rank bins.
	 */
	protected static Map<Integer, Double> convertValueBinsToRankBins(List<Double> valueBins) {
		if (valueBins == null || valueBins.size() == 0) return Util.newMap();
		
		Collections.sort(valueBins);
		Map<Integer, Double> rankBins = Util.newMap();
		int n = valueBins.size();
		for (int i = 0; i < n; i++) {
			rankBins.put(n-i, valueBins.get(i));
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
	protected static Map<Integer, Double> extractRankBins(RatingVector vRating1, RatingVector vRating2) {
		List<Double> valueBins = extractValueBins(vRating1, vRating2);
		return convertValueBinsToRankBins(valueBins);
	}
	
	
	/**
	 * Extracting value bins from configuration.
	 * @return extracted value bins from configuration.
	 */
	protected List<Double> extractConfigValueBins() {
		if (!getConfig().containsKey(VALUE_BINS_COUNT_FIELD)) return Util.newList();
		int binsCount = getConfig().getAsInt(VALUE_BINS_COUNT_FIELD);
		if (binsCount < 2) return Util.newList();
		double min = getMinRating(), max = getMaxRating();
		double interval = (max - min) / (double)(binsCount-1);
		if (interval <= 0) return Util.newList();
		
		List<Double> binsList = Util.newList();
		double v = min;
		while (v <= max) {
			binsList.add(v);
			v += interval;
		}
		
		return binsList;
	}
	
	
	/**
	 * Extracting rank bins from configuration.
	 * @return extracted SRC rank bins from configuration.
	 */
	protected Map<Integer, Double> extractConfigRankBins() {
		List<Double> valueBins = extractConfigValueBins();
		return convertValueBinsToRankBins(valueBins);
	}

	
	@Override
	public Inspector getInspector() {
		return EvaluateGUI.createInspector(this);
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(VALUE_BINS_COUNT_FIELD, VALUE_BINS_COUNT_DEFAULT);
		tempConfig.put(BCF_MEDIAN_MODE_FIELD, BCF_MEDIAN_MODE_DEFAULT);
		tempConfig.put(MU_ALPHA_FIELD, MU_ALPHA_DEFAULT);
		tempConfig.put(SMTP_LAMBDA_FIELD, SMTP_LAMBDA_DEFAULT);
		tempConfig.put(SMTP_GENERAL_VAR_FIELD, SMTP_GENERAL_VAR_DEFAULT);
		tempConfig.put(TA_NORMALIZED_FIELD, TA_NORMALIZED_DEFAULT);
		tempConfig.put(JACCARD_EXT_TYPE, JACCARD_EXT_TYPE_DUAL);
		tempConfig.put(ESIM_TYPE, ESIM_TYPE_ESIM);
		tempConfig.put(PSS_TYPE, PSS_TYPE_NORMAL);
		tempConfig.put(BCF_TYPE, BCF_TYPE_NORMAL);
		tempConfig.put(PIP_TYPE, PIP_TYPE_NORMAL);
		tempConfig.put(MMD_TYPE, MMD_TYPE_NORMAL);
		tempConfig.put(TA_TYPE, TA_TYPE_NORMAL);
		tempConfig.put(HSMD_TYPE, HSMD_TYPE_NORMAL);
		tempConfig.put(QUASI_TFIDF_TYPE, QUASI_TFIDF_TYPE_NORMAL);
		tempConfig.put(KL_TYPE, KL_TYPE_NORMAL);

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				if (key.equals(JACCARD_EXT_TYPE)) {
					String jexttype = getAsString(JACCARD_EXT_TYPE);
					jexttype = jexttype == null ? JACCARD_EXT_TYPE_DUAL : jexttype;
					List<String> jexttypes = Util.newList();
					jexttypes.add(JACCARD_EXT_TYPE_DUAL);
					Collections.sort(jexttypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one extended Jaccard type", 
						"Choosing Jaccard type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						jexttypes.toArray(new String[] {}), 
						jexttype);

				}
				else if (key.equals(ESIM_TYPE)) {
					String type = getAsString(ESIM_TYPE);
					type = type == null ? getDefaultMeasure() : type;
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one ESim type", 
						"Choosing ESim type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						new String[] {ESIM_TYPE_ESIM, ESIM_TYPE_ESIM2, ESIM_TYPE_ESIM3, ESIM_TYPE_ZSIM}, 
						type);
				}
				else if (key.equals(PSS_TYPE)) {
					String ttype = getAsString(PSS_TYPE);
					ttype = ttype == null ? PSS_TYPE_NORMAL : ttype;
					List<String> ttypes = Util.newList();
					ttypes.add(PSS_TYPE_NORMAL);
					ttypes.add(PSS_TYPE_NHSM);
					Collections.sort(ttypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one PSS type", 
						"Choosing PSS type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						ttypes.toArray(new String[] {}), 
						ttype);
				}
				else if (key.equals(BCF_TYPE)) {
					String btype = getAsString(BCF_TYPE);
					btype = btype == null ? BCF_TYPE_NORMAL : btype;
					List<String> btypes = Util.newList();
					btypes.add(BCF_TYPE_NORMAL);
					btypes.add(BCF_TYPE_JACCARD);
					Collections.sort(btypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one BCF type", 
						"Choosing BCF type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						btypes.toArray(new String[] {}), 
						btype);
				}
				else if (key.equals(PIP_TYPE)) {
					String ptype = getAsString(PIP_TYPE);
					ptype = ptype == null ? PIP_TYPE_NORMAL : ptype;
					List<String> ptypes = Util.newList();
					ptypes.add(PIP_TYPE_NORMAL);
					ptypes.add(PIP_TYPE_MPIP);
					Collections.sort(ptypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one PIP type", 
						"Choosing PIP type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						ptypes.toArray(new String[] {}), 
						ptype);
				}
				else if (key.equals(MMD_TYPE)) {
					String mtype = getAsString(MMD_TYPE);
					mtype = mtype == null ? MMD_TYPE_NORMAL : mtype;
					List<String> mtypes = Util.newList();
					mtypes.add(MMD_TYPE_NORMAL);
					mtypes.add(MMD_TYPE_CJACMD);
					Collections.sort(mtypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one MMD type", 
						"Choosing MMD type", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						mtypes.toArray(new String[] {}), 
						mtype);
				}
				else if (key.equals(TA_TYPE)) {
					String ttype = getAsString(TA_TYPE);
					ttype = ttype == null ? TA_TYPE_NORMAL : ttype;
					List<String> ttypes = Util.newList();
					ttypes.add(TA_TYPE_NORMAL);
					ttypes.add(TA_TYPE_JACCARD);
					Collections.sort(ttypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp,
						"Please choose one TA type",
						"Choosing TA type",
						JOptionPane.INFORMATION_MESSAGE,
						null,
						ttypes.toArray(new String[] {}),
						ttype);
				}
				else if (key.equals(HSMD_TYPE)) {
					String stype = getAsString(HSMD_TYPE);
					stype = stype == null ? HSMD_TYPE_NORMAL : stype;
					List<String> stypes = Util.newList();
					stypes.add(HSMD_TYPE_NORMAL);
					stypes.add(HSMD_TYPE_JACCARD);
					Collections.sort(stypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp,
						"Please choose one HSMD type",
						"Choosing HSMD type",
						JOptionPane.INFORMATION_MESSAGE,
						null,
						stypes.toArray(new String[] {}),
						stype);
				}
				else if (key.equals(QUASI_TFIDF_TYPE)) {
					String qtype = getAsString(QUASI_TFIDF_TYPE);
					qtype = qtype == null ? QUASI_TFIDF_TYPE_NORMAL : qtype;
					List<String> qtypes = Util.newList();
					qtypes.add(QUASI_TFIDF_TYPE_NORMAL);
					qtypes.add(QUASI_TFIDF_TYPE_JACCARD);
					Collections.sort(qtypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp,
						"Please choose one Quasi-TfIdf type",
						"Choosing Quasi-TfIdf type",
						JOptionPane.INFORMATION_MESSAGE,
						null,
						qtypes.toArray(new String[] {}),
						qtype);
				}
				else if (key.equals(KL_TYPE)) {
					String ktype = getAsString(KL_TYPE);
					ktype = ktype == null ? KL_TYPE_NORMAL : ktype;
					List<String> ktypes = Util.newList();
					ktypes.add(KL_TYPE_NORMAL);
					ktypes.add(KL_TYPE_ADVANCED);
					Collections.sort(ktypes);
					
					return (Serializable) JOptionPane.showInputDialog(
						comp,
						"Please choose one KL type",
						"Choosing KL type",
						JOptionPane.INFORMATION_MESSAGE,
						null,
						ktypes.toArray(new String[] {}),
						ktype);
				}
				else
					return tempConfig.userEdit(comp, key, defaultValue);
			}
			
		};

		config.putAll(tempConfig);
		
		return config;
	}


}
