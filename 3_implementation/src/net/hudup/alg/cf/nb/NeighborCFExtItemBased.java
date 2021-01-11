/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.nb.NeighborCFItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * This class sets up an advanced version of item-based nearest neighbors collaborative filtering (Neighbor CF) algorithm with more similarity measures.
 * <br>
 * There are many authors who contributed measure to this class.<br>
 * Authors Haifeng Liu, Zheng Hu, Ahmad Mian, Hui Tian, Xuzhen Zhu contributed PSS measures and NHSM measure.<br>
 * Authors Bidyut Kr. Patra, Raimo Launonen, Ville Ollikainen, Sukumar Nandi contributed BC and BCF measures.<br>
 * Author Hyung Jun Ahn contributed PIP measure.<br>
 * Authors Keunho Choi and Yongmoo Suh contributed PC measure.<br>
 * Authors Suryakant and Tripti Mahara contributed MMD measure and CjacMD measure.<br>
 * Authors Junmei Feng, Xiaoyi Fengs, Ning Zhang, and Jinye Peng contributed Feng model.<br>
 * Authors Yi Mua, Nianhao Xiao, Ruichun Tang, Liang Luo, and Xiaohan Yin contributed Mu measure.<br>
 * Authors Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee contributed SMTP measure.<br>
 * Author Ali Amer contributed measures SMD, SMD2, and NNSM.<br>
 * Author Loc Nguyen contributed TA (triangle area) measure.<br>
 * Authors Ali Amer and Loc Nguyen contributed quasi-TfIdf measure. Quasi-TfIdf measure is an extension of SMD2 measure and the ideology of TF and IDF.<br>
 * Authors Shunpan Liang, Lin Ma, and Fuyong Yuan contributed improved Jaccard (IJ) measure.<br>
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NeighborCFExtItemBased extends NeighborCFExt implements DuplicatableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCFExtItemBased() {

	}


	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		return NeighborCFItemBased.estimate(this, param, queryIds);
	}

	
	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, this.userMeans);
	}

	
	@Override
	protected double pip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return pip(vRating1, vRating2, this.userMeans);
	}


	@Override
	protected double pss(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return pss(vRating1, vRating2, this.userMeans);
	}

	
	@Override
	protected double pc(RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, int fixedColumnId) {
		return pc(vRating1, vRating2, fixedColumnId, this.userMeans);
	}


	@Override
	protected Set<Integer> getRowIds() {
		return itemIds;
	}

	
	@Override
	protected RatingVector getRowRating(int rowId) {
		return dataset.getItemRating(rowId);
	}

	
	@Override
	protected double calcRowMean(RatingVector vRating) {
		return calcMean(this, itemMeans, vRating);
	}


	@Override
	protected Set<Integer> getColumnIds() {
		return userIds;
	}

	
	@Override
	protected RatingVector getColumnRating(int columnId) {
		return dataset.getUserRating(columnId);
	}


	@Override
	protected double calcColumnMean(RatingVector vRating) {
		return calcMean(this, userMeans, vRating);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_itembased_ext";
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Extended item-based nearest neighbors collaborative filtering algorithm";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}

	
}
