/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: RootNode.java,v 1.7 2005-03-09 00:05:13 thiago Exp $
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
	
	public RootNode(String kind) {
		super();
		this.setKind(kind);
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
	public Vector getChildrenVector() {
		return children;
	}
	public void addChild(HierarchyNode child) {
		propertyChange(new PropertyChangeEvent(this, "new child", null, child));
		children.add(child);		
	}
	
}
