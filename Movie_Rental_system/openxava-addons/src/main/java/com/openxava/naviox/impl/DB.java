package com.openxava.naviox.impl;

import java.lang.reflect.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.metamodel.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.jpa.*;
import org.openxava.tools.*;
import org.openxava.util.*;

import com.openxava.naviox.model.*;
import com.openxava.naviox.model.Module;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class DB {
	
	private static Log log = LogFactory.getLog(DB.class);
	
	public static final String ROOT_SCHEMA;
	
	static {
		ROOT_SCHEMA = (String) XPersistence.getManager().getEntityManagerFactory().getProperties().get("hibernate.default_schema");
	}
		
	public static void init() {
		try {
			try {		
				populateDB();
			}
			catch (PersistenceException ex) {
				log.info(XavaResources.getString("xavapro_first_time_execution"));
				XPersistence.rollback();
				createDB(false);  
				populateDB();
			}
			if (NaviOXPreferences.getInstance().isUpdateNaviOXTablesInOrganizationsOnStartup()) {
				populateAllTenancies();
			}
			XPersistence.commit(); 
		}
		catch (RuntimeException ex) {
			XPersistence.rollback(); // This is not a total rollback, but at least we release the connection
			throw ex;
		}
	}
	
 	private static void populateAllTenancies() {
		for (String schema: Organization.getAllIds()) {
			try { 
				XPersistence.commit(); 
				XPersistence.setDefaultSchema(schema);
				populateDB(schema); 
				XPersistence.reset();
			}
			catch (Exception ex) { 
				log.error(XavaResources.getString("problems_populating_tenancy", schema), ex);
			}
		}			
	}

	public static void createTenancy(String schema) {
		createTenancy(schema, null);
	}
	
	/**
	 * @since 5.6 
	 */
	public static void createTenancy(String schema, String adminUser) { 
		XPersistence.commit(); 
		XPersistence.setDefaultSchema(schema);
		XPersistence.resetEntityManagerFactory(); 
		try { 
			createDB(true);
			populateDB(schema, adminUser); 
			XPersistence.reset(); 			
		}
		catch (RuntimeException ex) {
			XPersistence.rollback(); // This is not a total rollback, but at least we release the connection
			throw ex;
		}
	}

	private static void populateDB() {
		populateDB(null, null);
	}
	
	private static void populateDB(String schema) { 
		populateDB(schema, null);
	}

	private static void populateDB(String schema, String adminUser) { 
		createModules();
		createUserRolesConfiguration(adminUser); 
		setupRights(); 
		createSelfSignUpRole();
		if (schema != null) createJoinedRole(); // Only for organizations
		createROOTFolder(); 
		compressOrderIndexInFolder(); 
	} 	
	
	private static void compressOrderIndexInFolder() {
		if (countOrderInFolderTooBig("Module") + countOrderInFolderTooBig("Folder") > 0) {
			for (Folder folder: Folder.findAll()) {
				List<Module> modules = findByFolder(folder, "Module", "folder");
				int idx = 0;
				for (Module module: modules) {
					module.setOrderInFolder(idx++);
				}
				List<Folder> subfolders = findByFolder(folder, "Folder", "parent");
				idx = 0;
				for (Folder subfolder: subfolders) {
					subfolder.setOrderInFolder(idx++);
				}				
			}
			resetOrderInFolderForOrphans("Module", "folder");
			resetOrderInFolderForOrphans("Folder", "parent");
		}
	
		XPersistence.commit();
	}

	private static void resetOrderInFolderForOrphans(String model, String parent) {
		Query modulesQuery = XPersistence.getManager().createQuery( 
			"update " + model + " m set m.orderInFolder = 0 where m.orderInFolder = 9999 and m." + parent + " is null");
		modulesQuery.executeUpdate();
	}

	private static List findByFolder(Folder folder, String model, String folderReference) {
		Query query = XPersistence.getManager().createQuery( // Not folder.getModule() because performance problem because of possible high index
			"from " + model + " m where m." + folderReference + " = :folder order by m.orderInFolder");
		query.setParameter("folder", folder);
		return query.getResultList();
	}
	
	private static long countOrderInFolderTooBig(String model) { 
		Query countQuery = XPersistence.getManager().createQuery( 
			"select count(*) from " + model + " m where m.orderInFolder > 500"); 	
		return (Long) countQuery.getSingleResult();
	}

	private static void setupRights() {  
		try {
			if (ModuleRights.countForApplication(MetaModuleFactory.getApplication()) > 0) return;
			Folder adminFolder = Folder.findByName("Admin");
			Collection<Module> adminModules = new ArrayList<Module>();
			Collection<Module> userModules = new ArrayList<Module>();
			for (Module module : Module.findByApplication(MetaModuleFactory.getApplication())) {
				if (module.getName().equals("User") || module.getName().equals("Role")
						|| module.getName().equals("Module") || module.getName().equals("Folder")
						|| module.getName().equals("ModuleRights") || module.getName().equals("Organization")
						|| module.getName().equals("Configuration") || module.getName().equals("ConfigurationRecord")
						|| module.getName().equals("SessionRecord") || module.getName().equals("History")) { 
					adminModules.add(module);
					module.setFolder(adminFolder);
					if (module.getName().equals("ModuleRights") 
						|| module.getName().equals("SessionRecord")	
						|| module.getName().equals("ConfigurationRecord")) {
						module.setHidden(true);
					}
				} else {
					userModules.add(module);
					if (module.getName().equals("ChangePassword") || module.getName().equals("MyPersonalData")) { 
						module.setFolder(adminFolder);
					}
				}
			}
			Role adminRole = Role.find("admin");
			if (adminRole != null) {
				adminRole.setModules(adminModules);
			}
			else {
				log.warn(XavaResources.getString("role_not_exist_not_assign_modules", "admin"));
			}
			Role userRole = Role.find("user");
			if (userRole != null) {
				userRole.setModules(userModules);
			}
			else {
				log.warn(XavaResources.getString("role_not_exist_not_assign_modules", "user"));
			}
		} 
		finally {
			XPersistence.commit();
		}			
	}

	private static void createModules() { 
		Collection<MetaModule> inApp = MetaModuleFactory.createAll();
		Collection<Module> inDB = Module.findByApplication(MetaModuleFactory.getApplication());
		try {
			for (Iterator<MetaModule> it = inApp.iterator(); it.hasNext(); ) {
				MetaModule metaModule = it.next();
				Module module = Module.findByMetaModule(metaModule);
				if (module == null) {
					module = Module.createFromMetaModule(metaModule);
				}
				inDB.remove(module);
			}
			for (Module module: inDB) {
				XPersistence.getManager().remove(module); 
			}	
			XPersistence.commit();
		}
		catch (Exception ex) { 
			log.warn(XavaResources.getString("updating_modules_database_problem"), ex);
			XPersistence.rollback();
		}
	}
	
	private static void createDB(boolean allEntities) {
		if (isAutoUpdateSchema()) {
			if (usesSequences()) createSequences();
			return;
		}
		log.info(XavaResources.getString("creating_xavapro_tables"));
		SchemaTool tool = new SchemaTool();
		tool.setCommitOnFinish(false);
		if (!allEntities) {
			tool.addAnnotatedClass(User.class);
			tool.addAnnotatedClass(Role.class);
			tool.addAnnotatedClass(Module.class);
			tool.addAnnotatedClass(Folder.class);
			tool.addAnnotatedClass(ModuleRights.class);
			tool.addAnnotatedClass(Organization.class);
			tool.addAnnotatedClass(ConfigurationRecord.class); 
			tool.addAnnotatedClass(SessionRecord.class); 
			tool.addAnnotatedClass(History.class); 
		}		
		tool.generateSchema();
	}
	
	private static boolean usesSequences() { 
		for (EntityType type: XPersistence.getManager().getMetamodel().getEntities()) {
			for (Attribute attribute: (Collection<Attribute>) type.getAttributes()) {
				Member member = attribute.getJavaMember();
				if (member instanceof Field || member instanceof Method) {
					AnnotatedElement element = (AnnotatedElement) member;
					Class entityClass = type.getJavaType();
					if (element.isAnnotationPresent(GeneratedValue.class)) {
						GeneratedValue generatedValue = element.getAnnotation(GeneratedValue.class); 
						if (generatedValue.strategy() == GenerationType.SEQUENCE) return true;
					}
				}
			}
		}
		return false;
	}
	
	

	private static void createSequences() { 
		SchemaTool tool = new SchemaTool();
		tool.setCommitOnFinish(false);
		tool.setOnlySequences(true);
		tool.generateSchema();
	}

	private static boolean isAutoUpdateSchema() {
		Map<String, Object> properties = XPersistence.getManager().getEntityManagerFactory().getProperties(); 
		String ddlAuto = (String) properties.get("hibernate.hbm2ddl.auto");
		if (ddlAuto != null && "update".equalsIgnoreCase(ddlAuto.trim())) return true;
		String schemaGenerationAction = (String) properties.get("javax.persistence.schema-generation.database.action");
		if (schemaGenerationAction != null && "update".equalsIgnoreCase(schemaGenerationAction.trim())) return true;
		return false;
	}

	
	private static void createUserRolesConfiguration(String adminUser) {
		if (User.count() == 0) { 
			log.info(XavaResources.getString("creating_default_user_roles_configuration"));
			Collection<Role> roles = new ArrayList<Role>();
			Role adminRole = new Role();
			adminRole.setName("admin");
			adminRole.setDescription("Manages users, roles, folder, modules, organizations, etc."); 
			roles.add(adminRole); 
			
			Role userRole = new Role();		
			userRole.setName("user");
			userRole.setDescription("With access to all modules except administration ones"); 
			roles.add(userRole);
			
			User admin = new User();		
			if (adminUser == null) { 
				admin.setName("admin");
				admin.setPassword(NaviOXPreferences.getInstance().getInitialPasswordForAdmin());
			}
			else {
				admin.setName(adminUser);
				admin.setPassword(UUID.randomUUID().toString().substring(0, 30)); 
			}
			admin.setRoles(roles);
			
			Folder adminFolder = new Folder();
			adminFolder.setName("Admin");
			adminFolder.setIcon("account-multiple"); 
			XPersistence.getManager().persist(adminRole);
			XPersistence.getManager().persist(userRole);
			XPersistence.getManager().persist(admin);		
			XPersistence.getManager().persist(adminFolder); 
		} 
		
		XPersistence.commit();
	}

	private static void createSelfSignUpRole() { 
		if (Role.findSelfSignUpRole() == null) {		
			log.info(XavaResources.getString("creating_self_sign_up_role"));
			Roles.createSelfSignUpRole();
		}
		XPersistence.commit();
	}
	
	private static void createJoinedRole() { 
		if (Role.findJoinedRole() == null) { 		
			log.info(XavaResources.getString("creating_joined_role")); 
			Role.createJoinedRole();
		}
		XPersistence.commit();
	}
	
	private static void createROOTFolder() {  
		if (Folder.getROOT() == null) { 		
			log.info(XavaResources.getString("creating_root_folder"));  
			Folder.createROOT();
		}
		XPersistence.commit();
	}

}
