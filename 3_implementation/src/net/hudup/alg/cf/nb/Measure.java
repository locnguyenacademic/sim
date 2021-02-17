/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

/**
 * This class represents measures which may be supported by the nearest neighbors collaborative filtering algorithm built in {@link NeighborCFExt}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Measure extends net.hudup.core.alg.cf.nb.Measure {
	
	
	/**
	 * PSS measure.
	 */
	public static final String PSS = "pss";

	
	/**
	 * NHSM measure.
	 */
	public static final String NHSM = "nhsm";

	
	/**
	 * BCF measure.
	 */
	public static final String BCF = "bcf";

	
	/**
	 * BCFJ measure (BCF + Jaccard).
	 */
	public static final String BCFJ = "bcfj";

	
	/**
	 * SRC measure.
	 */
	public static final String SRC = "src";

	
	/**
	 * PIP measure.
	 */
	public static final String PIP = "pip";

	
	/**
	 * PC measure.
	 */
	public static final String PC = "pc";

	
	/**
	 * MMD measure.
	 */
	public static final String MMD = "mmd";

	
	/**
	 * CjacMD measure which is developed by Suryakant and Tripti Mahara.
	 */
	public static final String CJACMD = "cjacmd";

	
	/**
	 * Feng measure.
	 */
	public static final String FENG = "feng";

	
	/**
	 * Mu measure.
	 */
	public static final String MU = "mu";

	
	/**
	 * SMTP measure.
	 */
	public static final String SMTP = "smtp";

	
	/**
	 * Amer measure.
	 */
	public static final String AMER = "amer";
	
	
	/**
	 * SMD measure.
	 */
	public static final String SMD = "smd";

	
	/**
	 * SMD2 measure.
	 */
	public static final String SMD2 = "smd2";

	
	/**
	 * SMD2 + Jaccard measure.
	 */
	public static final String SMD2J = "smd2j";

	
	/**
	 * Quasi-TfIdf measure.
	 */
	public static final String QUASI_TFIDF = "qti";

	
	/**
	 * Quasi-TfIdf + Jaccard measure.
	 */
	public static final String QUASI_TFIDF_JACCARD = "qtij";

	
	/**
	 * Triangle area measure.
	 */
	public static final String TA = "ta";

	
	/**
	 * Triangle area + Jaccard measure.
	 */
	public static final String TAJ = "taj";

	
	/**
	 * Coco measure.
	 */
	public static final String COCO = "coco";

	
	/**
	 * Numerical nearby similarity measure (NNSM).
	 */
	public static final String NNSM = "nnsm";

	
	/**
	 * Improved Jaccard (IJ) measure.
	 */
	public static final String IJ = "ij";

	
	/**
	 * Relevant Jaccard (IJ) measure.
	 */
	public static final String RJ = "rj";

	
}
