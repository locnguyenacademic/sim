package net.pso;

import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.ProfileVector;
import net.hudup.core.logistic.LogUtil;
import net.rem.regression.logistic.speqmath.Parser;

/**
 * This class represents the function is specified by mathematical expression.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExprFunction extends FunctionAbstract {


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
	 */
	public ExprFunction(List<String> varNames, String expr) {
		this.expr = expr != null ? expr : "";
		this.vars = AttributeList.defaultRealAttributeList(varNames.size());
		
		for (int i = 0; i < this.vars.size(); i++) {
			this.vars.get(i).setName(varNames.get(i));
		}
	}

	
	@Override
	public double eval(ProfileVector arg) {
		int n = arg.getAttCount();
		String expr = this.expr;
		for (int i = 0; i < n; i++) {
			String attName =  arg.getAtt(i).getName();
			String replacedText = expr.contains(VAR_INDEX_SPECIAL_CHAR) ? VAR_INDEX_SPECIAL_CHAR + attName : attName;   
			if(!expr.contains(replacedText))
				continue;
			
			if(arg.isMissing(i))
				return Constants.UNUSED; //Cannot evaluate
			Double value = arg.getValueAsReal(attName);
			if(!Util.isUsed(value))
				return Constants.UNUSED; //Cannot evaluate
			
			expr = expr.replaceAll(replacedText, value.toString());
		}
		
		try {
			Parser parser = new Parser();
			return parser.parse2(expr);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return Constants.UNUSED;
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
