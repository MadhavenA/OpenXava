package com.openxava.naviox.web;

import javax.servlet.http.*;

import org.openxava.web.style.*;
import com.openxava.naviox.util.*; 
import com.openxava.naviox.model.*;

/** 
 * @since 6.4
 * @author Javier Paniza
 */
public class ThemeProvider implements IThemeProvider {
	
	public static void init() {
		Themes.setProvider(new ThemeProvider());
	}

	public String getCSS(HttpServletRequest request) {
		String organizationId = Organizations.getCurrent(request);
		return Organization.getTheme(organizationId); 
	}

}
