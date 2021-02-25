/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.util.List;

import net.ea.pso.logistic.speqmath.Parser;

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
		
		this.expr = expr != null ? expr.trim() : "";
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
			if(!expr.contains(replacedText)) continue;
			
			if(arg.isMissing(i)) return null;
			Double value = arg.getValueAsReal(attName);
			if(Double.isNaN(value)) return null;
			
			expr = expr.replaceAll(replacedText, value.toString());
		}
		
		try {
			Parser parser = new Parser();
			double value = parser.parse2(expr);
			if (Double.isNaN(value))
				return null;
			else
				return value;
		}
		catch (Throwable e) {
			Util.trace(e);
		}
		
		return null;
	}

	
	@Override
	public int getVarNum() {
		return vars.size();
	}


	/**
	 * Getting mathematical expression.
	 * @return mathematical expression.
	 */
	public String getExpr() {
		return expr;
	}
	
	
	@Override
	public String toString() {
		StringBuffer text = new StringBuffer("Function \"" + expr);
		if (optimizer != null)
			text.append("\" gets optimal at " + optimizer.toString());
		
		return text.toString();
	}

	
}
