/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.io.Serializable;
import java.util.EventListener;
import java.util.List;

import javax.swing.event.EventListenerList;

/**
 * This aims to solve the serialization problem when the system class {@link EventListenerList} does not specify the serial version UID.
 * However, this solution that specifies explicitly the serial version UID {@link #serialVersionUID} is work-around.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class HMMListenerList implements Serializable, Cloneable {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal list of listeners.
	 */
	protected List<EventListener> listeners = Util.newList(0);
	
	
    /**
	 * Default constructor.
	 */
	public HMMListenerList() {

	}

	
	/**
	 * Adding listener with specified class.
	 * @param <T> specified type of event listener.
	 * @param t specified class.
	 * @param l specified listener.
	 */
    public synchronized <T extends EventListener> void add(Class<T> t, T l) {
    	if (l == null || !t.isInstance(l)) return;
    	
    	listeners.add(l);
    }


    /**
	 * Removing listener with specified class.
	 * @param <T> specified type of event listener.
	 * @param t specified class.
	 * @param l specified listener.
     */
    public synchronized <T extends EventListener> void remove(Class<T> t, T l) {
    	if (l == null || !t.isInstance(l)) return;
    	
    	while (listeners.remove(l)) {};
    }
    
    
    /**
	 * Removing listener.
	 * @param l specified listener.
     */
    public synchronized void remove(EventListener l) {
    	if (l == null) return;
    	
    	while (listeners.remove(l)) {};
    }

    
    /**
     * Getting array of listeners with specified class.
	 * @param <T> specified type of event listener.
     * @param t specified class.
     * @return array of listeners with specified class.
     */
    @SuppressWarnings("unchecked")
	public synchronized <T extends EventListener> T[] getListeners(Class<T> t) {
    	List<T> list = Util.newList(0);
    	for (EventListener l : listeners) {
    		if (t.isInstance(l)) list.add((T)l);
    	}
    	
    	T[] array = Util.newArray(t, list.size());
    	for (int i = 0; i < list.size(); i++) array[i] = list.get(i);
    	
    	return array;
    }
    
    
    /**
     * Getting entire listeners. Using this method is careful.
     * @return entire listeners.
     */
    public synchronized List<EventListener> getListeners() {
    	return listeners;
    }
    
    
}
