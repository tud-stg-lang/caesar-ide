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
public abstract class HierarchyNode {
	public HierarchyNode()
	{
		kind = "no kind";
		name = "no name";
	}
	
	public abstract HierarchyNode getParent();
	public abstract Object[] getChildren();
	
	private String kind;
	private String name;
	/**
	 * @return Returns the kind.
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind The kind to set.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
