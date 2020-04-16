/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.NeighborCFUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * This class sets up an advanced version of user-based neighbor collaborative filtering (Neighbor CF) algorithm with more similarity measures.
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
 * Author Ali Amer contributed Amer and Amer2 measures.<br>
 * Author Loc Nguyen contributed TA (triangle area) measure.<br>
 * Authors Ali Amer and Loc Nguyen contributed quasi-TfIdf measure. Quasi-TfIdf measure is an extension of Amer2 measure and the ideology of TF and IDF.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NeighborCFExtUserBased extends NeighborCFExt implements DuplicatableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCFExtUserBased() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		return NeighborCFUserBased.estimate(this, param, queryIds);
	}

	
	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, this.itemMeans);
	}

	
	@Override
	protected double pip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		// TODO Auto-generated method stub
		return pip(vRating1, vRating2, this.itemMeans);
	}


	@Override
	protected double pss(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		// TODO Auto-generated method stub
		return pss(vRating1, vRating2, this.itemMeans);
	}

	
	@Override
	protected double pc(RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, int fixedColumnId) {
		// TODO Auto-generated method stub
		return pc(vRating1, vRating2, fixedColumnId, this.itemMeans);
	}


	@Override
	protected RatingVector getColumnRating(int columnId) {
		// TODO Auto-generated method stub
		return this.dataset.getItemRating(columnId);
	}


	@Override
	protected Set<Integer> getColumnIds() {
		// TODO Auto-generated method stub
		return this.itemIds;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_userbased_ext";
	}


	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Extended user-based nearest neighbors collaborative filtering algorithm";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		NeighborCFExtUserBased cf = new NeighborCFExtUserBased();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}

	
}
