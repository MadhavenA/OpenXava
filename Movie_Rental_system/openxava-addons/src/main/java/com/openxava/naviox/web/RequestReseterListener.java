package com.openxava.naviox.web;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
@WebListener
public class RequestReseterListener implements ServletRequestListener { 
	
	public void requestDestroyed(ServletRequestEvent sre) {
	}
	
	public void requestInitialized(ServletRequestEvent sre) {
		Organizations.setPersistenceDefaultSchema(((HttpServletRequest)sre.getServletRequest()).getSession());
	}

}
