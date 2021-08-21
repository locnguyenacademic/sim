package net.jsi.ui;

import java.io.Serializable;
import java.util.EventListener;

public interface MarketListener extends EventListener, Serializable {

	
	void notify(MarketEvent evt);

	
}
