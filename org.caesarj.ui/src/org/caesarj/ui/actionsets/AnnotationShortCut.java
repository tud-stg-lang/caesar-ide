/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: AnnotationShortCut.java,v 1.7 2005-01-24 16:57:22 aracic Exp $
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
