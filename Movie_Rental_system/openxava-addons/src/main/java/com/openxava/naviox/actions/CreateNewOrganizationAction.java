package com.openxava.naviox.actions;

import java.util.*;
import java.util.stream.*;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;

import com.openxava.naviox.model.*;
import com.openxava.naviox.model.Module;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class CreateNewOrganizationAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		Map values = getView().getValues();
		Messages errors = MapFacade.validate(getModelName(), values);
		if (errors.contains()) {
			addErrors(errors);
			return;
		}
		String name = (String) values.get("name");
		Boolean active = (Boolean) values.get("active");
		String theme = (String) values.get("theme"); 
		Organization organization = Organizations.create(name, active, theme); 
		
		copyAdminDataFromAdmin(organization.getId()); 
		String url = getRequest().getContextPath() + "/o/" + organization.getId(); 
		addMessage(name + " " + XavaResources.getString("organization_created") + " <a href='" + url + "'>" + url + "</a>");
	}

	private void copyAdminDataFromAdmin(String newSchema) { 
		Collection<Module> modules = load("Module");
		Collection<Role> roles = load("Role");
		Collection<ModuleRights> rights = load("ModuleRights");
		Collection<Folder> folders = load("Folder");
		XPersistence.commit();
		
		XPersistence.setDefaultSchema(newSchema);
		unsetFolderInModules();
		delete("Folder");
		saveFolders(folders);
		updateModules(modules);
		delete("User");
		delete("ModuleRights");
		delete("Role");
		save(roles);
		saveRights(rights);
		createAdminUser();
		XPersistence.commit();
	}

	private void saveRights(Collection<ModuleRights> rights) {
		rights.stream().forEach(r -> {
			Module module = Module.findByApplicationModule(r.getModule().getApplication(), r.getModule().getName());
			if (module != null) {
				r.setModule(module);
				XPersistence.getManager().persist(r);
			}
		});
	}

	private void unsetFolderInModules() {
		Stream<Module> modulesStream = XPersistence.getManager().createQuery("from Module", Module.class).getResultStream();
		modulesStream.forEach(m -> m.setFolder(null));
		XPersistence.getManager().flush();
	}
	
	private void createAdminUser() { 
		User user = new User();
		user.setName("admin");
		user.setPassword(NaviOXPreferences.getInstance().getInitialPasswordForAdmin());
		XPersistence.getManager().persist(user);
		user.addRole("user");
		user.addRole("admin");
	}


	private void save(Collection entities) {
		entities.stream().forEach(e -> XPersistence.getManager().persist(e));
	}
	
	private void updateModules(Collection<Module> modules) {
		modules.stream().forEach(m -> {
			Module module = Module.findByApplicationModule(m.getApplication(), m.getName());
			if (module != null) {
				module.setHidden(m.isHidden());
				module.setUnrestricted(m.isUnrestricted());
				module.setOrderInFolder(m.getOrderInFolder()); 
				module.setDesktop(m.isDesktop());
				module.setMobile(m.isMobile());
				if (m.getFolder() != null && !m.getFolder().isRoot()) {
					Folder folder = Folder.findByName(m.getFolder().getName());
					module.setFolder(folder);
				}
			}
		});
	}

	private void saveFolders(Collection<Folder> folders) {
		folders.stream().filter(f -> !f.isRoot()).forEach(f -> {
			XPersistence.getManager().persist(cloneFolder(f));			
		});
		folders.stream().filter(f -> f.getParent() != null && !f.getParent().isRoot()).forEach(f -> {
			Folder n = Folder.findByName(f.getName());
			n.setParent(Folder.findByName(f.getParent().getName()));
		});
		Folder.createROOT(); 
	}
	
	private Folder cloneFolder(Folder f) {
		Folder newFolder = new Folder();
		newFolder.setName(f.getName());
		newFolder.setIcon(f.getIcon());
		newFolder.setOrderInFolder(f.getOrderInFolder());
		return newFolder;
	}

	private Collection load(String entity) {
		Query query = XPersistence.getManager().createQuery("from " + entity);
		return new ArrayList(query.getResultList());
	}
	
	private void delete(String entity) {
		XPersistence.getManager().createQuery("delete from " + entity).executeUpdate();
	}

}
