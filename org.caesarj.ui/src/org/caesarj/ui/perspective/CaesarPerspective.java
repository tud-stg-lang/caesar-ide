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
		// Get the editor area.
		String editorArea = layout.getEditorArea();
		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft =
			layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea); //$NON-NLS-1$

		topLeft.addView(JavaUI.ID_PACKAGES);
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		IFolderLayout bottomRight =
			layout.createFolder(
				"bottomRight", //$NON-NLS-1$
				IPageLayout.RIGHT,
				0.75f,
				editorArea);
		bottomRight.addView(IPageLayout.ID_OUTLINE);
		bottomRight.addView("org.caesarj.ui.views.CaesarHierarchyView"); //$NON-NLS-1$
		 
		
		IFolderLayout bottom =
			layout.createFolder(
				"bottom", //$NON-NLS-1$
				IPageLayout.BOTTOM,
				0.75f,
				editorArea);
		bottom.addView(IPageLayout.ID_TASK_LIST);
		bottom.addView(NewSearchUI.SEARCH_VIEW_ID);
		bottom.addView(JavaUI.ID_SOURCE_VIEW);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);	
		layout.addPerspectiveShortcut("org.caesarj.ui.actionsets.AnnotationShortCut"); //$NON-NLS-1$
		layout.setEditorAreaVisible(true);
		
		//Show Preferences
		CaesarConfigWizard wizard = new CaesarConfigWizard();
		wizard.init();
		if(CaesarJPreferences.isCAESARPrefConfigDone())
		{
			org.eclipse.jface.wizard.WizardDialog dialog = new org.eclipse.jface.wizard.WizardDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					wizard);
			dialog.open();
		}
	}

}
