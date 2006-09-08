/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: WeaveShortCut.java,v 1.1 2006-09-08 13:37:01 thiago Exp $
 */

package org.caesarj.ui.actionsets;

import org.caesarj.ui.CaesarPlugin;
import org.caesarj.ui.preferences.CaesarJPreferences;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Shortcut to set if the caesar compiler should run the weaver or not
 * 
 * @author Thiago Tonelli Bartolomei <thiago.bartolomei@gmail.com>
 */
public class WeaveShortCut implements IWorkbenchWindowActionDelegate, IPropertyChangeListener {
	
	private boolean status; 
	
	private IAction action = null;
	
	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.status = CaesarJPreferences.isRunWeaver();
		CaesarPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		if (this.action != null) {
			this.action.setChecked(this.status);
		}
	}

	public void run(IAction action) {
		CaesarJPreferences.setRunWeaver(!this.status);
		this.status = CaesarJPreferences.isRunWeaver();
		action.setChecked(this.status);
		this.action = action;
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		action.setChecked(this.status);
		this.action = action;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty() == CaesarJPreferences.CAESAR_RUN_WEAVER) {
			this.status = CaesarJPreferences.isRunWeaver();
			if (this.action != null) {
				this.action.setChecked(this.status);
			}
		}
	}
}
