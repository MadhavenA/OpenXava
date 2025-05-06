package com.openxava.phone.actions;

/**
 * 
 * @author Javier Paniza
 */

public class OrderByAscendingAction extends OrderByBaseAction  {
	
	public void execute() throws Exception {		
		if (getTab().isOrderAscending(getProperty())) return;
		resetCondition();
		getTab().orderBy(getProperty());
	}

}
