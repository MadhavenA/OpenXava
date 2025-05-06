package com.openxava.naviox.formatters;

import javax.servlet.http.*;

import org.openxava.formatters.*;
import org.openxava.util.*;
import org.openxava.web.style.*;

/**
 * @since 6.4
 * @author Javier Paniza
 */
public class ThemeListFormatter implements IFormatter {

	public String format(HttpServletRequest request, Object object) throws Exception {
		if (Is.empty(object)) return "";
		return Themes.cssToLabel(object.toString());
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
