package com.openxava.naviox.actions;

import org.openxava.actions.SaveAction;

import com.openxava.naviox.model.Organization;

/**
 * 
 * @since 6.2
 * @author Javier Paniza
 */
public class ModifyOrganizationAction extends SaveAction {
	
	public void execute() throws Exception {
		super.execute();
		Organization.resetCache();
	}

}
