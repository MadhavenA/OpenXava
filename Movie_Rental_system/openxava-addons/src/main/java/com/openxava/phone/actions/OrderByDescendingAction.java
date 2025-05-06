package com.openxava.phone.actions;

/**
 * 
 * @author Javier Paniza
 */
public class OrderByDescendingAction extends OrderByBaseAction  {
	
	public void execute() throws Exception {
		if (getTab().isOrderDescending(getProperty())) return;
		resetCondition();
		getTab().orderBy(getProperty());
		if (getTab().isOrderDescending(getProperty())) return;
		getTab().orderBy(getProperty());
	}

}
