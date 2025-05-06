package com.openxava.naviox.actions;

import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;

/**
 *
 * @since 5.4
 * @author Javier Paniza
 */
public class UnlockAction extends ViewBaseAction implements ICustomViewAction { 
	
	@Inject
	private Boolean locked;

	@Inject
	private Boolean locking;

	public void execute() throws Exception {
		if (SignInHelper.isAuthorized(getRequest(), Users.getCurrent(), getView().getValueString("password"), getErrors(), "invalid_password")) { 
			locked = false;
			locking = false;
			closeDialog();
		}		
	}

	public String getCustomView() throws Exception {
		return locking?ICustomViewAction.SAME_VIEW:ICustomViewAction.PREVIOUS_VIEW;
	}

}
