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
 * $Id: CaesarConfigWizard.java,v 1.6 2005-01-24 16:57:22 aracic Exp $
 */

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