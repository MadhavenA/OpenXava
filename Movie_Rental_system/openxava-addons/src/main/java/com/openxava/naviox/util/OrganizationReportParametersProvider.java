package com.openxava.naviox.util;


import javax.servlet.http.*;
import org.openxava.util.*;

/**
 * @since 6.5
 * @author Javier Paniza
 */

public class OrganizationReportParametersProvider extends DefaultReportParametersProvider {

	private HttpServletRequest request;

	public String getOrganization() {
		String organization = Organizations.getCurrentName(request);
		return organization==null?super.getOrganization():organization;
	}
	
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
		this.request = request;
	}

}
