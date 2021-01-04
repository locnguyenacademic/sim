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
public class Measure extends net.hudup.core.alg.cf.Measure {
	
	
	/**
	 * Name of PSS measure.
	 */
	public static final String PSS = "pss";

	
	/**
	 * Name of NHSM measure.
	 */
	public static final String NHSM = "nhsm";

	
	/**
	 * Name of BCF measure.
	 */
	public static final String BCF = "bcf";

	
	/**
	 * Name of BCFJ measure (BCF + Jaccard).
	 */
	public static final String BCFJ = "bcfj";

	
	/**
	 * Name of SRC measure.
	 */
	public static final String SRC = "src";

	
	/**
	 * Name of PIP measure.
	 */
	public static final String PIP = "pip";

	
	/**
	 * Name of PC measure.
	 */
	public static final String PC = "pc";

	
	/**
	 * Name of MMD measure.
	 */
	public static final String MMD = "mmd";

	
	/**
	 * Name of CjacMD measure which is developed by Suryakant and Tripti Mahara.
	 */
	public static final String CJACMD = "cjacmd";

	
	/**
	 * Name of Feng measure.
	 */
	public static final String FENG = "feng";

	
	/**
	 * Name of Mu measure.
	 */
	public static final String MU = "mu";

	
	/**
	 * Name of SMTP measure.
	 */
	public static final String SMTP = "smtp";

	
	/**
	 * Name of SMD measure.
	 */
	public static final String SMD = "smd";

	
	/**
	 * Name of SMD2 measure.
	 */
	public static final String SMD2 = "smd2";

	
	/**
	 * Name of SMD2 + Jaccard measure.
	 */
	public static final String SMD2J = "smd2j";

	
	/**
	 * Name of Quasi-TfIdf measure.
	 */
	public static final String QUASI_TFIDF = "qti";

	
	/**
	 * Name of Quasi-TfIdf + Jaccard measure.
	 */
	public static final String QUASI_TFIDF_JACCARD = "qtij";

	
	/**
	 * Name of triangle area measure.
	 */
	public static final String TA = "ta";

	
	/**
	 * Name of triangle area + Jaccard measure.
	 */
	public static final String TAJ = "taj";

	
	/**
	 * Name of Coco measure.
	 */
	public static final String COCO = "coco";

	
	/**
	 * Name of numerical nearby similarity measure (NNSM).
	 */
	public static final String NNSM = "nnsm";

	
	/**
	 * Name of improved Jaccard (IJ) measure.
	 */
	public static final String IJ = "ij";

	
}
