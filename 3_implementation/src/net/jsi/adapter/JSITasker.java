/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi.adapter;

import java.io.File;

import net.hudup.core.Task;
import net.hudup.core.Tasker;
import net.hudup.core.client.PowerServer;
import net.jsi.StockProperty;
import net.jsi.UniverseImpl;
import net.jsi.UniverseRemoteImpl;

/**
 * This class represents a JSI tasker.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JSITasker implements Tasker {

	
	/**
	 * Tasker name.
	 */
	public final static String JSI = "JSI";
	
	
	/**
	 * Internal JSI task.
	 */
	protected JSITask jsiTask = null;
	
	
	/**
	 * Default JSI tasker.
	 */
	public JSITasker() {

	}

	
	@Override
	public String getName() {
		return JSI;
	}

	
	@Override
	public Task create(PowerServer server) {
		if (jsiTask != null) return jsiTask;
		
		if (server == null) return null;
		try {
			UniverseRemoteImpl jsiUniverseRemote = new UniverseRemoteImpl(new UniverseImpl()) {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected boolean isAdminAccount() {
					return true;
				}
			};
			jsiUniverseRemote.open(new File(StockProperty.WORKING_DIRECTORY));
			jsiUniverseRemote.export(server.getPort());
			
			return (jsiTask = new JSITask(server, this, jsiUniverseRemote));
		} catch (Throwable e) {net.jsi.Util.trace(e);}
		
		return null;
	}

	
	/**
	 * Discarding JSI task.
	 * @param jsiTask specified JSI task.
	 * @return true if discarding is successful.
	 */
	protected boolean discard(JSITask jsiTask) {
		if (jsiTask == null)
			return false;
		else {
			boolean discarded = jsiTask.discard0();
			this.jsiTask = jsiTask == this.jsiTask ? null : this.jsiTask;
			return discarded;
		}
	}
	
	
}
