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
 
import org.apache.log4j.Logger;
import org.caesarj.ui.CaesarPlugin;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;

/**
 * Creates a perspective using views, perspective shortcuts and wizard shortcuts.
 */
public class CaesarPerspective implements IPerspectiveFactory {
	//public static final String ID_CAESAROUTLINE = "org.caesarj.ui.editor.CaesarOutlineView";
	//public static final String ID_EDUWIZARD =
		//"com.ibm.lab.soln.dialogs.wizard.Basic";
	//public static final String ID_EDU_JAVA_PERSPECTIVE =
		//"org.eclipse.jdt.ui.JavaHierarchyPerspective";
	private static Logger log = Logger.getLogger(CaesarPerspective.class);

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
			layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
		//topLeft.addView(IPageLayout.ID_RES_NAV);
		topLeft.addView(JavaUI.ID_PACKAGES);
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft =
			layout.createFolder(
				"bottomLeft",
				IPageLayout.BOTTOM,
				0.50f,
				"topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		bottomLeft.addView(IPageLayout.ID_PROP_SHEET);

		layout.setEditorAreaVisible(true);
		IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (w == null)
			return;
		
		
		IWorkbenchPage[] page = w.getPages();
		if (page == null)
			return;
		IViewPart viewPart = page[0].findView(JavaUI.ID_PACKAGES);
		if (viewPart==null)
			return;
		IPackagesViewPart packageView = (IPackagesViewPart)viewPart;
		log.debug("PackageView found: '"+packageView+"'!");
		//layout.addNewWizardShortcut(ID_EDUWIZARD);
		//layout.addPerspectiveShortcut(ID_EDU_JAVA_PERSPECTIVE);
	}

}
