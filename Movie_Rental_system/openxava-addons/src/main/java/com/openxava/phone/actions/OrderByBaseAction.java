package com.openxava.phone.actions;

import java.util.*;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

abstract public class OrderByBaseAction extends OrderByAction {
	
	abstract public void execute() throws Exception;
	
	protected void resetCondition() {
		Collection<String> condition = Collections.nCopies(getTab().getMetaPropertiesNotCalculated().size(), "");
		getTab().setConditionValues(condition);
		getTab().setConditionComparators(condition);
	}
	

}
