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
 */
package org.caesarj.ui.preferences;

import org.caesarj.ui.CJDTConfigSettings;
import org.caesarj.ui.CaesarPlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class implements the main preferences page for the Plugi-in.
 * 
 * TODO - Since the preferences are actually not all stored in one
 * PreferenceStore, we should make several pages, one for each. This
 * class implements only the options in the Wizzard, just for testing.
 * 
 * TODO - Use ResourceBundles for internationalization
 * 
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 * 
 */
public class CaesarJPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	/**
	 * Constructor
	 */
	public CaesarJPreferencePage() {
		super("CaesarJ", FLAT);			//$NON-NLS-1$
		this.setTitle("CaesarJ Preferences");		//$NON-NLS-1$
		this.setDescription("To customize your CaesarJ Plugin choose your preferences");	//$NON-NLS-1$	
	}

	/**
	 * Returns the preference store for the plugin
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		return CaesarPlugin.getDefault().getPreferenceStore();
	}
	
	/**
	 * Creates the field editors.
	 * 
	 * TODO - A better layout should be used, instead of FLAT.
	 * TODO - Solve the preference problems
	 */
	protected void createFieldEditors() {

		// get the parent composite
		Composite parent = getFieldEditorParent();
		
		addField(
			new BooleanFieldEditor(
				CaesarJPreferences.CAESAR_ANALIZE_ANNOTATIONS,
				"Default setting: annotation while typing",							//$NON-NLS-1$
				parent));
		
		addField(
			new BooleanFieldEditor(
				CaesarJPreferences.CAESAR_AUTO_SWITCH, 
				"Auto annotation switch while changing editors", 					//$NON-NLS-1$
				parent));
		

		addField(
			new BooleanFieldEditor(
				CaesarJPreferences.CAESAR_IS_DEFAULT_EDITOR,						
				"Make the CaesarJ editor the default java - editor", 				//$NON-NLS-1$
				parent));
		
		addField(
			new BooleanFieldEditor(
				CaesarJPreferences.CAESAR_PREF_CONFIG_DONE, 
				"Open options dialog next time you open the CaesarJ perspective?", 	//$NON-NLS-1$
				parent));
	}
	
	/**
	 * We have to override this method to create a hook for some options.
	 * 
	 * TODO - REMOVE this method. We should create more pages and remove this method
	 */
	public boolean performOk() {
		if (! super.performOk()) {
			return false;
		}
		
		CJDTConfigSettings.applyCaesarPreferences();
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		CJDTConfigSettings.updateCaesarPreferences();
	}
}
