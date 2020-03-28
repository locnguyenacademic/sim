package net.hudup.data;

import java.util.Collection;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;

/**
 * This abstract class represents a set of document vectors. SMTP measure in this class is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public abstract class DocumentVectorSet {

	
	/**
	 * Internal data source.
	 */
	protected Object dataSource = null;
	
	
	/**
	 * Internal attribute list.
	 */
	protected AttributeList attList = null;

	
	/**
	 * List of variances.
	 */
	protected List<Double> vars = Util.newList();

	
	/**
	 * Lambda.
	 */
	protected double lambda = 0;


	/**
	 * Constructor with specified data source and lambda.
	 * @param dataSource specified data source.
	 * @param lambda specified lambda.
	 */
	protected DocumentVectorSet(Object dataSource, double lambda) {
		this.dataSource = dataSource;
		this.lambda = lambda;
		
		try {
			Fetcher<Profile> docs = this.fetchDocs();
			if (docs.next()) {
				Profile profile = docs.pick();
				if (profile != null)
					this.attList = profile.getAttRef();
			}
			docs.close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}

		int n = getAttributeList().size();
		List<Double> means = DSUtil.initDoubleList(n, 0);
		List<Double> counts = DSUtil.initDoubleList(n, 0);
		this.vars = DSUtil.initDoubleList(n, 0);
		try {
			Fetcher<Profile> docs = this.fetchDocs();
			while(docs.next()) {
				DocumentVector doc = (DocumentVector)docs.pick();
				if (doc == null) continue;
				for (int i = 0; i < n; i++) {
					double v = doc.getValueAsReal(i);
					if (Util.isUsed(v)) {
						counts.set(i, counts.get(i) + 1);
						means.set(i, means.get(i) + v);
					}
				}
			}
			docs.close();
			for (int i = 0; i < n; i++) {
				means.set(i, means.get(i) / counts.get(i));
			}
			
			docs = this.fetchDocs();
			while(docs.next()) {
				DocumentVector doc = (DocumentVector)docs.pick();
				if (doc == null) continue;
				for (int i = 0; i < n; i++) {
					double v = doc.getValueAsReal(i);
					if (Util.isUsed(v)) {
						double d = v - means.get(i);
						this.vars.set(i, d*d);
					}
				}
			}
			docs.close();
			for (int i = 0; i < n; i++) {
				double count = counts.get(i);
				if (count == 0)
					this.vars.set(i, Constants.UNUSED);
				else
					this.vars.set(i, this.vars.get(i) / count); //Use MLE variance
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}


	/**
	 * Getting attribute list.
	 * @return attribute list.
	 */
	public AttributeList getAttributeList() {
		return attList;
	}
	
	
	/**
	 * Fetching documents.
	 * @return fetched documents.
	 */
	public abstract Fetcher<Profile> fetchDocs();
	
	
	/**
	 * Getting variances.
	 * @return variances.
	 */
	public List<Double> getVars() {
		return vars;
	}
	
	
	/**
	 * Getting lambda.
	 * @return lambda.
	 */
	public double getLambda() {
		return lambda;
	}

	
	/**
	 * Setting variances.
	 * @param lambda lambda.
	 */
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
	
	/**
	 * Computing SMTP measure with other document set. SMTP measure is developed by Yung-Shen Lin, Jung-Yi Jiang, Shie-Jue Lee, and implemented by Loc Nguyen.
	 * @param other other document set.
	 * @author Yung-Shen Lin, Jung-Yi Jiang, and Shie-Jue Lee.
	 * @return SMTP measure with other document set.
	 */
	public double smtp(DocumentVectorSet other) {
		int n = getAttributeList().size();
		if (n == 0) return Constants.UNUSED;
		
		try {
			double a = 0, b = 0;
			for (int k = 0; k < n; k++) {
				Fetcher<Profile> docs1 = this.fetchDocs();
				double var = vars.get(k);
	
				while(docs1.next()) {
					Profile doc1 = docs1.pick();
					if (doc1 == null) continue;
					
					double d1 = doc1.getValueAsReal(k);
					Fetcher<Profile> docs2 = other.fetchDocs();
					while(docs2.next()) {
						Profile doc2 = docs2.pick();
						if (doc2 == null) continue;
						
						double d2 = doc2.getValueAsReal(k);
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
					docs2.close();
				}
				docs1.close();
			}
			
			return (a/b + lambda) / (1 + lambda);
		}
		catch (Exception e) {
			LogUtil.trace(e);
			return Constants.UNUSED;
		}
	}
	
	
	/**
	 * Create document set from specified profiles and lambda.
	 * @param profiles specified profiles.
	 * @param lambda specified lambda.
	 * @return document set from specified profiles and lambda.
	 */
	public static DocumentVectorSet create(Profiles profiles, double lambda) {
		
		return new DocumentVectorSet(profiles, lambda) {
			
			@Override
			public Fetcher<Profile> fetchDocs() {
				// TODO Auto-generated method stub
				return ((Profiles)this.dataSource).fetch();
			}
		};
	}
	
	
	/**
	 * Create document set from specified dataset and lambda.
	 * @param dataset specified dataset.
	 * @param lambda specified lambda.
	 * @return document set from specified dataset and lambda.
	 */
	public static DocumentVectorSet create(Dataset dataset, double lambda) {
		
		return new DocumentVectorSet(dataset, lambda) {
			
			@Override
			public Fetcher<Profile> fetchDocs() {
				// TODO Auto-generated method stub
				return ((Dataset)this.dataSource).fetchSample();
			}
		};
	}

	
	/**
	 * Create document set from specified collection of vectors and lambda.
	 * @param vCollection specified collection of vectors.
	 * @param lambda specified lambda.
	 * @return document set from specified collection of vectors and lambda.
	 */
	public static DocumentVectorSet create(final Collection<DocumentVector> vCollection, double lambda) {
		
		return new DocumentVectorSet(vCollection, lambda) {
			
			@Override
			public Fetcher<Profile> fetchDocs() {
				// TODO Auto-generated method stub
				List<Profile> vList = Util.newList(vCollection.size());
				for (DocumentVector v : vCollection) {
					vList.add(v);
				}
				return new MemFetcher<Profile>(vList);
			}
		};
	}
	
	
	/**
	 * Create document set from specified collection of vectors and lambda.
	 * @param vCollection specified collection of vectors.
	 * @param lambda specified lambda.
	 * @return document set from specified collection of vectors and lambda.
	 */
	public static DocumentVectorSet create2(final Collection<Profile> vCollection, double lambda) {
		
		return new DocumentVectorSet(vCollection, lambda) {
			
			@Override
			public Fetcher<Profile> fetchDocs() {
				// TODO Auto-generated method stub
				return new MemFetcher<Profile>(vCollection);
			}
		};
	}


}
