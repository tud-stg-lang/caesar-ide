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
 * $Id: CaesarHierarchyView.java,v 1.37 2005-03-09 00:05:49 thiago Exp $
 */

package org.caesarj.ui.views;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.editor.CaesarEditor;
import org.caesarj.ui.resources.CaesarJPluginResources;
import org.caesarj.ui.util.ProjectProperties;
import org.caesarj.ui.views.hierarchymodel.HierarchyModelFactory;
import org.caesarj.ui.views.hierarchymodel.HierarchyNode;
import org.caesarj.ui.views.hierarchymodel.LinearNode;
import org.caesarj.ui.views.hierarchymodel.RootNode;
import org.caesarj.ui.views.hierarchymodel.StandardNode;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * Implements CaesarJ Hierarchy View
 * 
 * TODO - Change the image resources to the plugin resources
 * 
 * @author Jochen
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 * 
 */
public class CaesarHierarchyView extends ViewPart implements ISelectionListener {
	
	/**
	 * The image displayed for the subtype hierarchy
	 */
	protected static final Image SUBIMAGE = CaesarPluginImages.DESC_HIER_MODE_SUB.createImage();

	/**
	 * The image displayed for the supertype hierarchy
	 */
	protected static final Image SUPERIMAGE = CaesarPluginImages.DESC_HIER_MODE_SUPER.createImage();

	/**
	 * The singleton instance
	 */
	private static CaesarHierarchyView instance = null;

	/**
	 * The treeViewer is responsible for displaying the Caesarj Classes Hierarchy
	 */
	private static TreeViewer treeViewer = null;

	/**
	 * The listViewer is responsible for displaying the Caesarj Mixin Classes
	 */
	private static ListViewer listViewer = null;

	/**
	 * The logger object
	 */
	private static Logger log = Logger.getLogger(CaesarHierarchyView.class);
	
	/**
	 * Flags if the Hierarchy View is enabled or disabled
	 * 
	 */
	protected static boolean enabled = false;
	
	/**
	 * The ProjectProperties for the active hierarchy view. This object carries
	 * information on the project, the IProject object, etc.
	 */
	private ProjectProperties activeProjectProperties = null;
	
	/**
	 * The IJavaElement for the active hierarchy view.
	 */
	private IJavaElement activeJavaElement = null;
	
	/**
	 * The tool button on the toolbar from the view.
	 * When clicked, show the super or subtype hierarchy 
	 */
	private ToolItem toolButton = null;
	
	/**
	 * The filter button on the toolbar from the view.
	 * When clicked, toggle Filtering from implicit types
	 */
	private ToolItem filterButton = null;
	
	/**
	 * This flag is set when the hierarchy tree should not display the
	 * implicity classes.
	 */
	private boolean implicitFilter = false;
	
	/**
	 * This flag is set when the hierarchy tree is currently displaying
	 * the super type hierarchy and not the subtype. 
	 */
	private boolean superView = false;
	
	/**
	 * Constructor. Sets the singleton instance.
	 *
	 */
	public CaesarHierarchyView() {
		super();
		instance = this;
	}
	
	/**
	 * This method creates the widgets for the view. It is only called once by the Eclipse
	 * framework, when the plugin starts.
	 * 
	 * @param parent The parent control of this view
	 */
	public void createPartControl(Composite parent) {
		
		// Add this class as a selection listener for the page
		getSite().getPage().addSelectionListener(this);
		
		//ORGANIZE FOR ECLIPSE
		//getViewSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		
		// Create a sash form. The sash will have two elements. The top element
		// contains both the toolbar and the tree. The bottom element contains the
		// list viewer.
		SashForm sash = new SashForm(parent, SWT.VERTICAL | SWT.NULL);
		
		// Create the top composite to put the toolbar and tree
		Composite top = new Composite(sash, SWT.NULL);
		
		// Create the layout to place the widgets in the top composite
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);
		
		// Create the tool bar, with buttons for filter and super type hierarchy
		// Create the layout data, to fill a line
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.END;
		
		// Create the tool bar
		ToolBar toolbar = new ToolBar(top, SWT.FLAT);
		toolbar.setLayoutData(gridData);
		// Insert a vertical separator
		ToolItem seperator = new ToolItem(toolbar, SWT.SEPARATOR);
		
		// Create the filter button as a check button (for toggle)
		filterButton = new ToolItem(toolbar, SWT.CHECK);
		filterButton.setToolTipText(CaesarJPluginResources.getResourceString("HierarchyView.tooltip.filter")); //$NON-NLS-1$
		filterButton.setImage(CaesarPluginImages.DESC_OBJS_INNER_CCLASS_IMPLICID.createImage());
		filterButton.setDisabledImage(CaesarPluginImages.DESC_OBJS_INNER_CCLASS_IMPLICID_DISABLED.createImage());
		filterButton.setEnabled(false);

		// Insert another vertical separator in the toolbar
		ToolItem seperator2 = new ToolItem(toolbar, SWT.SEPARATOR);

		// Create the tool button, which selects the super or subtype hierarchy
		toolButton = new ToolItem(toolbar, SWT.FLAT);
		toolButton.setToolTipText(CaesarJPluginResources.getResourceString("HierarchyView.tooltip.showSuper")); //$NON-NLS-1$
		toolButton.setImage(SUPERIMAGE);
		toolButton.setEnabled(false);

		// Create the area to show the hierarchy tree
		// Create the layout to fill all space left
		gridData = new GridData(GridData.FILL_BOTH);
		
		// Create the tree
		Composite treeGroup = new Composite(top, SWT.SHADOW_IN);
		treeGroup.setLayoutData(gridData);
		// Inside the tree group, use a fill layout
		treeGroup.setLayout(new FillLayout());
		
		// Create the tree viewer
		treeViewer = new TreeViewer(treeGroup, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.HORIZONTAL);
		//for testing treeViewer.setInput(buildTestTreeModel());

		// Create the area to show the mixin view
		
		// Create the Title
		Group buttomGroup = new Group(sash, SWT.NONE);
		buttomGroup.setText(CaesarJPluginResources.getResourceString("HierarchyView.mixin.title"));
		
		// Inside the button group, use a fill layout
		buttomGroup.setLayout(new FillLayout());
		
		// Create the list viewer
		listViewer = new ListViewer(buttomGroup, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.HORIZONTAL);
		
		// Set the relative weights in the sashform. This will make the tree 2x bigger than the list
		sash.setWeights(new int[] { 2, 1});
		
		// Create providers for the widgets
		createProviders();
		
		// Create the listeners for the widgets
		createListeners();
		
		// Start the tree with everything opened
		treeViewer.expandAll();
		
		// Set this view as enabled and call refresh to set the initial state
		enabled=true;
		refresh();
	}

	/**
	 * Creates the providers both for the tree viewer and for the list viewer
	 */
	private void createProviders() {
		// Create the Label provider, that will be used on both
		CaesarJHierarchyLabelProvider labelProvider = new CaesarJHierarchyLabelProvider();
		
		// Set the providers for the tree viewer
		treeViewer.setContentProvider(new CaesarJHierarchyTreeContentProvider());
		treeViewer.setLabelProvider(labelProvider);
		
		// Set the providers for the list viewer
		listViewer.setContentProvider(new CaesarJHierarchyListContentProvider());
		listViewer.setLabelProvider(labelProvider);
	}
	
	/**
	 * Creates the listeners for the widgets.
	 * This method must be called before the first refresh.
	 */
	private void createListeners() {

		// Create the selection listener for the tree viewer
		treeViewer.addSelectionChangedListener(new CaesarJHierarchySelectionChangedListener());
		
		// Create the selection listener for the filter button
		filterButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Toggle the implicit filter
				implicitFilter = ! implicitFilter;
				
				// Refresh the view to reflect the new filter
				refresh();
			}
		});
		
		// Create the selection listener for the tool button
		toolButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Toggle the superView flag
				superView = ! superView;

				// Toggle the button
				if (superView) {
					toolButton.setToolTipText(CaesarJPluginResources.getResourceString("HierarchyView.tooltip.showSub")); //$NON-NLS-1$
					toolButton.setImage(SUBIMAGE);
				} else {
					toolButton.setToolTipText(CaesarJPluginResources.getResourceString("HierarchyView.tooltip.showSuper")); //$NON-NLS-1$
					toolButton.setImage(SUPERIMAGE);
				}
				
				// Refresh the view to reflect the new type
				refresh();
			}
		});
	}
	
	/**
	 * Implementation for the interface Selection Listener
	 * This method is called by the Eclipse framework always when the selection
	 * in the page containing the workbench changes.
	 * It refreshes the view.
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	
		// If there is a change in the caesar editor and we are enabled, refresh the view
		if ((part instanceof CaesarEditor) && enabled) {
			refresh(((CaesarEditor)part).getInputJavaElement());
		}
	}
	
	/**
	 * Implementation for the abstract class ViewPart.
	 * This method is called when the framework wants the view to get
	 * focus.
	 * It enables the view setting the enabled flag.
	 */
	public void setFocus() {
		enabled = true;
	}
	
	/**
	 * Implementation (optional) for the ViewPart class.
	 * This method is called when the view is going to be disposed.
	 * It disables the view and call the super method.
	 */
	public void dispose() {
		enabled = false;
		super.dispose();
	}
	
	/**
	 * Called by the Caesar Builder when the project was rebuilt.
	 * Causes the instance to be refreshed.
	 * 
	 * @param properties The project properties for the new built project
	 */
	public static void updateAll(ProjectProperties properties) {
		if (instance != null) {
			instance.refresh();
		} 
	}
	
	/**
	 * Refreshes the view using the current properties and java element.
	 * 
	 */
	public void refresh() {
		refresh(activeProjectProperties, activeJavaElement);
	}
	
	/**
	 * Refreshes the view using the current properties and this java element.
	 * This method sets this java element as the active java element.
	 * 
	 * @param file the java element to use in the refresh
	 */
	public void refresh(IJavaElement file) {
		try {
			// Set the active project properties for the editor
			activeProjectProperties = 
				ProjectProperties.create(file.getJavaProject().getProject());
			
			// Set the active java element
			activeJavaElement = file;
			
			// Call refresh
			refresh(activeProjectProperties, activeJavaElement);
			
		} catch (Exception e) {
			refresh();
		}
	}
	
	/**
	 * Refreshes the view.
	 * 
	 * Checks if the properties have an ASM. If not, just print messages in the view.
	 * 
	 * If yes, create new input objects for the treeviewer and list viewer. If error occurs,
	 * set the messages is the view.
	 * 
	 * @param properties
	 * @param file
	 */
	public void refresh(ProjectProperties properties, IJavaElement file) {
		// Enable the buttons
		filterButton.setEnabled(true);
		toolButton.setEnabled(true);
		
		// Check if we already have an ASM for the project. If we don't have,
		// Just call the update method with empty nodes (to show a message) and return
		if (properties == null || properties.getStructureModel() == null) {
			treeViewer.setInput(new RootNode(HierarchyNode.EMPTY));
			listViewer.setInput(new LinearNode(HierarchyNode.EMPTY));
			// Disable the buttons, since we don't have input
			filterButton.setEnabled(false);
			toolButton.setEnabled(false);
			return;
		}
		
		try {
			// Gets the absolute path for the project output directory
			String outputdir = properties.getProjectLocation() + properties.getOutputPath();
			
			// Gets the filename for the input element
			String filename = file.getResource().getName();
			
			// Try to get the class string, which is the packages + the file name
			String path = file.getResource().getProjectRelativePath().removeFileExtension().toOSString();
			IPackageFragmentRoot[] roots = file.getJavaProject().getAllPackageFragmentRoots();
			String clazz = null;
			for (int i = 0; i < roots.length; i++) {
				if (path.lastIndexOf(roots[i].getElementName()) != -1) {
					clazz = path.substring(roots[i].getElementName().length() + 1);
				}
			}
			
			// Create the input nodes with the factory
			RootNode rootNode = HierarchyModelFactory.createHierarchyTreeModel(
					properties.getStructureModel(), 
					filename, 
					outputdir, 
					implicitFilter, 
					superView);
			LinearNode linearNode = HierarchyModelFactory.createHierarchyListModel(
					clazz,
					outputdir);
			
			// If the root node is EMPTY or if it doesn't have children, disable the buttons
			if (rootNode.getKind().equals(HierarchyNode.EMPTY) ||
				rootNode.getChildren().length == 0) {
				// Disable the buttons, since we don't have input
				filterButton.setEnabled(false);
				toolButton.setEnabled(false);			
			}
			
			// Set the input for the viewers
			treeViewer.setInput(rootNode);
			listViewer.setInput(linearNode);
			
			// Show 5 levels on the treeViewer
			treeViewer.expandToLevel(5);
			
		} catch (Exception e) {
			// If we have an error, set the messages in the view
			treeViewer.setInput(new RootNode(HierarchyNode.EMPTY));
			listViewer.setInput(new LinearNode(HierarchyNode.EMPTY));
			// Disable the buttons, since we don't have input
			filterButton.setEnabled(false);
			toolButton.setEnabled(false);
		}	
	}
	
	/**
	 * Refreshes only the list, using the given node name from the tree
	 * 
	 * @param nodeName
	 */
	protected void refreshList(String nodeName) {
		listViewer.setInput(
				HierarchyModelFactory.createHierarchyListModel(nodeName, 
						activeProjectProperties.getProjectLocation() + 
						activeProjectProperties.getOutputPath()));
	}
	
	protected void refreshTree(String nodeName) {
		treeViewer.setInput(
				HierarchyModelFactory.createHierarchyTreeModel(
						activeProjectProperties.getStructureModel(), 
						nodeName, 
						activeProjectProperties.getProjectLocation() + activeProjectProperties.getOutputPath(), 
						implicitFilter, 
						superView));
	}
	/**
	 * 
	 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
	 *
	 */
	private class CaesarJHierarchySelectionChangedListener implements ISelectionChangedListener {
	
		/**
		 * Method called when the user selects a node in the tree viewer
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			// Get the selection made by the user
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			// Only do something if the element is a StandardNode from the HierarchyModel
			if (selection.getFirstElement() instanceof StandardNode) {
				// Get the node and make an action, based on its kind
				StandardNode markedNode = (StandardNode) selection.getFirstElement();
				
				if (markedNode.getKind().equals(HierarchyNode.SUPER)) {
					/** TODO: Add some functionality - navigation or further expansion */
				} else if (markedNode.getKind().equals(HierarchyNode.NESTED)) {

					if (markedNode.getTypeInformation().getNestedClasses().length > 0) {
						ArrayList list = new ArrayList();
						list.add(markedNode.getName());
						//refreshTree(list.toArray());
						refreshTree(markedNode.getName());
					}
					refreshList(markedNode.getName());
				} else 
					if (markedNode.getKind().equals(HierarchyNode.NESTEDSUPER) ||
						markedNode.getKind().equals(HierarchyNode.NESTEDSUB) ||
						markedNode.getKind().equals(HierarchyNode.CLASS)) {
					refreshList(markedNode.getName());
				}
			}
		}
	}
}