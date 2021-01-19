/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;
import net.pso.logistic.speqmath.Parser;

/**
 * This class represents the function is specified by mathematical expression.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExprFunction extends FunctionReal {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Special character for indexing variables.
	 */
	public final static String VAR_INDEX_SPECIAL_CHAR = "#";

	
	/**
	 * Mathematical expression.
	 */
	protected String expr = "";
	
	
	/**
	 * Default constructor.
	 * @param varNames variable names.
	 * @param expr mathematical expression.
	 */
	public ExprFunction(List<String> varNames, String expr) {
		super(varNames.size());
		
		this.expr = expr != null ? expr : "";
		int dim = this.vars.size();
		for (int i = 0; i < dim; i++) {
			this.vars.get(i).setName(varNames.get(i));
		}
	}

	
	@Override
	public Double eval(Vector<Double> arg) {
		int n = arg.getAttCount();
		String expr = this.expr;
		for (int i = 0; i < n; i++) {
			String attName =  arg.getAtt(i).getName();
			String replacedText = expr.contains(VAR_INDEX_SPECIAL_CHAR) ? VAR_INDEX_SPECIAL_CHAR + attName : attName;   
			if(!expr.contains(replacedText))
				continue;
			
			if(arg.isMissing(i))
				return null; //Cannot evaluate
			Double value = arg.getValueAsReal(attName);
			if(!Util.isUsed(value))
				return null; //Cannot evaluate
			
			expr = expr.replaceAll(replacedText, value.toString());
		}
		
		try {
			Parser parser = new Parser();
			double value = parser.parse2(expr);
			if (Util.isUsed(value))
				return value;
			else
				return null;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}

	
	@Override
	public int getVarNum() {
		return vars.size();
	}


	@Override
	public String toString() {
		StringBuffer text = new StringBuffer("Function \"" + expr);
		if (optimizer != null)
			text.append("\" gets optimal at " + optimizer.toString());
		
		return text.toString();
	}

	
}
