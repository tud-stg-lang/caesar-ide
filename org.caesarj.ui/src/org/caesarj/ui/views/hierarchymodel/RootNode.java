/*
 * Created on 02.09.2004
 */
package org.caesarj.ui.views.hierarchymodel;

import java.util.Vector;

import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * @author Jochen
 *
 */
public class RootNode extends HierarchyNode {
	
	Vector children;
	
	boolean furtherBinding = false;

	public boolean isFurtherBinding() {
		return furtherBinding;
	}
	public void setFurtherBinding(boolean furtherBinding) {
		this.furtherBinding = furtherBinding;
	}
	
	public RootNode()
	{
		super();
		children = new Vector();
	}

	/* (non-Javadoc)
	 * @see org.caesarj.ui.views.hierarchymodel.HierarchyNode#getParent()
	 */
	public HierarchyNode getParent() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caesarj.ui.views.hierarchymodel.HierarchyNode#getChildren()
	 */
	public Object[] getChildren() {
		return children.toArray();
	}
	public void addChild(HierarchyNode child) {
		propertyChange(new PropertyChangeEvent(this, "new child", null, child));
		children.add(child);		
	}
	
}
