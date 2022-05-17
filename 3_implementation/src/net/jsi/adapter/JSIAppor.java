/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi.adapter;

import java.io.File;

import net.hudup.core.App;
import net.hudup.core.ApporAbstract;
import net.hudup.core.client.PowerServer;
import net.jsi.StockProperty;
import net.jsi.UniverseImpl;
import net.jsi.UniverseRemoteImpl;

/**
 * This class represents a JSI application creator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JSIAppor extends ApporAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * JSI application creator name.
	 */
	public final static String JSI = "JSI";
	
	
	/**
	 * Default JSI application.
	 */
	public JSIAppor() {

	}

	
	@Override
	public String getName() {
		return JSI;
	}

	
	@Override
	public App create(PowerServer server) {
		if (app != null) return app;
		
		if (server == null) return null;
		try {
//			UniverseRemoteImpl jsiUniverseRemote = new UniverseRemoteForAppor(new UniverseImpl());
			
			UniverseRemoteImpl jsiUniverseRemote = new UniverseRemoteImpl(new UniverseImpl()) {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected boolean isAdminAccount() {
					return true;
				}
			};
			jsiUniverseRemote.open(new File(StockProperty.WORKING_DIRECTORY));
			jsiUniverseRemote.export(server.getPort());
			
			return (app = new JSIApp(server, this, jsiUniverseRemote));
		} catch (Throwable e) {net.jsi.Util.trace(e);}
		
		return null;
	}

	
}


//class UniverseRemoteImplEx extends UniverseRemoteImpl {
//
//	public UniverseRemoteImplEx(Universe universe) {
//		super(universe);
//		// TODO Auto-generated constructor stub
//	}
//	
//	@Override
//	protected boolean isAdminAccount() {
//		return true;
//	}
//
//}
