package com.openxava.naviox.actions;

import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @since 5.4
 * @author Javier Paniza
 */
public class LockAction extends ViewBaseAction implements ICustomViewAction {  
	
	@Inject
	private Boolean locked;
	
	@Inject
	private Boolean locking;
	
	private boolean dialogOpened = false; 


	public void execute() throws Exception {
		if (Users.getCurrent() == null) return;
		if (getView().getModelName().equals("SignIn") && getView().getViewName().equals("Unlock")) return;
		showDialog();
		dialogOpened = true; 
		getView().setTitle("!x:" +  XavaResources.getString("unlock_session")); 
		getView().setModelName("SignIn");
		getView().setViewName("Unlock");
		addActions("SessionLocker.unlock"); 
		locked = true;
		locking = true;
	}

	public String getCustomView() throws Exception { 
		return dialogOpened?ICustomViewAction.DEFAULT_VIEW:ICustomViewAction.SAME_VIEW;
	}
	
}
