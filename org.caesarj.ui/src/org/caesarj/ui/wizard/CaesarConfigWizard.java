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

import org.eclipse.jface.wizard.Wizard;

/**
 * This wizard sets preferences to customize the AJDT enviroment.
 * Specifically:
 * 1. Turn off analyze annotations (red squiggles)
 * 2. Make the AspectJ editor the default for .java files
 * 3. Set the unused imports to "warning"
 * 4. Add Aspect and AspectJ Project wizards to the File->New menu for the
 * Java perspective (currently disabled)
 */
public class CaesarConfigWizard extends Wizard {
	private CaesarConfigPage mainPage;

	/** 
	 * Adds the CaesarConfigPage (only page for this wizard)
	 */
	public void addPages() {
		mainPage = new CaesarConfigPage();
		addPage(mainPage);
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