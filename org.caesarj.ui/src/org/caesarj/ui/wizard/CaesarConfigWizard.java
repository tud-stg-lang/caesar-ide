

package org.caesarj.ui.wizard;

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