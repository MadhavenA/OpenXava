package com.openxava.phone.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.servlets.*;

/**
 * @author Jeromy Altuna
 * @since  5.8
 */
public class PhoneList {
	
	private static Log log = LogFactory.getLog(PhoneList.class);
	
	public String filter(HttpServletRequest request, HttpServletResponse response, String application, String module, String searchWord) {
		try {
			request.setAttribute("style", com.openxava.phone.web.PhoneStyle.getInstance());
			searchWord = searchWord.replace("&", "%26"); 
			return Servlets.getURIAsString(request, response, "/phone/listCore.jsp?application=" + application + "&module=" + module + "&searchWord=" + searchWord);
		} catch (Exception ex) {
			log.error(XavaResources.getString("display_phone_list_error"), ex);
			return null;
		}		
		finally {
			ModuleManager.commit();
		}
	}
}