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
 * $Id: CaesarPerspective.java,v 1.19 2005-02-15 17:39:47 gasiunas Exp $
 */

package org.caesarj.ui.perspective;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import org.caesarj.ui.preferences.CaesarJPreferences;
import org.caesarj.ui.wizard.CaesarConfigWizard;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;

/**
 * Creates a perspective using views, perspective shortcuts and wizard shortcuts.
 */
public class CaesarPerspective implements IPerspectiveFactory {
	
	public static final String ID_CAESAR_HIERARCHY_VIEW = "org.caesarj.ui.views.CaesarHierarchyView";
	
	/**
	 * Constructor for CaesarPerspective
	 */
	public CaesarPerspective() {
		super();
	}

	/**
	 * The initial layout of this perspective will have no editor area. It will 
	 * contain a folder with the Resource Navigator view and Bookmarks view. It will have 
	 * another folder with the Outline and Properties view. It will also contain the MyView
	 * view covered in the section on Views.There is a shortcut to the dialogs solution and the
	 * Java perspective.
	 * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		
		/*
		 * Configure initial view layout
		 */
		
		// Get the editor area.
		String editorArea = layout.getEditorArea();
		
		// Top left: Navigation views 
		IFolderLayout topLeft =
			layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea); //$NON-NLS-1$
		topLeft.addView(JavaUI.ID_PACKAGES);
		topLeft.addPlaceholder(IPageLayout.ID_RES_NAV);
		topLeft.addPlaceholder(JavaUI.ID_TYPE_HIERARCHY);
		
		// Bottom: Status views
		IFolderLayout bottom =
			layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IPageLayout.ID_TASK_LIST);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		
		bottom.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
		bottom.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		bottom.addPlaceholder(JavaUI.ID_JAVADOC_VIEW);
		bottom.addPlaceholder(JavaUI.ID_SOURCE_VIEW);
		
		// Right: Outline views
		IFolderLayout right =
			layout.createFolder("right", IPageLayout.RIGHT,	0.75f, editorArea);
		right.addView(IPageLayout.ID_OUTLINE);
		right.addView(ID_CAESAR_HIERARCHY_VIEW); 
		
		/*
		 * Configure views menu
		 */
		
		// views - java
		layout.addShowViewShortcut(JavaUI.ID_PACKAGES);
		layout.addShowViewShortcut(JavaUI.ID_TYPE_HIERARCHY);
		layout.addShowViewShortcut(ID_CAESAR_HIERARCHY_VIEW);
		layout.addShowViewShortcut(JavaUI.ID_SOURCE_VIEW);
		layout.addShowViewShortcut(JavaUI.ID_JAVADOC_VIEW);

		// views - search
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);
		
		// views - debugging
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		
		/*
		 * Setup ection set
		 */		
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);	
				
		// new actions - Java project creation wizard
		layout.addNewWizardShortcut("org.caesarj.newprojectwizard");
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard");	 //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
		
		layout.addPerspectiveShortcut("org.caesarj.ui.actionsets.AnnotationShortCut"); //$NON-NLS-1$
		layout.setEditorAreaVisible(true);	
		
		//Show Preferences
		CaesarConfigWizard wizard = new CaesarConfigWizard();
		wizard.init();
		if (!CaesarJPreferences.isPrefConfigDone())
		{
			org.eclipse.jface.wizard.WizardDialog dialog = new org.eclipse.jface.wizard.WizardDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					wizard);
			dialog.open();
		}
	}

}
