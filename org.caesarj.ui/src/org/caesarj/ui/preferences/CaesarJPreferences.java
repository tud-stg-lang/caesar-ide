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
 * $Id: CaesarJPreferences.java,v 1.5 2005-01-24 16:57:22 aracic Exp $
 */

package org.caesarj.ui.preferences;

import org.caesarj.ui.CaesarPlugin;
import org.eclipse.jface.preference.IPreferenceStore;


public class CaesarJPreferences {

	
	public static final String CAESAR_PREF_CONFIG_DONE = "org.caesarj.ui.preferences.ajdtPrefConfigDone"; //$NON-NLS-1$

	public static final String CAESAR_AUTO_SWITCH = "org.caesarj.ui.preferences.autoSwitch"; //$NON-NLS-1$
	
	public static final String PDE_AUTO_IMPORT_CONFIG_DONE = "org.caesarj.ui.preferences.pdeAutoImportConfigDone"; //$NON-NLS-1$

	public static final String ASK_PDE_AUTO_IMPORT = "org.caesarj.ui.preferences.askPdeAutoImport"; //$NON-NLS-1$

	public static void setCAESARAutoSwitch(boolean arg)
	{
		IPreferenceStore store = CaesarPlugin.getDefault().getPreferenceStore();
		store.setValue(CAESAR_AUTO_SWITCH, arg);
	}
	
	public static boolean isCAESARAutoSwitch()
	{
		IPreferenceStore store = CaesarPlugin.getDefault().getPreferenceStore();
		return store.getBoolean(CAESAR_AUTO_SWITCH);
	}
	
	static public void setCAESARPrefConfigDone(boolean done) {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(CAESAR_PREF_CONFIG_DONE, done);
	}

	static public boolean isCAESARPrefConfigDone() {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		return store.getBoolean(CAESAR_PREF_CONFIG_DONE);
	}

	static public void setAskPDEAutoImport(boolean ask) {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(ASK_PDE_AUTO_IMPORT, ask);
	}

	static public boolean askPDEAutoImport() {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		return store.getBoolean(ASK_PDE_AUTO_IMPORT);
	}

	static public void setPDEAutoImportConfigDone(boolean done) {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(PDE_AUTO_IMPORT_CONFIG_DONE, done);
	}

	public static boolean isPDEAutoImportConfigDone() {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		return store.getBoolean(PDE_AUTO_IMPORT_CONFIG_DONE);
	}
}