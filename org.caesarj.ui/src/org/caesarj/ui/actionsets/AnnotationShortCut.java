/*
 * Created on 05.06.2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.ui.actionsets;

import org.apache.log4j.Logger;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Jochen
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AnnotationShortCut implements IWorkbenchWindowActionDelegate{

	private static Logger log = Logger.getLogger(AnnotationShortCut.class);
	
	private boolean status; 
	
	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.status = JavaPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS);
	}

	public void run(IAction action) {
		log.debug("Annotation action was activated!"); //$NON-NLS-1$
		JavaPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS, !this.status);
		this.status = JavaPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS);
	}

	
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
