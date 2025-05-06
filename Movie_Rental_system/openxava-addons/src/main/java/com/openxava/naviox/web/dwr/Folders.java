package com.openxava.naviox.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.servlets.*;

/**
 * 
 * @author Javier Paniza
 */
public class Folders { 
	
	private static Log log = LogFactory.getLog(Folders.class);
	
	public String goFolder(HttpServletRequest request, HttpServletResponse response, String folderOid) {
		try {
			com.openxava.naviox.Folders folders = (com.openxava.naviox.Folders) request.getSession().getAttribute("folders");
			if (folderOid == null) folders.goBack();
			else folders.goFolder(folderOid);
			String prefix = Browsers.isMobile(request)?"/phone":"/naviox";
			String result = Servlets.getURIAsString(request, response, prefix + "/modulesMenu.jsp");
			return result;
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("go_folder_error"), ex);
			return null; 
		}
		finally {
			ModuleManager.commit();
		}
	}
	
	public String goBack(HttpServletRequest request, HttpServletResponse response) { 
		return goFolder(request, response, null);
	}
	
}
