package com.openxava.naviox.impl;

import java.util.*;

import org.openxava.application.meta.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;

import com.openxava.naviox.model.*;


/**
 * Members refiner, to remove the unauthorized members or to mark them as read only.
 * 
 * @author Javier Paniza
 */
public class MembersRefiner {
	
	private static MembersRefiner instance;

	public void refine(MetaModule metaModule, View view) {
		String currentUser = Users.getCurrent();
		if (currentUser == null) return;
		User user = User.find(currentUser);
		if (user == null) return; 
		Collection<MetaMember> excludedMembers = user.getExcludedMetaMembersForMetaModule(metaModule);
		for (MetaMember member: excludedMembers) {		
			view.setHidden(member.getName(), true); 
		}
		Collection<MetaMember> readOnlyMembers = user.getReadOnlyMetaMembersForMetaModule(metaModule);
		for (MetaMember member: readOnlyMembers) {	
			view.setEditable(member.getName(), false); 
		}
	}
	
	public void polish(MetaModule metaModule, MetaTab metaTab) {
		String currentUser = Users.getCurrent();
		if (currentUser == null) return;
		User user = User.find(currentUser);
		if (user == null) return; 
		
		Collection<MetaMember> excludedMembers = user.getExcludedMetaMembersForMetaModule(metaModule);
		for (MetaMember excludedMember: excludedMembers) {
			metaTab.dropMember(excludedMember.getName());
		}
		
		Map<String, String> modelsOfQualifiedProperties = new HashMap<String, String>();
		for(String propertyName: metaTab.getRemainingPropertiesNames()) {
			if (propertyName.contains(".")) {
				MetaProperty metaProperty = metaTab.getMetaModel().getMetaProperty(propertyName);
				modelsOfQualifiedProperties.put(metaProperty.getMetaModel().getName(), Strings.noLastToken(propertyName, "."));
			}
		}
		
		for (Map.Entry<String, String> model: modelsOfQualifiedProperties.entrySet()) {
			MetaModule module = metaModule.getMetaApplication().getMetaModule(model.getKey());
			Collection<MetaMember> excludedMembersForQualifiedProperties = user.getExcludedMetaMembersForMetaModule(module);
			for (MetaMember excludedMember: excludedMembersForQualifiedProperties) {
				String excludedQualifiedProperty = model.getValue() + excludedMember.getName();
				metaTab.dropMember(excludedQualifiedProperty);
			}	
		}
	}
	
	public String refine(MetaModule metaModule, String email, String changes) { 
       	User u = User.findByEmail(email);
       	if (u != null) {
       		Collection<MetaMember> excludedMembers = u.getExcludedMetaMembersForMetaModule(metaModule);
       		for (MetaMember m: excludedMembers) {
       			if (changes.contains("data-property='" + m.getName() + "'") || 
       				changes.contains("data-property='" + m.getName() + ".")) 
       			{
       				changes = changes.replaceAll("<li data-property='" + m.getName() + "['\\.].*?</li>", "");
       			}
       		}
       	}
       	return changes;
   	}

	public static void init() {
		if (instance == null) instance = new MembersRefiner();
		View.setPolisher(instance); 
		Tab.setRefiner(instance);
		EmailNotifications.setRefiner(instance); 
	}
	
}
