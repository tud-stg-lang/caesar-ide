/*
 * Created on 02.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.ui.views.hierarchymodel;

/**
 * @author Jochen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StandardNode extends RootNode {
	
	HierarchyNode parent;

	/**
	 * 
	 */
	public StandardNode() {
		super();
	}
	
	public StandardNode (String kind, String name, HierarchyNode parent) {
		super();
		this.setKind(kind);
		this.setName(name);
		this.setParent(parent);
	}

	/* (non-Javadoc)
	 * @see org.caesarj.ui.views.hierarchymodel.HierarchyNode#getParent()
	 */
	public HierarchyNode getParent() {
		
		return parent;
	}
	
	public void setParent(HierarchyNode parent)
	{
		this.parent = parent;
	}

}