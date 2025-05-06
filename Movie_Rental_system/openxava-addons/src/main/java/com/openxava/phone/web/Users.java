package com.openxava.phone.web;

import com.openxava.naviox.model.*;

/**
 * @author Javier Paniza
 */
public class Users {
	
	public static boolean currentNeedsToChangePassword() {
		String userName = org.openxava.util.Users.getCurrent();
		if (userName == null) return false;
		User user = User.find(userName);
		if (user == null) return false;
		return user.isForceChangePassword();
	}

}
