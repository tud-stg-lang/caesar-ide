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
 * $Id: CaesarConfigPage.java,v 1.9 2005-01-24 16:57:22 aracic Exp $
 */

package org.caesarj.ui.wizard;


import org.apache.log4j.Logger;
import org.caesarj.ui.CJDTConfigSettings;
import org.caesarj.ui.preferences.CaesarJPreferences;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


public class CaesarConfigPage extends WizardPage {
	//private IWorkbench workbench;
	private static Logger log = Logger.getLogger(CaesarConfigPage.class);

	// widgets
	private Button caesarJAnnotationCheckbox = null;
	private Button caesarEditorDefaultCheckbox = null;
	private Button caesarJAnnotationAutoSwitshCheckbox = null;
	private Button dontAskAgainCheckbox = null;
	
	public CaesarConfigPage(){

		super("Caesar Preferences"); //$NON-NLS-1$
		this.setTitle("Caesar Preferences");		 //$NON-NLS-1$
		this.setDescription("To costomize your CaesarJ Plugin choose your preferences"); //$NON-NLS-1$
	}
	
	
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		setControl(composite);
	
		// create workbench preferences group
		try {
			Group workbench = new Group(composite, SWT.NONE);
			workbench.setLayout(new GridLayout());
			workbench.setText("CaesarJ Preference"); //$NON-NLS-1$
			workbench.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
				this.caesarJAnnotationCheckbox = new Button(workbench, SWT.CHECK);		
				this.caesarJAnnotationCheckbox.setText("Default setting: annotation while typing"); //$NON-NLS-1$
				this.caesarJAnnotationCheckbox.setSelection(JavaPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS));
			
				this.caesarJAnnotationAutoSwitshCheckbox = new Button(workbench, SWT.CHECK);		
				this.caesarJAnnotationAutoSwitshCheckbox.setText("Auto annotation switch while changing editors"); //$NON-NLS-1$
				this.caesarJAnnotationAutoSwitshCheckbox.setSelection(CaesarJPreferences.isCAESARAutoSwitch());
				
				this.caesarEditorDefaultCheckbox = new Button(workbench, SWT.CHECK);	
				this.caesarEditorDefaultCheckbox.setText("Make the CaesarJ editor the default java - editor"); //$NON-NLS-1$
				this.caesarEditorDefaultCheckbox.setSelection(CJDTConfigSettings.isCaesarJEditorDefault());
							
		new Label(composite, SWT.NONE); // vertical spacer
		}
		catch (Exception e)
		{
			log.warn("Error while drawing ConfigPage", e); //$NON-NLS-1$
		}
		
		this.dontAskAgainCheckbox = new Button(composite, SWT.CHECK);
		this.dontAskAgainCheckbox.setText("Open this dialog next time you open the CaesarJ perspective?"); //$NON-NLS-1$
		this.dontAskAgainCheckbox.setSelection(CaesarJPreferences.isCAESARPrefConfigDone());
	}
	
	
	public boolean finish() {
		
		if (this.caesarJAnnotationCheckbox != null) {
			if (!this.caesarJAnnotationCheckbox.getSelection()) {
				CJDTConfigSettings.disableAnalyzeAnnotations();
			} else
				CJDTConfigSettings.enableAnalyzeAnnotations();
		}
		if (this.caesarJAnnotationAutoSwitshCheckbox != null) {
			CaesarJPreferences.setCAESARAutoSwitch(this.caesarJAnnotationAutoSwitshCheckbox.getSelection());
		}
		if (this.caesarEditorDefaultCheckbox != null) {
			if (this.caesarEditorDefaultCheckbox.getSelection()) {
				CJDTConfigSettings.enableCaesarJEditorDefault();
			} else
				CJDTConfigSettings.disableCaesarJEditorDefault();
		}
		boolean dontAskAgain = this.dontAskAgainCheckbox.getSelection();
		CaesarJPreferences.setCAESARPrefConfigDone(dontAskAgain);
		return true;
	}
}