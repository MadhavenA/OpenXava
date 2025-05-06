package com.openxava.naviox.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.jpa.*;
import org.openxava.util.*;
import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrganizationServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int baseIndex = Is.emptyString(request.getContextPath())?0:1;
		String [] uri = request.getRequestURI().split("/", baseIndex + 4); 
		if (uri.length < baseIndex + 3) { 
			response.getWriter().print(XavaResources.getString(request, "organization_name_missing"));
			return;
		}
		String organization = uri[baseIndex + 2]; 
		String originalURI = uri.length < baseIndex + 4?"":uri[baseIndex + 3]; 
		if (Is.emptyString(originalURI) && !request.getRequestURI().endsWith("/")) {
			response.sendRedirect(request.getRequestURI() + "/");
			return;
		}
		String separator = originalURI.contains("?")?"&":"?";
		if (originalURI.equals("phone")) originalURI = originalURI + "/"; 
		String url = ("/" + originalURI).replace("/m/", "/modules/") + separator + "organization=" + organization;
		Organization.setUp();
		Configuration.getInstance(); // To init Configuration before changing schema
		XPersistence.commit(); 
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		boolean userActive = Organizations.init(request, organization);
		if (!userActive) {
			response.sendError(403, XavaResources.getString("user_disabled_for_this_organization"));
			return;			
		}
		dispatcher.forward(new SecureRequest(request), response);
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}

