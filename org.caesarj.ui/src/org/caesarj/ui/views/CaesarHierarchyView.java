/*
 * Created on 02.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.ui.views;



import java.util.Vector;

import org.apache.log4j.Logger;
import org.caesarj.ui.views.hierarchymodel.HierarchyNode;
import org.caesarj.ui.views.hierarchymodel.ParentsNode;
import org.caesarj.ui.views.hierarchymodel.RootNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jochen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CaesarHierarchyView extends ViewPart {

	private TreeViewer viewer;
	
	private static Logger log = Logger.getLogger(CaesarHierarchyView.class);
	
	
	private RootNode getModel()
	{
		RootNode root = new RootNode();
		root.setKind("Root Node");
		root.setName("Root Node Name");
		ParentsNode n1 = new ParentsNode();
		n1.setKind("class");
		n1.setName("Classname");
		n1.setParent(root);
		root.addChild(n1);
		ParentsNode n2 = new ParentsNode();
		n2.setKind("parents");
		n2.setName("Parents");
		n2.setParent(n1);
		n1.addChild(n2);
		ParentsNode n3 = new ParentsNode();
		n3.setKind("nested");
		n3.setName("Nested Class 1");
		n3.setParent(n1);
		n1.addChild(n3);
		ParentsNode n4 = new ParentsNode();
		n4.setKind("super");
		n4.setName("Super Class 1");
		n4.setParent(n2);
		n2.addChild(n4);
		ParentsNode n5 = new ParentsNode();
		n5.setKind("super");
		n5.setName("Super Class 2");
		n5.setParent(n2);
		n2.addChild(n5);
		return root;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		HierarchyContentProvider cp = new HierarchyContentProvider();
		HierarchyLabelProvider lp = new HierarchyLabelProvider();
		viewer.setContentProvider(cp);
		viewer.setLabelProvider(lp);
		viewer.setInput(getModel());
		viewer.expandAll();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return Returns the viewer.
	 */
	public TreeViewer getViewer() {
		return viewer;
	}
	/**
	 * @param viewer The viewer to set.
	 */
	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
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
}
