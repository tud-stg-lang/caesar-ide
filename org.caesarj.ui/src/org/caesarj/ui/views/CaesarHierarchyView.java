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
 * $Id: CaesarHierarchyView.java,v 1.36 2005-03-07 15:47:01 aracic Exp $
 */

package org.caesarj.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.aspectj.asm.StructureNode;
import org.caesarj.compiler.asm.CaesarProgramElementNode;
import org.caesarj.compiler.export.CClass;
import org.caesarj.runtime.AdditionalCaesarTypeInformation;
import org.caesarj.ui.CaesarElementImageDescriptor;
import org.caesarj.ui.CaesarPlugin;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.editor.CaesarEditor;
import org.caesarj.ui.editor.CaesarJContentOutlinePage;
import org.caesarj.ui.resources.CaesarJPluginResources;
import org.caesarj.ui.test.CaesarHierarchyTest;
import org.caesarj.ui.util.ProjectProperties;
import org.caesarj.ui.views.hierarchymodel.HierarchyNode;
import org.caesarj.ui.views.hierarchymodel.IHierarchyPropertyChangeListener;
import org.caesarj.ui.views.hierarchymodel.LinearNode;
import org.caesarj.ui.views.hierarchymodel.RootNode;
import org.caesarj.ui.views.hierarchymodel.StandardNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

/**
 * Implements CaesarJ Hierarchy View
 * 
 * TODO - Total refactor the class
 * TODO - Change the image resources to the plugin resources
 * 
 * @author Jochen
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 * 
 */
public class CaesarHierarchyView extends ViewPart implements ISelectionListener {

	protected static final Image SUBIMAGE = CaesarPluginImages.DESC_HIER_MODE_SUB
		.createImage();

	protected static final Image SUPERIMAGE = CaesarPluginImages.DESC_HIER_MODE_SUPER
		.createImage();

	/**
	 * The singleton instance
	 */
	private static CaesarHierarchyView instance = null;

	/**
	 * The treeViewer is responsible for displaying the Caesar Classes Hierarchy
	 */
	private static TreeViewer treeViewer = null;

	/**
	 * The listViewer is responsible for displaying the Caesar Mixin Classes
	 */
	private static ListViewer listViewer = null;

	/**
	 * The logger object
	 */
	private static Logger log = Logger.getLogger(CaesarHierarchyView.class);
	
	/**
	 * Flags if the Hierarchy View is enabled or disabled
	 * 
	 * TODO - CHECK IF IT IS NECESSARY TO HAVE THIS FLAG
	 */
	protected static boolean enabled = false;
	
	/**
	 * The ProjectProperties for the active hierarchy view. This object carries
	 * information on the project, the IProject object, etc.
	 */
	private ProjectProperties activeProjectProperties = null;
	
	private String globalPathForInformationAdapter = new String("");

	private boolean superView = false;

	private ToolItem toolButton, filterButton;

	private boolean implicitFilter = false;

	private Object[] qualifiedNameToActualClasses = null;

	/**
	 * Constructor. Sets the singleton instance.
	 *
	 */
	public CaesarHierarchyView() {
		super();
		instance = this;
	}
	
	private String filterName(String name) {
		try {
			String help = new String(name);
			int slashPos = name.lastIndexOf("/");
			int dollarPos = name.lastIndexOf("$");
			if (slashPos > 0)
				help = name.substring(slashPos + 1);
			if (dollarPos > slashPos) {
				help = name.substring(dollarPos + 1);
			}
			return help;
		} catch (Exception e) {
			log.warn("Filtering names for Hierarchy View.", e);
			return "Error";
		}
	}
	
	private boolean isNodeInSuperlist(String qualifiedNameOfNode, String[] nestedClasses, CaesarHierarchyTest nav) throws Exception
	{
		AdditionalCaesarTypeInformation info;
		CClass clazz;
		String[] help;
		boolean result = false;
		
		for (int i = 0; nestedClasses.length>i ; i++)
		{
			clazz = nav.load(nestedClasses[i]);
			info = clazz.getAdditionalTypeInformation();
			help = info.getSuperClasses();
			result = result | isElementInArray(qualifiedNameOfNode, help);
		}
		
		return result;
	}
	
	private boolean isNodeInSubList(String qualifiedNameOfNode, String[] nestedClasses, CaesarHierarchyTest nav) throws Exception
	{
		AdditionalCaesarTypeInformation info;
		CClass clazz;
		String[] help;
		boolean result = false;
		
		for (int i = 0; nestedClasses.length>i ; i++)
		{
			clazz = nav.load(nestedClasses[i]);
			info = clazz.getAdditionalTypeInformation();
			help = info.getSuperClasses();
			result = result | isElementInArray(qualifiedNameOfNode, help);
		}
		
		return result;
	}

	private StandardNode findAllSub(String[] nestedClasses, StandardNode node) {
		CaesarHierarchyTest nav = new CaesarHierarchyTest(
				globalPathForInformationAdapter);
		CClass clazz;
		AdditionalCaesarTypeInformation helpInfo;
		String[] superClasses;
		String additionalInfo = "";

		for (int j = 0; nestedClasses.length > j; j++) {
			clazz = nav.load(nestedClasses[j]);
			helpInfo = clazz.getAdditionalTypeInformation();
			superClasses = helpInfo.getSuperClasses();
			if (isElementInArray(node.getName(), superClasses)) {
				StandardNode subNode = new StandardNode(
						HierarchyNode.NESTEDSUB, nestedClasses[j], node,
						helpInfo);
				subNode = checkFurtherBinding(subNode);
				for (int k = 0; superClasses.length > k; k++) {
					if (k == 0)
						additionalInfo = filterName(superClasses[k]);
					else
						additionalInfo = additionalInfo + " & "
								+ filterName(superClasses[k]);
				}
				if (superClasses.length > 1)
					subNode.setAdditionalName(additionalInfo);
				subNode = findAllSub(nestedClasses, subNode);
			}
		}
		return node;
	}

	private StandardNode findAllSuper(StandardNode classNode) {
		try {
			if (classNode == null)
				throw new Exception("Node is null!");
			if (classNode.getTypeInforamtion() == null)
				throw new Exception("Node had no Type Information!");
			AdditionalCaesarTypeInformation info = classNode
					.getTypeInforamtion();
			log.debug("Information of " + classNode.getName() + "\n" + info);

			String[] superNodes = info.getSuperClasses();
			for (int i = 0; superNodes.length > i; i++) {
				StandardNode help = new StandardNode();
				CaesarHierarchyTest nav = new CaesarHierarchyTest(
						globalPathForInformationAdapter);
				CClass clazz = nav.load(superNodes[i]);
				help.setTypeInforamtion(clazz.getAdditionalTypeInformation());
				help.setName(superNodes[i]);
				help.setParent(classNode);
				help = checkFurtherBinding(help);
				help.setKind(StandardNode.NESTEDSUPER);
				classNode.addChild(help);
				help = findAllSuper(help);
			}
			return classNode;

		} catch (Exception e) {
			log.warn("Finding all Super classes", e);
			return null;
		}

	}

	private boolean isElementInArray(String element, String[] array) {
		boolean help = false;
		for (int i = 0; array.length > i; i++) {
			help = help | (element.compareTo(array[i]) == 0);
		}
		return help;
	}
	
	private StandardNode checkFurtherBinding(StandardNode node)
	{
		String [] increment = node.getTypeInforamtion().getIncrementFor();
		boolean isImplicid = node.getTypeInforamtion().isImplicit();
		
		node.setFurtherBinding(!isImplicid&&!(increment.length==0));
		return node;
	}

	private RootNode buildTreeModel(Object[] path) {

		RootNode root = new RootNode();
		root.setKind(HierarchyNode.ROOT);
		StandardNode node;

		try {
			try {
				if (path.length==0) 
					throw new NullPointerException("No Data.");
				for (int k = 0; path.length > k; k++) {
					toolButton.setEnabled(true);
					filterButton.setEnabled(true);
					CaesarHierarchyTest nav = new CaesarHierarchyTest(
							globalPathForInformationAdapter);
					CClass clazz = nav.load((String) path[k]);
					AdditionalCaesarTypeInformation info = clazz
							.getAdditionalTypeInformation();
					StandardNode classNode = new StandardNode();
					classNode.setKind(HierarchyNode.CLASS);
					classNode.setName(info.getQualifiedName());
					classNode.setTypeInforamtion(info);
					classNode.setParent(root);
					classNode = checkFurtherBinding(classNode);
					root.addChild(classNode);
					String[] superClasses = info.getSuperClasses();
					StandardNode parentNode = new StandardNode();
	
					if (superClasses.length > 0)
						parentNode = new StandardNode(HierarchyNode.PARENTS,
								"Super", classNode);
	
					for (int i = 0; superClasses.length > i; i++) {
						node = new StandardNode();
						node.setKind(HierarchyNode.SUPER);
						node.setName(superClasses[i]);
						node.setParent(parentNode);
						node.setTypeInforamtion(nav.load(superClasses[i]).getAdditionalTypeInformation());
						parentNode.addChild(node);
					}
	
					String[] nestedClasses = info.getNestedClasses();
	
					StandardNode nestedClassesNode = new StandardNode();
					if (nestedClasses.length > 0)
						if (superView)
							nestedClassesNode = new StandardNode(
									HierarchyNode.NESTEDCLASSES,
									"Contains (Super)", classNode);
						else
							nestedClassesNode = new StandardNode(
									HierarchyNode.NESTEDCLASSES,
									"Contains (Sub)", classNode);
	
					for (int i = 0; nestedClasses.length > i; i++) 
					{
						clazz = nav.load(nestedClasses[i]);
						AdditionalCaesarTypeInformation nestedInfo = clazz
								.getAdditionalTypeInformation();
						
					
						node = new StandardNode();
						node.setTypeInforamtion(nestedInfo);
						node.setKind(HierarchyNode.NESTED);
						node.setName(nestedClasses[i]);
						node = checkFurtherBinding(node);
						if (superView) //SuperView
						{
							node = findAllSuper(node);
						} else //SubView
						{
							node = findAllSub(nestedClasses, node);
						}
						if ((!isNodeInSuperlist(nestedClasses[i], nestedClasses, nav) && superView)
								|(/*!isNodeInSubList(nestedClasses[i], nestedClasses, nav)&&*/!superView))
						{
							nestedClassesNode.addChild(node);
							node.setParent(nestedClassesNode);
						}		
					}
					if (!superView)
					{
						Object[] helpNestedClasses = nestedClassesNode.getChildren();
						for (int i = 0; helpNestedClasses.length>i; i++)
						{
			
							for (int j = 0; helpNestedClasses.length>j; j++)
							{
								if ((i!=j)&&((StandardNode)helpNestedClasses[j]).hasSubNode(((StandardNode)helpNestedClasses[i])))
								{
									((StandardNode)helpNestedClasses[i]).setParent(null);
									nestedClassesNode.removeChild((StandardNode)helpNestedClasses[i]);
								}
							}
						}
					}
				}
				return root;
			} catch (NullPointerException e) {
				log.error("buildTree Nullpointer Exception");
				log.debug("No Information.");
				StandardNode n1 = new StandardNode();
				n1.setKind(HierarchyNode.EMTY);
				n1.setName(CaesarJPluginResources.getResourceString("HierarchyView.hierarchy.noInformationAvailable"));
				toolButton.setEnabled(false);
				filterButton.setEnabled(false);
				n1.setParent(root);
				root.addChild(n1);
				return root;
			} 
		} catch (Exception e) {
			log.warn("Building hierarchy tree.");
			return root;
		}

	}

	private LinearNode buildListModel(String path) {
		try {
			if (path == null)
				throw (new NullPointerException("no information"));
			CaesarHierarchyTest nav = new CaesarHierarchyTest(
					globalPathForInformationAdapter);
			CClass clazz = nav.load(path);
			AdditionalCaesarTypeInformation info = clazz
					.getAdditionalTypeInformation();
			LinearNode node1 = new LinearNode();
			LinearNode node2 = new LinearNode();
			String[] list = info.getMixinList();
			if (list.length > 0) {
				for (int i = 0; list.length > i; i++) {
					if (i == 0) {
						node1 = new LinearNode();
						node1.setKind(HierarchyNode.LIST);
						node1.setName(list[i]);
					} else {
						node2 = node1;
						node1 = new LinearNode();
						node2.setPreNode(node1);
						node1.setNextNode(node2);
						node1.setKind(HierarchyNode.LIST);
						node1.setName(list[i]);
					}
				}
				return node1;
			} else
				throw (new NullPointerException("no information"));
		} catch (NullPointerException e) {
			log.debug("No Information to show on hierarchy tree.");
			LinearNode emty = new LinearNode();
			emty.setKind(HierarchyNode.LIST);
			emty.setName(CaesarJPluginResources.getResourceString("HierarchyView.hierarchy.noListAvailable"));
			return emty;
		} catch (Exception e) {
			log.warn("Building List View.", e);
			return null;
		}
	}

	/**
	 * This method creates the widgets for the view. It is only called once by the Eclipse
	 * framework, when the plugin starts.
	 * 
	 * @param parent The parent control of this view
	 */
	public void createPartControl(Composite parent) {
		//ORGANIZE FOR ECLIPSE
		getViewSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);

		//INIT
		GridLayout top = new GridLayout(1, false);
		top.horizontalSpacing = 0;
		top.verticalSpacing = 0;
		top.marginHeight = 0;
		top.marginWidth = 0;
		parent.setLayout(top);

		//CONTROLL
		ToolBar toolbar = new ToolBar(parent, SWT.FLAT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.END;
		toolbar.setLayoutData(gridData);
		ToolItem seperator = new ToolItem(toolbar, SWT.SEPARATOR);
		filterButton = new ToolItem(toolbar, SWT.CHECK);
		filterButton.setToolTipText(CaesarJPluginResources.getResourceString("HierarchyView.tooltip.filter")); //$NON-NLS-1$
		filterButton
				.setImage(CaesarPluginImages.DESC_OBJS_INNER_CCLASS_IMPLICID
						.createImage());
		filterButton
		.setDisabledImage(CaesarPluginImages.DESC_OBJS_INNER_CCLASS_IMPLICID_DISABLED
				.createImage());
		filterButton.setEnabled(false);
		filterButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (implicitFilter) {
					implicitFilter = false;
				} else {
					implicitFilter = true;
				}
				refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		ToolItem seperator2 = new ToolItem(toolbar, SWT.SEPARATOR);
		toolButton = new ToolItem(toolbar, SWT.FLAT);
		toolButton.setToolTipText(CaesarJPluginResources.getResourceString("HierarchyView.tooltip.super")); //$NON-NLS-1$
		toolButton.setImage(SUPERIMAGE);
		toolButton.setEnabled(false);
		toolButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				log.debug("Button Pressed!!!");
				setSuperView(!isSuperView());
				if (toolButton.getToolTipText().equals(CaesarJPluginResources.getResourceString("HierarchyView.tooltip.super"))) { //$NON-NLS-1$
					toolButton.setToolTipText(CaesarJPluginResources.getResourceString("HierarchyView.tooltip.sub")); //$NON-NLS-1$
					toolButton.setImage(SUBIMAGE);
				} else {
					toolButton.setToolTipText(CaesarJPluginResources.getResourceString("HierarchyView.tooltip.super")); //$NON-NLS-1$
					toolButton.setImage(SUPERIMAGE);
				}
				refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		//TREEVIEW
		Composite treeGroup = new Composite(parent, SWT.SHADOW_IN);
		treeGroup.setLayout(new FillLayout());
		GridData gridData2 = new GridData(GridData.FILL_BOTH);
		treeGroup.setLayoutData(gridData2);
		treeViewer = new TreeViewer(treeGroup, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.HORIZONTAL);
		HierarchyTreeContentProvider cp = new HierarchyTreeContentProvider();
		HierarchySelectionChangedListener scl = new HierarchySelectionChangedListener();
		HierarchyLabelProvider lp = new HierarchyLabelProvider();
		treeViewer.setContentProvider(cp);
		treeViewer.setLabelProvider(lp);
		treeViewer.addSelectionChangedListener(scl);
		treeViewer.expandAll();
		//for testing treeViewer.setInput(buildTestTreeModel());

		//LINEVIEW
		Group buttomGroup = new Group(parent, SWT.NONE);
		buttomGroup.setLayout(new FillLayout());
		buttomGroup.setText(CaesarJPluginResources.getResourceString("HierarchyView.mixin.title"));
		GridData gridDa2 = new GridData(GridData.FILL_HORIZONTAL);
		gridDa2.heightHint = 200;
		buttomGroup.setLayoutData(gridDa2);
		HierarchyListContentProvider lcp = new HierarchyListContentProvider();
		listViewer = new ListViewer(buttomGroup, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.HORIZONTAL);
		listViewer.setContentProvider(lcp);
		listViewer.setLabelProvider(lp);
		enabled=true;
		//refreshTree(new String[] { "" });
		refresh();
	}

	public void setFocus() {
		enabled = true;
	}
	

	public void dispose() {
		enabled = false;
		super.dispose();
	}
	
	public static TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * The content provider for the mixin view list
	 * 
	 */
	protected class HierarchyListContentProvider implements
			IStructuredContentProvider {

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		public Object[] getElements(Object listElement) {
			Vector vec = new Vector();
			if (listElement instanceof LinearNode) {
				LinearNode element = (LinearNode) listElement;
				vec = element.getAll();
			}
			return vec.toArray();
		}

	}

	/**
	 * The content provider for the hierarchy tree
	 * 
	 */
	protected class HierarchyTreeContentProvider implements
			ITreeContentProvider, IHierarchyPropertyChangeListener {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			try {
				if (oldInput == null && newInput != null) {
					RootNode input = (RootNode) newInput;
					input.addPropertyChangeListener("Content provider", this);
				} else if (oldInput != null && newInput == null) {
					RootNode input = (RootNode) newInput;
					input.removePropertyListener("Content provider", this);
				}

			} catch (Exception e) {

			}
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public Object[] getChildren(Object parentElement) {
			Vector vec = new Vector();
			if (parentElement instanceof HierarchyNode) {
				HierarchyNode element = (HierarchyNode) parentElement;
				if (implicitFilter) {
					Object all[] = element.getChildren();
					for (int i = 0; i < all.length; i++) {
						AdditionalCaesarTypeInformation typInfo = ((HierarchyNode) all[i])
								.getTypeInforamtion();
						if (typInfo == null || !typInfo.isImplicit()) {
							vec.add(all[i]);
						}
					}
					return vec.toArray();
				} else {
					return element.getChildren();
				}
			}
			return vec.toArray();
		}

		public Object getParent(Object element) {
			if (element instanceof HierarchyNode) {
				HierarchyNode node = (HierarchyNode) element;
				return node.getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof HierarchyNode) {
				HierarchyNode node = (HierarchyNode) element;
				return node.getChildren() != null;
			}
			return false;
		}

		public void propertyChange(PropertyChangeEvent event) {

		}
	}

	/**
	 * The label provider for the hierarchy tree
	 */
	protected class HierarchyLabelProvider extends LabelProvider {

		public Image getImage(Object element) {
			if (element instanceof RootNode) {
				RootNode node = (RootNode) element;
				if (0 == node.getKind().compareTo(HierarchyNode.CLASS)
						| 0 == node.getKind().compareTo(HierarchyNode.SUPER)
						| 0 == node.getKind().compareTo(HierarchyNode.NESTED)
						| 0 == node.getKind().compareTo(
								HierarchyNode.NESTEDSUPER)
						| 0 == node.getKind()
								.compareTo(HierarchyNode.NESTEDSUB))
					try {
						if (node.getTypeInforamtion().isImplicit())
							return new CaesarElementImageDescriptor(CaesarPluginImages.DESC_OBJS_INNER_CCLASS_IMPLICID, CaesarJContentOutlinePage.BIG_SIZE, node).createImage();
						else
							return new CaesarElementImageDescriptor(CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC, CaesarJContentOutlinePage.BIG_SIZE, node).createImage();							
					} catch (NullPointerException e) {
						//For nodes, which do not have any typ information
						return new CaesarElementImageDescriptor(CaesarPluginImages.DESC_OBJS_INNER_CCLASS_IMPLICID, CaesarJContentOutlinePage.BIG_SIZE, node).createImage();
					}

				else if (0 == node.getKind().compareTo(HierarchyNode.PARENTS)
						| 0 == node.getKind().compareTo(
								HierarchyNode.NESTEDCLASSES))
					return JavaPluginImages.DESC_OBJS_IMPCONT.createImage();
				else
					return super.getImage(element);
			} else
				return null;
		}

		public String getText(Object element) {
			String help;
			if (element instanceof RootNode) {
				RootNode node = (RootNode) element;
				//help = node.getName();
				help = filterName(node.getName());
				if (node.hasAdditionalName()) {
					help = help + " (" + node.getAdditionalName() + ")";					
				}
				help = replaceAll(help, "$", ".");
				/*if (node.isFurtherBinding())
				{
					help = help + "(F)";
				}*/
				return help;
			} else if (element instanceof LinearNode) {
				LinearNode node = (LinearNode) element;
				return replaceAll(node.getName(), "$", ".");
			} else {
				return "unknown object";
			}
		}

		private String replaceAll(String source, String orig_val, String new_val) {
			try {
				String help = source;
				for (; help.lastIndexOf(orig_val) > 0;) {
					help = help.substring(0, help.lastIndexOf(orig_val))
							+ new_val
							+ help.substring(help.lastIndexOf(orig_val) + 1);
				}
				return help;
			} catch (Exception e) {
				log.warn("Replacing '" + orig_val + "' with '" + new_val
						+ "' in '" + source + "'.", e);
				return source;
			}
		}

	}

	private class HierarchySelectionChangedListener implements
			ISelectionChangedListener {

		public HierarchySelectionChangedListener() {

		}

		private IFile getLinkLocation(String fullQualifiedName) {
			String fullPath = globalPathForInformationAdapter + "/"
					+ fullQualifiedName + ".java";
			IFile iFile = (IFile) ProjectProperties.findResource(fullPath,
					activeProjectProperties.getProject());
			return iFile;
		}

		private IWorkbenchPage getActivePage() {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage();
		}

		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			if (selection.getFirstElement() instanceof StandardNode) {
				StandardNode markedNode = (StandardNode) selection
						.getFirstElement();
				if (markedNode.getKind().compareTo(HierarchyNode.SUPER) == 0) {
					/** TODO: Add some functionality - navigation or further expansion */
				} 
				else if (markedNode.getKind().compareTo(HierarchyNode.NESTED) == 0) {
					log.debug("Nested class '" + markedNode.getName()
							+ "' selected.");
					if (markedNode.getTypeInforamtion().getNestedClasses().length>0)
					{
						ArrayList list = new ArrayList();
						list.add(markedNode.getName());
						refreshTree(list.toArray());
						//actualFileHelp = "-";
					}
					refreshList(markedNode.getName());
				} else if (markedNode.getKind().compareTo(
						HierarchyNode.NESTEDSUPER) == 0) {
					log.debug("Nested Super class '" + markedNode.getName()
							+ "' selected.");
					refreshList(markedNode.getName());
				} else if (markedNode.getKind().compareTo(
						HierarchyNode.NESTEDSUB) == 0) {
					log.debug("Nested Sub class '" + markedNode.getName()
							+ "' selected.");
					refreshList(markedNode.getName());
				} else if (markedNode.getKind().compareTo(HierarchyNode.CLASS) == 0) {
					log.debug("Main Class '" + markedNode.getName()
							+ "' selected.");
					refreshList(markedNode.getName());
				} else {
					log.debug("Not interested in selection of '"
							+ markedNode.getName() + "'.");
					refreshList(null);
				}
			}
		}
	}

	public static ListViewer getListViewer() {
		return listViewer;
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
		String path;
		StructureNode node = findJavaRootElement();
		int selectionLength = 0;
		
		if (selection instanceof TextSelection) {
			TextSelection textSelection = (TextSelection) selection;
			selectionLength = textSelection.getLength();	
		}

		if ((part instanceof CaesarEditor) && node != null && enabled) {
			CaesarEditor e = (CaesarEditor) part;
			refresh();
		}		
	}
	
	protected StructureNode getInput(StructureNode node, FileEditorInput input)
			throws Exception {
		if (node == null) {
			return null;
		}
		StructureNode r = null;
		if (node.getName().equals(input.getName())) {
			r = node;
		} else {
			StructureNode res = null;
			for (Iterator it = node.getChildren().iterator(); it.hasNext()
					&& res == null;) {
				res = getInput((StructureNode) it.next(), input);
			}
			r = res;
		}
		return r;
	}



	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private StructureNode findJavaRootElement() {
	    try {
			CaesarEditor editor = (CaesarEditor) CaesarPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
	
			FileEditorInput input = (FileEditorInput) editor.getEditorInput();
			
			activeProjectProperties = 
				ProjectProperties.create(editor.getInputJavaElement().getJavaProject().getProject());
				
			return getInput(activeProjectProperties.getStructureModel().getRoot(), input);
		} catch (Exception e) {
			return null;
		}
	}
	
	/*public void refresh() {
		log.debug("Refresh!!!");
		ArrayList listOfFullQualifiedNames = new ArrayList();
		try {			
			List listOfToplevelClasses = findJavaRootElement().getChildren();			
			
			Object[] arrayOfFullQualifiedNames;
			String help = "";
			log.debug("Iterating over toplevel classes.");
			ListIterator iter = listOfToplevelClasses.listIterator();
			for (int i = 0; iter.hasNext(); i++) {
				//CClassNode topLevelClass = (CClassNode) iter.next();
				CaesarProgramElementNode topLevelClass = (CaesarProgramElementNode)iter.next();
				help = topLevelClass.getName();
				log.debug("toplevelclass no." + i + " getName() = " + help);
				if(help.indexOf("_Impl") != -1){
					help = help.substring(0, help.indexOf("_Impl"));
					log.debug("Remove suffix _Impl from toplevel classname.");
				}
				listOfFullQualifiedNames.add(topLevelClass.getPackageName()
						+ "/" + help);
			}
			qualifiedNameToActualClasses = listOfFullQualifiedNames.toArray();			
			ProjectProperties prob = new ProjectProperties(ACTIVE_PROJECT);
			globalPathForInformationAdapter = prob.getProjectLocation()+prob.getOutputPath();
			refreshTree(qualifiedNameToActualClasses);
		} catch (Exception e) {
			log.error("Exception in refresh()!", e);
			refreshTree(listOfFullQualifiedNames.toArray());
		}
	}*/
	
	public void refresh(){
		log.debug("Starting rebuilding of HierarchyView.....");
		try{
			List fullQualifiedNames = new ArrayList();
			log.debug("Iterating over toplevel elements.");
			Iterator it = findJavaRootElement().getChildren().iterator();
			while(it.hasNext()){
				Object next = it.next();
				if(next instanceof CaesarProgramElementNode){
					if(((CaesarProgramElementNode)next).getCaesarKind() == CaesarProgramElementNode.Kind.VIRTUAL_CLASS
							|| ((CaesarProgramElementNode)next).getCaesarKind() == CaesarProgramElementNode.Kind.ASPECT){
						String classname = ((CaesarProgramElementNode)next).getName();
						if(classname.indexOf("_Impl") != -1){
							classname = classname.substring(0, classname.indexOf("_Impl"));
							log.debug("Remove suffix _Impl from toplevel classname.");
						}
						log.debug("Adding fullqualified class name: " + ((CaesarProgramElementNode)next).getPackageName() + "/" + classname);
						fullQualifiedNames.add(((CaesarProgramElementNode)next).getPackageName() + "/" + classname);
					}else{
						log.debug("element is not a virtual class and will be ignored.");
					}
				}else{
					log.debug("element is not a CeasarProgramElementNode, it will be ignored.");
				}
			}
			qualifiedNameToActualClasses = fullQualifiedNames.toArray();
			ProjectProperties prob = ProjectProperties.create(activeProjectProperties.getProject());
			globalPathForInformationAdapter = prob.getProjectLocation()+prob.getOutputPath();
			refreshTree(qualifiedNameToActualClasses);
		}catch(Exception e){
			log.error("An Exception occured during rebuild.", e);
			refreshTree(new ArrayList().toArray());
		}
	}

	public void refreshTree(Object[] path) {
		log.debug("Refreshing Hierarchy Tree!");
		treeViewer.setInput(buildTreeModel(path));
		treeViewer.expandToLevel(4);
		qualifiedNameToActualClasses = path;
		if (path.length>0)
			refreshList((String)path[0]);
	}

	public void refreshList(String path) {
		log.debug("Refreshing Hierarchy List for '" + path + "'!");
		listViewer.setInput(buildListModel(path));
	}

	public boolean isSuperView() {
		return superView;
	}

	public void setSuperView(boolean superView) {
		this.superView = superView;
	}
	
	/**
	 * Called by the Caesar Builder when everything was rebuilt.
	 * Causes the instance to be refreshed.
	 * 
	 * @param properties The project properties for the new built project
	 */
	public static void updateAll(ProjectProperties properties) {
		if (instance != null) {
			instance.refresh();
		} 
	}
}