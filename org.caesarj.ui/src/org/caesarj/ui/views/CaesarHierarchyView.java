package org.caesarj.ui.views;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.caesarj.ui.views.hierarchymodel.HierarchyNode;
import org.caesarj.ui.views.hierarchymodel.RootNode;
import org.caesarj.ui.views.hierarchymodel.StandardNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.framelist.FrameList;
import org.eclipse.ui.views.navigator.NavigatorFrameSource;


/**
 * @author Jochen
 *
 */
public class CaesarHierarchyView extends ViewPart {

	private TreeViewer treeViewer;
	private ListViewer listViewer;
	
	private static Logger log = Logger.getLogger(CaesarHierarchyView.class);
	
	
	private RootNode buildTreeModel()
	{
		RootNode root = new RootNode();
		root.setKind("Root Node");
		root.setName("Root Node Name");
		StandardNode n1 = new StandardNode();
		n1.setKind("class");
		n1.setName("Sample class");
		n1.setParent(root);
		root.addChild(n1);
		StandardNode n2 = new StandardNode();
		n2.setKind("parents");
		n2.setName("Parents");
		n2.setParent(n1);
		n1.addChild(n2);
		StandardNode n3 = new StandardNode();
		n3.setKind("nested");
		n3.setName("Nested Class 1");
		n3.setParent(n1);
		n1.addChild(n3);
		StandardNode n4 = new StandardNode();
		n4.setKind("super");
		n4.setName("Super Class 1");
		n4.setParent(n2);
		n2.addChild(n4);
		StandardNode n5 = new StandardNode();
		n5.setKind("super");
		n5.setName("Super Class 2");
		n5.setParent(n2);
		n2.addChild(n5);
		
		StandardNode n6 = new StandardNode();
		n6.setKind("nestedparents");
		n6.setName("Parents");
		n6.setParent(n3);
		n3.addChild(n6);
		
		StandardNode n7 = new StandardNode();
		n7.setKind("nestedsuper");
		n7.setName("Super Class 1.Nested Class");
		n7.setParent(n6);
		n6.addChild(n7);
		return root;
	}
	
	private RootNode buildListModel()
	{
		RootNode root = new RootNode();
		root.setKind("Root Node");
		root.setName("Root Node Name");
		StandardNode n1 = new StandardNode();
		n1.setKind("linearizedclass");
		n1.setName("Subclass");
		n1.setParent(root);
		root.addChild(n1);
		
		StandardNode n2 = new StandardNode();
		n2.setKind("linearizedclass");
		n2.setName("Sample class");
		n2.setParent(root);
		root.addChild(n2);
		return root;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		HierarchyContentProvider cp = new HierarchyContentProvider();
		HierarchyLabelProvider lp = new HierarchyLabelProvider();
		treeViewer.setContentProvider(cp);
		treeViewer.setLabelProvider(lp);
		treeViewer.setInput(buildTreeModel());
		treeViewer.expandAll();
	
		listViewer = new ListViewer(parent);
		listViewer.setContentProvider(cp);
		listViewer.setLabelProvider(lp);
		listViewer.setInput(buildListModel());

	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return Returns the treeViewer.
	 */
	public TreeViewer getViewer() {
		return treeViewer;
	}
	/**
	 * @param treeViewer The treeViewer to set.
	 */
	public void setViewer(TreeViewer viewer) {
		this.treeViewer = viewer;
	}
	
	protected class HierarchyContentProvider implements ITreeContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
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
	}
	protected class HierarchyLabelProvider extends LabelProvider
	{
		public String getText(Object element)
		{
			log.debug("Label requested.");
			if (element instanceof HierarchyNode) {
				HierarchyNode node = (HierarchyNode) element;
				return node.getKind()+": "+node.getName();
			}
			else
			{
				return "unknown object";
			}
		}
	}
	/**
	 * @return Returns the listViewer.
	 */
	public ListViewer getListViewer() {
		return listViewer;
	}
	/**
	 * @param listViewer The listViewer to set.
	 */
	public void setListViewer(ListViewer listViewer) {
		this.listViewer = listViewer;
	}
}
