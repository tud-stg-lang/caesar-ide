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
 * $Id: CaesarJPreferences.java,v 1.6 2005-02-15 17:39:12 gasiunas Exp $
 */

package org.caesarj.ui.preferences;

import org.caesarj.ui.CaesarPlugin;
import org.eclipse.jface.preference.IPreferenceStore;


public class CaesarJPreferences {
	
	public static final String CAESAR_PREF_CONFIG_DONE = "org.caesarj.ui.preferences.ajdtPrefConfigDone"; //$NON-NLS-1$

	public static final String CAESAR_AUTO_SWITCH = "org.caesarj.ui.preferences.autoSwitch"; //$NON-NLS-1$
	
	public static final String CAESAR_IS_DEFAULT_EDITOR = "org.caesarj.ui.preferences.isDefaultEditor";
	
	public static final String CAESAR_ANALIZE_ANNOTATIONS = "org.caesarj.ui.preferences.analizeAnnotations";
	
	public static boolean isAutoSwitch() {
		return getCjPrefStore().getBoolean(CAESAR_AUTO_SWITCH);
	}
	
	public static void setAutoSwitch(boolean arg) {
		getCjPrefStore().setValue(CAESAR_AUTO_SWITCH, arg);
	}
	
	public static boolean isCaesarDefaultEditor() {
		return getCjPrefStore().getBoolean(CAESAR_IS_DEFAULT_EDITOR);
	}
	
	public static void setCaesarDefaultEditor(boolean arg) {
		getCjPrefStore().setValue(CAESAR_IS_DEFAULT_EDITOR, arg);
	}
	
	public static boolean isAnalyzeAnnotationsEnabled() {
		return getCjPrefStore().getBoolean(CAESAR_ANALIZE_ANNOTATIONS);
	}
	
	public static void setAnalizeAnnotations(boolean arg) {
		getCjPrefStore().setValue(CAESAR_ANALIZE_ANNOTATIONS, arg);
	}
	
	public static boolean isPrefConfigDone() {
		return getCjPrefStore().getBoolean(CAESAR_PREF_CONFIG_DONE);
	}
	
	public static void setPrefConfigDone(boolean done) {
		getCjPrefStore().setValue(CAESAR_PREF_CONFIG_DONE, done);
	}
	
	private static IPreferenceStore getCjPrefStore() {
		return CaesarPlugin.getDefault().getPreferenceStore();
	}
}