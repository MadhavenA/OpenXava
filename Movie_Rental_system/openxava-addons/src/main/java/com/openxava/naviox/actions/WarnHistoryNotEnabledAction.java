package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.util.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class WarnHistoryNotEnabledAction extends BaseAction {

	public void execute() throws Exception {
		if (!XavaPreferences.getInstance().getAccessTrackerProvidersClasses().contains(HistoryAccessTrackerProvider.class.getName())) {
			addWarning("history_not_enabled", "'" + HistoryAccessTrackerProvider.class.getName() + "'");
		}
	}

}
