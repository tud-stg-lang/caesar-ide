/**********************************************************************
Copyright (c) 2002 IBM Corporation and others.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html
Contributors:
Mik Kersten, Julie Waterhouse - initial version
...
**********************************************************************/
package org.caesarj.ui.wizard;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;


public class CaesarConfigWizard extends Wizard {

	private CaesarConfigPage mainPage;
	private NewJavaProjectWizardPage fJavaPage;//test
	private WizardNewProjectCreationPage fMainPage;

	/** 
	 * Adds the CaesarConfigPage (only page for this wizard)
	 */
	public void addPages() {
		
		mainPage = new CaesarConfigPage();
		addPage(mainPage);
//		fMainPage = new WizardNewProjectCreationPage("NewAspectjProjectCreationWizard");
//		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//        fJavaPage = new NewJavaProjectWizardPage(root, fMainPage);
//        addPage(fJavaPage);
	}
	
	/** 
	 * Set-up the title.
	 */
	public void init() {
		setWindowTitle("CaesarJ Configuration Wizard"); 
	}
	
	/**
	 * Callback for the "Finish" button of this wizard.
	 * @return boolean Whether finish() for the single page of this wizard was 
	 * successful.
	 */
	public boolean performFinish() {
		return mainPage.finish();
	}
}