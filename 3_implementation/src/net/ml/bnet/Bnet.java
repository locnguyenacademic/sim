/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.bnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * This interface represents a most abstract Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Bnet {

	
	/**
	 * Adding root nodes.
	 * @param rootNodes root nodes.
	 */
	void addRootNodes(Bnode...rootNodes);
	
	
	/**
	 * Getting root nodes.
	 * @return root nodes.
	 */
	List<Bnode> getRootNodes();
	
	
	/**
	 * Loading Bayesian network from storage.
	 * @param in specified input stream.
	 * @throws IOException if any IO error raises.
	 */
	void load(InputStream in) throws IOException;
	
	
	/**
	 * Saving Bayesian network to storage.
	 * @param out specified output stream.
	 * @throws IOException if any IO error raises.
	 */
	void save(OutputStream out) throws IOException;
	
	
	/**
	 * Create a node in Bayesian network.
	 * @param nodeName specified node name.
	 * @return a node in Bayesian network.
	 */
	Bnode newNode(String nodeName);
	
	
}
