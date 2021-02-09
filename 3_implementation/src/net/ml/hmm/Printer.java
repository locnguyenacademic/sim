/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.rmi.RemoteException;

/**
 * This class is printer to print information.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public class Printer implements HMMListener, AutoCloseable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal printer.
	 */
	protected PrintWriter printer = null;

	
	/**
	 * Flag to indicate system printer.
	 */
	protected boolean system = false;
	
	
	/**
	 * Flag to indicate paused mode.
	 */
	protected boolean paused = false;

	
	/**
	 * Default printer.
	 */
	public Printer() {

	}

	
	/**
	 * Constructor with specified writer.
	 * @param out specified writer.
	 */
	public Printer(Writer out) {
		open(out);
	}
	
	
	/**
	 * Constructor with specified output stream.
	 * @param out specified output stream.
	 */
	public Printer(OutputStream out) {
		open(out);
	}

	
	/**
	 * Constructor with specified file.
	 * @param file specified file.
	 */
	public Printer(File file) {
		open(file);
	}

	
	/**
	 * Constructor with specified file path.
	 * @param filePath specified file path.
	 */
	public Printer(String filePath) {
		open(filePath);
	}
	
	
	/**
	 * Print message.
	 * @param message specified message.
	 */
	public void print(Object message) {
		if (isEnable()) {
			printer.print(message);
			printer.flush();
		}
	}
	
	
	/**
	 * Print message with new line.
	 * @param message specified message.
	 */
	public void println(Object message) {
		if (isEnable()) {
			printer.println(message);
			printer.flush();
		}
	}
	
	
	/**
	 * Open printer with specified writer.
	 * @param out specified writer.
	 */
	public void open(Writer out) {
		close();
		printer = new PrintWriter(out, true);
		paused = false;
		system = false;
	}
	
	
	/**
	 * Open printer with specified output stream.
	 * @param out specified output stream.
	 */
	public void open(OutputStream out) {
		close();
		printer = new PrintWriter(out, true);
		paused = false;
		system = (out == System.out);
	}

	
	/**
	 * Open printer with specified file.
	 * @param file specified file.
	 */
	public void open(File file) {
		try {
			close();
			printer = new PrintWriter(file);
			paused = false;
			system = false;
		}
		catch (Exception e) {
			close();
			Util.trace(e);
		}
	}

	
	/**
	 * Open printer with specified file path.
	 * @param filePath specified file path.
	 */
	public void open(String filePath) {
		try {
			close();
			printer = new PrintWriter(filePath);
			paused = false;
			system = false;
		}
		catch (Exception e) {
			close();
			Util.trace(e);
		}
	}
	

	/**
	 * Checking if this printer is enabled.
	 * @return true if this printer is enabled.
	 */
	public boolean isEnable() {
		return (printer != null) && (!paused);
	}
	
	
	/**
	 * Pause the printer.
	 */
	public void pause() {
		paused = true;
	}
	
	
	/**
	 * Resume the printer.
	 */
	public void resume() {
		paused = false;
	}

	
	@Override
	public void receivedInfo(HMMInfoEvent evt) throws RemoteException {
		println(evt.getInfo());
	}


	@Override
	public void receivedDo(HMMDoEvent evt) throws RemoteException {
		
	}


	@Override
	public void close() {
		if (printer == null) return;
		
		try {
			printer.flush();
			if (!system) printer.close();
			printer = null;
			system = false;
			paused = false;
		}
		catch (Throwable e) {
			printer = null;
			Util.trace(e);
		}
	}


	@Override
	protected void finalize() throws Throwable {
		try {
			close();
		} catch (Throwable e) {}
//		super.finalize();
	}
	
	
}
