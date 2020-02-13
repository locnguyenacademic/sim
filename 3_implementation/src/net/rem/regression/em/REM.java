package net.rem.regression.em;

import net.rem.em.EM;
import net.rem.regression.RM;

/**
 * This interface is an indicator of any algorithm that applying expectation maximization algorithm into learning regression model.
 * It is called REM algorithm.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface REM extends REMRemoteTask, RM, EM {

	
}
