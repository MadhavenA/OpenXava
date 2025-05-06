package com.openxava.naviox;

import java.util.*;

import javax.servlet.http.*;

import org.openxava.application.meta.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class Folders implements java.io.Serializable {
	
	private String folderId; 
	private Modules modules;
	
	private boolean applicationNameAsRootLabel;
	private boolean includeFixedAndBoookmarkModules; 

	public String getFolderLabel() { 
		return getLabel(getFolder());
	}
	
	public String getParentFolderLabel() {
		if (getFolder() == null) return "";
		return getLabel(getFolder().getParent());
	}
	
	private String getLabel(Folder folder) {
		return folder==null || folder.isRoot()?getRootLabel():folder.getLabel(); 
	}
	
	private String getRootLabel() { 
		return isApplicationNameAsRootLabel()?getApplicationLabel():Labels.get("home");
	}
	
	public String getApplicationLabel() { 
		return MetaApplications.getMetaApplication(MetaModuleFactory.getApplication()).getLabel();
	}

	public Folder getFolder() {
		return folderId == null?null:Folder.find(folderId);
	}
	
	public void goFolder(String oid) { 
		this.folderId = oid; 
	}
	
	public void goBack() { 
		Folder parent = getFolder().getParent();
		this.folderId = parent==null?null:parent.getId();
	}
	
	public boolean isRoot() { 
		return this.folderId == null || getFolder().isRoot(); 
	}
		
	
	public Collection<Folder> getSubfolders(HttpServletRequest request) { 
		return getSubfolders(request, getFolder());
	}
	
	public Collection<Folder> getSubfolders(HttpServletRequest request, Folder folder) { 
		Collection<Folder> subfolders = new ArrayList<Folder>();
		for (Folder subfolder: Folder.findByParent(folder)) {
			if (!getFolderModules(request, subfolder).isEmpty() || !getSubfolders(request, subfolder).isEmpty()) { 
				subfolders.add(subfolder);
			}
		}
		return subfolders;
	}
	
	public List<MetaModule> getFolderModules(HttpServletRequest request) {  
		return getFolderModules(request, getFolder()); 
	}
	
	private List<MetaModule> getFolderModules(HttpServletRequest request, Folder folder) { 
		Collection<com.openxava.naviox.model.Module> folderModules = folder == null || folder.isRoot()?com.openxava.naviox.model.Module.findInRoot():folder.getModules(); 
		List<MetaModule> result = new ArrayList<MetaModule>();
		for (com.openxava.naviox.model.Module module: folderModules) {
			if (!module.getApplication().equals(MetaModuleFactory.getApplication())) continue; // Because we can share the database schema by several applications
			try { 
				MetaModule metaModule = MetaModuleFactory.create(module.getApplication(), module.getName());
				if (!includeFixedAndBoookmarkModules && this.modules.getFixedModules(request).contains(metaModule)) continue; 
				if (!includeFixedAndBoookmarkModules && this.modules.getBookmarkModules(request).contains(metaModule)) continue; 			 
				if (this.modules.isModuleInMenu(request, metaModule)) { 
					result.add(metaModule);
				}
			}
			catch (org.openxava.util.ElementNotFoundException ex) {
				// Because we can remove modules from application.xml and they still be in db
			}
		}
		return result;
	}

	public void setModules(Modules modules) {
		this.modules = modules;
	}
	
	public boolean isApplicationNameAsRootLabel() {
		return applicationNameAsRootLabel;
	}

	public void setApplicationNameAsRootLabel(boolean applicationNameAsRootLabel) {
		this.applicationNameAsRootLabel = applicationNameAsRootLabel;
	}

	public boolean isIncludeFixedAndBoookmarkModules() {
		return includeFixedAndBoookmarkModules;
	}

	public void setIncludeFixedAndBoookmarkModules(boolean includeFixedAndBoookmarkModules) {
		this.includeFixedAndBoookmarkModules = includeFixedAndBoookmarkModules;
	}

}
