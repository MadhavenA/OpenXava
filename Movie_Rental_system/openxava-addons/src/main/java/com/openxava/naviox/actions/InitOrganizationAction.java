package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.util.*;
import org.openxava.web.style.*;

/**
 * 
 * @since 6.4
 * @author Javier Paniza
 */

public class InitOrganizationAction extends TabBaseAction {

	public void execute() throws Exception {
		String themes = XavaPreferences.getInstance().getThemes();
		if (Is.emptyString(themes)) { 
			getView().setHidden("theme", true);
			getTab().removeProperty("theme");
			return;
		}
		for (String theme: themes.split(",")) {
			String value = theme.trim();
			String label = Themes.cssToLabel(value);
			getView().addValidValue("theme", value, label);			
		}
	}

}
