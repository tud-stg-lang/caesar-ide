

package org.caesarj.ui.wizard;

import org.eclipse.jface.wizard.Wizard;


public class CaesarConfigWizard extends Wizard {

	private CaesarConfigPage mainPage;
//	private NewJavaProjectWizardPage fJavaPage;//test
//	private WizardNewProjectCreationPage fMainPage;

	/** 
	 * Adds the CaesarConfigPage (only page for this wizard)
	 */
	public void addPages() {
		
		this.mainPage = new CaesarConfigPage();
		addPage(this.mainPage);
	}
	
	/** 
	 * Set-up the title.
	 */
	public void init() {
		setWindowTitle("CaesarJ Configuration Wizard");  //$NON-NLS-1$
	}
	
	/**
	 * Callback for the "Finish" button of this wizard.
	 * @return boolean Whether finish() for the single page of this wizard was 
	 * successful.
	 */
	public boolean performFinish() {
		return this.mainPage.finish();
	}
}