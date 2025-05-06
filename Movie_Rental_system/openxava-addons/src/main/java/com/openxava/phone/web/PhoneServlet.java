package com.openxava.phone.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;

/**
 * 
 * @author Javier Paniza
 */
public class PhoneServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String [] uri = request.getRequestURI().split("/");
		int baseIndex = Is.emptyString(request.getContextPath())?0:1;		
		if (uri.length < 3 + baseIndex) {
			response.getWriter().print(XavaResources.getString(request, "module_name_missing"));
			return;
		}
		HttpServletRequest phoneRequest = new HttpServletRequestWrapper((HttpServletRequest)request) {
			
			public String getHeader(String key) {
				if ("user-agent".equals(key)) return super.getHeader("user-agent") + " XAVAPHONE_BROWSER"; 
				return super.getHeader(key); 
			}
			
		};

		RequestDispatcher dispatcher = request.getRequestDispatcher(
			"/phone/phoneModule.jsp?application=" + MetaModuleFactory.getApplication() + "&module=" + uri[baseIndex + 2] + "&friendlyURL=true"); 	
		dispatcher.forward(phoneRequest, response);
	}
		
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

}
