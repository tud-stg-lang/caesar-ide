/*
 * Created on Sep 6, 2004

 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.ui.views.hierarchymodel;

import java.util.Vector;

import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * @author Jochen
 *
 */
public class LinearNode extends HierarchyNode {

	/**
	 * 
	 */
	public LinearNode() {
		super();

	}

	/* (non-Javadoc)
	 * @see org.caesarj.ui.views.hierarchymodel.HierarchyNode#getParent()
	 */
	public HierarchyNode getParent() {
		return this;
	}
	
	LinearNode nextNode;
	LinearNode preNode;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {

	}

	
	public Vector getAll()
	{
		log.debug("getAll() called.");
		Vector all = new Vector();
		all = getLeft(this, all);		
		all.removeElement(this);
		all = getRight(this, all);
		return all;
	}
	
	private Vector getRight(LinearNode node, Vector vec)
	{
		Vector vecHelp = vec;
		vecHelp.addElement(node);
		if (node.getNextNode()!=null)
		{
			vecHelp = getRight(node.getNextNode(), vecHelp);
		}		
		return vecHelp;
	}
	
	private Vector getLeft(LinearNode node, Vector vec)
	{
		Vector vecHelp = vec;
		if (node.getPreNode()!=null)
		{
			vecHelp = getLeft(node.getPreNode(), vecHelp);
		}
		vecHelp.addElement(node);
		return vecHelp;
	}

	/**
	 * @return Returns the nextNode.
	 */
	public LinearNode getNextNode() {
		return nextNode;
	}
	/**
	 * @param nextNode The nextNode to set.
	 */
	public void setNextNode(LinearNode nextNode) {
		propertyChange(new PropertyChangeEvent(this, "next node", this.nextNode, nextNode));
		this.nextNode = nextNode;
	}
	/**
	 * @return Returns the preNode.
	 */
	public LinearNode getPreNode() {
		return preNode;
	}
	/**
	 * @param preNode The preNode to set.
	 */
	public void setPreNode(LinearNode preNode) {
		propertyChange(new PropertyChangeEvent(this, "pre node", this.preNode, preNode));
		this.preNode = preNode;
	}
}
