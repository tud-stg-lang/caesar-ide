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
 * $Id: LinearNode.java,v 1.3 2005-01-24 16:57:22 aracic Exp $
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
