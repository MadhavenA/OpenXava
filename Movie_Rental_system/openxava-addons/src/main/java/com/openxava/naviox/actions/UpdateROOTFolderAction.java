package com.openxava.naviox.actions;

import org.openxava.actions.*;
import com.openxava.naviox.model.*;

/** 
 * @since 6.1
 * @author Javier Paniza
 */
public class UpdateROOTFolderAction extends BaseAction {

	public void execute() throws Exception {
		Folder.updateROOT();		
	}

}
