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
import org.eclipse.jdt.internal.ui.JavaPluginImages;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
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
public class CaesarHierarchyView extends ViewPart implements ISelectionListener{

	private static TreeViewer treeViewer;
	private static ListViewer listViewer;
	private String globalPathForInformationAdapter = new String("");
	
	private static Logger log = Logger.getLogger(CaesarHierarchyView.class);
	
	
	private RootNode buildTreeModel(String path)
	{
		try {
			//TODO BUG: bei Zwei geöffneten Projekten wird nicht die Auswahl 
			//			des aktuellen Projekts genommen, sondern das, was als
			//			letztes gebuildet wurde!!!
			ProjectProperties projectProperties = new ProjectProperties(Builder.getLastBuildTarget());
			log.debug(projectProperties.getProjectLocation()+projectProperties.getOutputPath());
			globalPathForInformationAdapter = projectProperties.getProjectLocation()+projectProperties.getOutputPath();
		} catch(Exception e)
		{
		
		}
		//TODO Herausfinden, welchen Knoten noch die AdditionalInfos mitgegeben werden müssen.
		RootNode root = new RootNode();
		root.setKind(HierarchyNode.ROOT);
		StandardNode node;
		
		//log.debug(Builder.getLastBuildTarget().getRawLocation().toString());
		try {
			CaesarHierarchyTest nav = new CaesarHierarchyTest(globalPathForInformationAdapter);
	        CClass clazz = nav.load(path);
			AdditionalCaesarTypeInformation info = clazz.getAdditionalTypeInformation();
			StandardNode classNode = new StandardNode();
			classNode.setKind(HierarchyNode.CLASS);
			classNode.setName(info.getQualifiedName());
			classNode.setTypeInforamtion(info);
			classNode.setParent(root);
			root.addChild(classNode);
			StandardNode parentNode = new StandardNode(HierarchyNode.PARENTS, "Super", classNode);
			classNode.addChild(parentNode);			
			
			String [] superClasses = info.getSuperClasses();
			for (int i=0; superClasses.length>i; i++)
			{
				node = new StandardNode();
				node.setKind(HierarchyNode.SUPER);
				node.setName(superClasses[i]);
				node.setParent(parentNode);
				parentNode.addChild(node);
			}
			
			String [] nestedClasses = info.getNestedClasses();
			for (int i=0; nestedClasses.length>i; i++)
			{
				node = new StandardNode();
				node.setKind(HierarchyNode.NESTED);
				node.setName(nestedClasses[i]);
				node.setParent(classNode);
				classNode.addChild(node);
				String[] nestedIncrementClasses = nav.load(nestedClasses[i]).getAdditionalTypeInformation().getIncrementFor();
				String[] nestedSuperClasses = nav.load(nestedClasses[i]).getAdditionalTypeInformation().getSuperClasses();
				if (nav.load(nestedClasses[i]).getAdditionalTypeInformation().isImplicit())
				{
					node.setAdditionalName(nestedIncrementClasses[0]);
				}
				if (nestedIncrementClasses.length>0)
				{
					StandardNode subNode = new StandardNode(HierarchyNode.INCREMENTCLASSES, "Increment for", node);
					node.addChild(subNode);
					for (int j=0; nestedIncrementClasses.length>j; j++)
					{
						node = new StandardNode();
						node.setKind(HierarchyNode.NESTEDSUPER);
						node.setName(nestedIncrementClasses[j]);
						node.setParent(subNode);
						subNode.addChild(node);
					}
				}
				if (nestedSuperClasses.length>0)
				{
					StandardNode subNode = new StandardNode(HierarchyNode.NESTEDSUPER, "Super", node);
					node.addChild(subNode);
					for (int j=0; nestedIncrementClasses.length>j; j++)
					{
						node = new StandardNode();
						node.setKind(HierarchyNode.NESTEDSUPER);
						node.setName(nestedSuperClasses[j]);
						node.setParent(subNode);
						subNode.addChild(node);
					}
				}
			}
			
			return root;
		} 
		  catch (NullPointerException e) {
		  	log.debug("No Information.");
		  	StandardNode n1 = new StandardNode();
		  	n1.setKind(HierarchyNode.EMTY);
			n1.setName("No informations available.");
			n1.setParent(root);
			root.addChild(n1);
			return root;
		}
		  catch (Exception e) {
			log.warn("Building hierarchy tree.",e);
			return root;
		} 
		
	}
	
	private RootNode buildTestTreeModel()
	{
		RootNode root = new RootNode();
		root.setKind("Root Node");
		root.setName("Root Node Name");
		StandardNode n1 = new StandardNode();
		n1.setKind(HierarchyNode.CLASS);
		n1.setName("Sample class");
		n1.setParent(root);
		root.addChild(n1);
		StandardNode n2 = new StandardNode();
		n2.setKind(HierarchyNode.PARENTS);
		n2.setName("Parents");
		n2.setParent(n1);
		n1.addChild(n2);
		StandardNode n3 = new StandardNode();
		n3.setKind(HierarchyNode.NESTED);
		n3.setName("Nested Class 1");
		n3.setParent(n1);
		n1.addChild(n3);
		StandardNode n4 = new StandardNode();
		n4.setKind(HierarchyNode.SUPER);
		n4.setName("Super Class 1");
		n4.setParent(n2);
		n2.addChild(n4);
		StandardNode n5 = new StandardNode();
		n5.setKind(HierarchyNode.SUPER);
		n5.setName("Super Class 2");
		n5.setParent(n2);
		n2.addChild(n5);
		
		StandardNode n6 = new StandardNode();
		n6.setKind(HierarchyNode.NESTEDPARENTS);
		n6.setName("Parents");
		n6.setParent(n3);
		n3.addChild(n6);
		
		StandardNode n7 = new StandardNode();
		n7.setKind(HierarchyNode.NESTEDSUPER);
		n7.setName("Super Class 1.Nested Class");
		n7.setParent(n6);
		n6.addChild(n7);
		return root;
	}
	
	private LinearNode buildListModel(String path)
	{
		try {
			if (path==null)
				throw (new NullPointerException("no information"));
			CaesarHierarchyTest nav = new CaesarHierarchyTest(globalPathForInformationAdapter);
	        CClass clazz = nav.load(path);
			AdditionalCaesarTypeInformation info = clazz.getAdditionalTypeInformation();
			LinearNode node1 = new LinearNode();
			LinearNode node2 = new LinearNode();
			String [] list = info.getMixinList();
			if (list.length>0)
			{
				for (int i = 0; list.length>i; i++)
				{
					if ( i==0 )
					{
						node1 = new LinearNode();
						node1.setKind(HierarchyNode.LIST);
						node1.setName(list[i]);
					} else
					{
						node2 = node1;
						node1 = new LinearNode();
						node2.setPreNode(node1);
						node1.setNextNode(node2);
						node1.setKind(HierarchyNode.LIST);
						node1.setName(list[i]);
					}
				}
				return node1;
			}else
				throw (new NullPointerException("no information"));
		}
		catch (NullPointerException e) {
		  	log.debug("No Information to show on hierarchy tree.");
		  	LinearNode emty = new LinearNode();
		  	emty.setKind(HierarchyNode.LIST);
			emty.setName("No list available for selection");
			return emty;
		}
		catch(Exception e)
		{
			log.warn("Building List View.",e);
			return null;
		}
	}
	
	private LinearNode buildListModel()
	{
		LinearNode n1 = new LinearNode();
		LinearNode n2 = new LinearNode();
		LinearNode n3 = new LinearNode();
				
		n1.setKind("linearizedclass");
		n1.setName("Subclass 2");
		
		n2.setKind("linearizedclass");
		n2.setName("Subclass 1");
		
		n3.setKind("linearizedclass");
		n3.setName("Sample class");
	
		n1.setNextNode(n2);
		n2.setNextNode(n3);
		n2.setPreNode(n1);
		n3.setPreNode(n2);
		return n1;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		Group layoutGroup = new Group(parent,SWT.NONE);
		Group topGroup = new Group(layoutGroup,SWT.NONE);
		Group buttomGroup = new Group(layoutGroup,SWT.NONE);
		topGroup.setText("Tree View");
		buttomGroup.setText("Mixin View");
		treeViewer = new TreeViewer(topGroup, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.HORIZONTAL);

		HierarchyTreeContentProvider cp = new HierarchyTreeContentProvider();
		HierarchySelectionChangedListener scl = new HierarchySelectionChangedListener();
		HierarchyLabelProvider lp = new HierarchyLabelProvider();
		treeViewer.setContentProvider(cp);
		treeViewer.setLabelProvider(lp);
		treeViewer.addSelectionChangedListener(scl);
		
		//for testing treeViewer.setInput(buildTestTreeModel());
		
		treeViewer.expandAll();
		
		HierarchyListContentProvider lcp = new HierarchyListContentProvider();
		listViewer = new ListViewer(buttomGroup, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.HORIZONTAL);
		listViewer.setContentProvider(lcp);
		listViewer.setLabelProvider(lp);
		refreshTree("");
		
		FillLayout layout = new FillLayout();
		layout.type = SWT.VERTICAL;
		layoutGroup.setLayout(layout);
		topGroup.setLayout(layout);
		buttomGroup.setLayout(layout);
		
		getViewSite().
		getWorkbenchWindow().
		getSelectionService().
		addSelectionListener(this);

	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {

	}

	/**
	 * @return Returns the treeViewer.
	 */
	public static TreeViewer getTreeViewer() {
		return treeViewer;
	}

	
	protected class HierarchyListContentProvider implements IStructuredContentProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
	
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object listElement) {
			Vector vec = new Vector();
			if (listElement instanceof LinearNode) {
				LinearNode element = (LinearNode) listElement;
				vec = element.getAll();
				//vec.add(element);
			}
			return vec.toArray();
		}
		
		
	}
	
	protected class HierarchyTreeContentProvider implements ITreeContentProvider, IHierarchyPropertyChangeListener {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			try {
				if(oldInput == null && newInput != null)
				{	
					RootNode input = (RootNode)newInput;
					input.addPropertyChangeListener("Content provider", this);			
				}
				else if(oldInput != null && newInput == null)
				{
					RootNode input = (RootNode)newInput;
					input.removePropertyListener("Content provider", this);
				}
				
			}
			catch(Exception e)
			{
				
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
				return node.getChildren()!=null;
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
	
		}
	}
	protected class HierarchyLabelProvider extends LabelProvider
	{
		
		public Image getImage(Object element) {
			if (element instanceof RootNode) {
				RootNode node = (RootNode) element;
				if (0==node.getKind().compareTo(HierarchyNode.CLASS)|
						0==node.getKind().compareTo(HierarchyNode.SUPER)|
						0==node.getKind().compareTo(HierarchyNode.NESTED)|
						0==node.getKind().compareTo(HierarchyNode.NESTEDSUPER)
				)
					return CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC.createImage();
				else if (0==node.getKind().compareTo(HierarchyNode.PARENTS)|
						0==node.getKind().compareTo(HierarchyNode.NESTEDPARENTS)|
						0==node.getKind().compareTo(HierarchyNode.INCREMENTCLASSES)
				)
					return JavaPluginImages.DESC_OBJS_IMPCONT.createImage();
				else
					return super.getImage(element);
			}
			else
				return null;
		}
		
		public String getText(Object element)
		{
			String help;
			if (element instanceof RootNode) {
				RootNode node = (RootNode) element;
				//help = node.getName();
				help = filterName(node.getName());
				if (node.hasAdditionalName())
				{
					String help2 = node.getAdditionalName();
					help2 = help2.substring(help2.lastIndexOf("/")+1,help2.lastIndexOf("$"));
					help = help + " (" + help2 + ")";
				}
				return replaceAll(help,"$",".");
			}
			else if (element instanceof LinearNode) {
				LinearNode node = (LinearNode) element;
				return replaceAll(node.getName(),"$",".");
			}
			else
			{
				return "unknown object";
			}
		}
		private String replaceAll(String source, String orig_val, String new_val)
		{
			try {
				String help = source;
				for (;help.lastIndexOf(orig_val)>0;)
				{
					help = help.substring(0,help.lastIndexOf(orig_val))+new_val+help.substring(help.lastIndexOf(orig_val)+1);
				} 
				return help;
			} catch (Exception e) {
				log.warn("Replacing '"+orig_val+"' with '"+new_val+"' in '"+source+"'.",e);
				return source;
			}
		}
		
		private String filterName(String name)
		{
			try {
				String help = new String(name);
				int slashPos = name.lastIndexOf("/");
				int dollarPos = name.lastIndexOf("$");
				if (slashPos>0)
					help = name.substring(slashPos+1);
				/*if (dollarPos>slashPos)
				{
					help = name.substring(dollarPos+1);
				}*/		
				return help;
			}
			catch(Exception e)
			{
				log.warn("Filtering names for Hierarchy View.",e);
				return "Error";
			}
		}
	}
	
	
	private class HierarchySelectionChangedListener implements ISelectionChangedListener
	{

		public HierarchySelectionChangedListener()
		{
			
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			if (selection.getFirstElement() instanceof StandardNode) {
				StandardNode markedNode = (StandardNode) selection.getFirstElement() ;
				if (markedNode.getKind().compareTo(HierarchyNode.SUPER)==0)
				{
					log.debug("Super class '"+markedNode.getName()+"' selected.");
					refreshTree(markedNode.getName());
					//refreshEditor(markedNode);
				}
				else if (markedNode.getKind().compareTo(HierarchyNode.NESTED)==0)
				{
					log.debug("Nested class '"+markedNode.getName()+"' selected.");
					refreshList(markedNode.getName());
				}
				else if (markedNode.getKind().compareTo(HierarchyNode.NESTEDSUPER)==0)
				{
					log.debug("Nested Super class '"+markedNode.getName()+"' selected.");
					refreshList(markedNode.getName());
				}
				else if (markedNode.getKind().compareTo(HierarchyNode.CLASS)==0)
				{
					log.debug("Main Class '"+markedNode.getName()+"' selected.");
					refreshList(markedNode.getName());
				}
				else
				{
					log.debug("Not interested in selection of '"+markedNode.getName()+"'.");
					refreshList(null);
				}
			}
		}
		
	}
	
	/**
	 * @return Returns the listViewer.
	 */
	public static ListViewer getListViewer() {
		return listViewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		String path;
		if (part instanceof CaesarEditor) {			
			CaesarEditor editor = (CaesarEditor)part;
			if (editor.getEditorInput() instanceof FileEditorInput) {
				FileEditorInput input = (FileEditorInput) editor.getEditorInput();
				//TODO noch nicht sicher, ob das gehen wird
				path = input.getFile().getProjectRelativePath().toString();
				path = path.substring(0,path.indexOf(input.getFile().getFileExtension())-1);
				log.debug("Selection in Editor! \n\tFile: '"+editor.getEditorInput().getName()+"'.\n\tQualified name: "+path+"'");
				refreshTree(path);
			}
	    
		}		
		log.debug("Selection in Editor! Part: '"+part+"'.");
	}

	public void refresh()
	{
		log.debug("Refresh!!!");
	}
	
	public void refreshTree(String path)
	{
		log.debug("Refreshing Hierarchy Tree for '"+path+"'!");
		treeViewer.setInput(buildTreeModel(path));
		treeViewer.expandAll();
		//treeViewer.expandToLevel(3);
		refreshList(path);
	}
	
	public void refreshList(String path)
	{
		log.debug("Refreshing Hierarchy List'"+path+"'!");
		listViewer.setInput(buildListModel(path));
	}
	
}
