/*
 * Created on 02.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.ui.views.hierarchymodel;

import java.util.Vector;

/**
 * @author Jochen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RootNode extends HierarchyNode {
	
	Vector children;
	
	public RootNode()
	{
		super();
		children = new Vector();
	}

	/* (non-Javadoc)
	 * @see org.caesarj.ui.views.hierarchymodel.HierarchyNode#getParent()
	 */
	public HierarchyNode getParent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.caesarj.ui.views.hierarchymodel.HierarchyNode#getChildren()
	 */
	public Object[] getChildren() {
		return children.toArray();
	}
	public void addChild(HierarchyNode child) {
		children.add(child);
	}
	
}
