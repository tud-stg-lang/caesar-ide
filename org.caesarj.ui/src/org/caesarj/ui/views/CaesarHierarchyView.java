package org.caesarj.ui.views;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.caesarj.compiler.export.CClass;
import org.caesarj.runtime.AdditionalCaesarTypeInformation;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.builder.Builder;
import org.caesarj.ui.editor.CaesarEditor;
import org.caesarj.ui.test.CaesarHierarchyTest;
import org.caesarj.ui.util.ProjectProperties;
import org.caesarj.ui.views.hierarchymodel.HierarchyNode;
import org.caesarj.ui.views.hierarchymodel.IHierarchyPropertyChangeListener;
import org.caesarj.ui.views.hierarchymodel.LinearNode;
import org.caesarj.ui.views.hierarchymodel.RootNode;
import org.caesarj.ui.views.hierarchymodel.StandardNode;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitDocumentProvider;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jochen
 *  
 */
public class CaesarHierarchyView extends ViewPart implements ISelectionListener {

	private static TreeViewer treeViewer;

	private static ListViewer listViewer;

	private String globalPathForInformationAdapter = new String("");

	private boolean superView = false;

	private static Logger log = Logger.getLogger(CaesarHierarchyView.class);

	private Button toolButton;

	protected static final String SUPER = "Super Mode", SUB = "Sub Mode";

	protected static final Image SUBIMAGE = CaesarPluginImages.DESC_HIER_MODE_SUB
			.createImage();

	protected static final Image SUPERIMAGE = CaesarPluginImages.DESC_HIER_MODE_SUPER
			.createImage();

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
			if (elementInArray(node.getName(), superClasses)) {
				StandardNode subNode = new StandardNode(
						HierarchyNode.NESTEDSUB, nestedClasses[j], node,
						helpInfo);
				if (helpInfo.isImplicit())
					subNode.setAdditionalName("Implicid");
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

	private boolean elementInArray(String element, String[] array) {
		boolean help = false;
		for (int i = 0; array.length > i; i++) {
			help = help | (element.compareTo(array[i]) == 0);
			//log.debug("Comparing '"+qn+"' and '"+listOfClasses[i]+"'");
		}
		return help;
	}

	private RootNode buildTreeModel(String path) {
		try {
			//TODO BUG: bei Zwei geöffneten Projekten wird nicht die Auswahl
			//			des aktuellen Projekts genommen, sondern das, was als
			//			letztes gebuildet wurde!!!
			ProjectProperties projectProperties = new ProjectProperties(Builder
					.getLastBuildTarget());
			log.debug(projectProperties.getProjectLocation()
					+ projectProperties.getOutputPath());
			globalPathForInformationAdapter = projectProperties
					.getProjectLocation()
					+ projectProperties.getOutputPath();
		} catch (Exception e) {

		}

		RootNode root = new RootNode();
		root.setKind(HierarchyNode.ROOT);
		StandardNode node;

		//log.debug(Builder.getLastBuildTarget().getRawLocation().toString());
		try {
			toolButton.setEnabled(true);
			CaesarHierarchyTest nav = new CaesarHierarchyTest(
					globalPathForInformationAdapter);
			CClass clazz = nav.load(path);
			AdditionalCaesarTypeInformation info = clazz
					.getAdditionalTypeInformation();
			StandardNode classNode = new StandardNode();
			classNode.setKind(HierarchyNode.CLASS);
			classNode.setName(info.getQualifiedName());
			classNode.setTypeInforamtion(info);
			classNode.setParent(root);
			root.addChild(classNode);
			String[] superClasses = info.getSuperClasses();
			StandardNode parentNode = new StandardNode();

			if (superClasses.length > 0)
				parentNode = new StandardNode(HierarchyNode.PARENTS, "Super",
						classNode);

			for (int i = 0; superClasses.length > i; i++) {
				node = new StandardNode();
				node.setKind(HierarchyNode.SUPER);
				node.setName(superClasses[i]);
				node.setParent(parentNode);
				parentNode.addChild(node);
			}

			String[] nestedClasses = info.getNestedClasses();

			StandardNode nestedClassesNode = new StandardNode();
			if (nestedClasses.length > 0)
				if (superView)
					nestedClassesNode = new StandardNode(
							HierarchyNode.NESTEDCLASSES,
							"Contains (Super Hierarchy)", classNode);
				else
					nestedClassesNode = new StandardNode(
							HierarchyNode.NESTEDCLASSES,
							"Contains (Sub Hierarchy)", classNode);

			//new Implemtation
			for (int i = 0; nestedClasses.length > i; i++) {
				clazz = nav.load(nestedClasses[i]);
				AdditionalCaesarTypeInformation nestedInfo = clazz
						.getAdditionalTypeInformation();
				node = new StandardNode();
				node.setTypeInforamtion(nestedInfo);
				node.setKind(HierarchyNode.NESTED);
				node.setName(nestedClasses[i]);
				if (nestedInfo.isImplicit())
					node.setAdditionalName("Implicid");
				node.setParent(nestedClassesNode);
				if (superView) //SuperView
				{
					node = findAllSuper(node);
				} else //SubView
				{
					node = findAllSub(nestedClasses, node);
				}
				nestedClassesNode.addChild(node);

			}

			return root;
		} catch (NullPointerException e) {
			log.debug("No Information.");
			StandardNode n1 = new StandardNode();
			n1.setKind(HierarchyNode.EMTY);
			n1.setName("No informations available.");
			toolButton.setEnabled(false);
			n1.setParent(root);
			root.addChild(n1);
			return root;
		} catch (Exception e) {
			log.warn("Building hierarchy tree.", e);
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
			emty.setName("No list available for selection");
			return emty;
		} catch (Exception e) {
			log.warn("Building List View.", e);
			return null;
		}
	}

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
		//Group layoutGroup = new Group(parent, SWT.NONE);
		//GridLayout top = new GridLayout(1,false);
		//GridData grid = new GridData(GridData.FILL_BOTH);
		//layoutGroup.setLayoutData(grid);
		//layoutGroup.setLayout(top);

		//CONTROLL
		Composite com = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		com.setLayoutData(gridData);
		GridLayout grid2 = new GridLayout(2, false);
		com.setLayout(grid2);
		GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
		Button NULLBUTTON = new Button(com, SWT.FLAT);
		NULLBUTTON.setVisible(false);
		NULLBUTTON.setLayoutData(gridData3);
		toolButton = new Button(com, SWT.FLAT);
		toolButton.setToolTipText(SUB);
		toolButton.setImage(SUBIMAGE);
		toolButton.setEnabled(true);
		toolButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				log.debug("Button Pressed!!!");
				setSuperView(!isSuperView());
				if (toolButton.getToolTipText().equals(SUPER)) {
					toolButton.setToolTipText(SUB);
					toolButton.setImage(SUBIMAGE);
				} else {
					toolButton.setToolTipText(SUPER);
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
		buttomGroup.setText("Mixin View");
		GridData gridDa2 = new GridData(GridData.FILL_HORIZONTAL);
		gridDa2.heightHint = 200;
		buttomGroup.setLayoutData(gridDa2);
		HierarchyListContentProvider lcp = new HierarchyListContentProvider();
		listViewer = new ListViewer(buttomGroup, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.HORIZONTAL);
		listViewer.setContentProvider(lcp);
		listViewer.setLabelProvider(lp);
		refreshTree("");
	}

	public void setFocus() {
	}

	public static TreeViewer getTreeViewer() {
		return treeViewer;
	}

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
				return element.getChildren();
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
					return CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC
							.createImage();
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
				return replaceAll(help, "$", ".");
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			if (selection.getFirstElement() instanceof StandardNode) {
				StandardNode markedNode = (StandardNode) selection
						.getFirstElement();
				if (markedNode.getKind().compareTo(HierarchyNode.SUPER) == 0) {
					log.debug("Super class '" + markedNode.getName()
							+ "' selected.");
					refreshTree(markedNode.getName());
					//refreshEditor(markedNode);
				} else if (markedNode.getKind().compareTo(HierarchyNode.NESTED) == 0) {
					log.debug("Nested class '" + markedNode.getName()
							+ "' selected.");
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
		if (part instanceof CaesarEditor) {
			CaesarEditor editor = (CaesarEditor) part;

			CompilationUnitDocumentProvider compilationUnitProvider = (CompilationUnitDocumentProvider) editor
					.getDocumentProvider();
			// TODO Hier habe ich schonmal geschaut, wie man an die
			// Toplevel-Klassen rankommen könnte.
			// 		War aber noch nicht erfolgreich!
			try {
				IJavaElement[] elements = compilationUnitProvider
						.getWorkingCopy(editor.getEditorInput()).getJavaModel()
						.getChildren();
				for (int i = 0; elements.length > i; i++) {
					log.debug("JAVAELEMENTS: " + elements[i].getElementName());
				}
			} catch (Exception e) {

			}
			if (editor.getEditorInput() instanceof FileEditorInput) {
				FileEditorInput input = (FileEditorInput) editor
						.getEditorInput();
				//TODO Ab hier die alte Lösung über den Filenamen

				path = input.getFile().getProjectRelativePath().toString();
				path = path.substring(0, path.indexOf(input.getFile()
						.getFileExtension()) - 1);

				log.debug("Selection in Editor! \n\tFile: '"
						+ editor.getEditorInput().getName()
						+ "'.\n\tQualified name: " + path + "'");

				refreshTree(path);
			}
		}
	}

	private String qualifiedNameToActualFile = "";

	public void refresh() {
		log.debug("Refresh!!!");
		refreshTree(qualifiedNameToActualFile);
		refreshList(qualifiedNameToActualFile);
	}

	public void refreshTree(String path) {
		log.debug("Refreshing Hierarchy Tree for '" + path + "'!");
		treeViewer.setInput(buildTreeModel(path));
		treeViewer.expandToLevel(3);
		qualifiedNameToActualFile = path;
		refreshList(path);
	}

	public void refreshList(String path) {
		log.debug("Refreshing Hierarchy List'" + path + "'!");
		listViewer.setInput(buildListModel(path));
	}

	public boolean isSuperView() {
		return superView;
	}

	public void setSuperView(boolean superView) {
		this.superView = superView;
	}
}