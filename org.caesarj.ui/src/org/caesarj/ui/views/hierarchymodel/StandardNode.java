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
 * $Id: StandardNode.java,v 1.7 2005-01-24 16:57:22 aracic Exp $
 */

package org.caesarj.ui.views.hierarchymodel;

import java.util.Iterator;

import org.caesarj.runtime.AdditionalCaesarTypeInformation;

/**
 * @author Jochen
 *
 */
public class StandardNode extends RootNode {
	
	HierarchyNode parent;

	/**
	 * 
	 */
	public StandardNode() {
		super();
	}
	
	public StandardNode (String kind, String name, StandardNode parent) {
		super();
		this.setKind(kind);
		this.setName(name);
		this.setParent(parent);
		parent.addChild(this);
	}

	public StandardNode (String kind, String name, StandardNode parent, AdditionalCaesarTypeInformation info)
	{
		this(kind, name, parent);
		this.setTypeInforamtion(info);
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
	private boolean hasChildrenWithSameName(StandardNode node)
	{
		boolean help = false;;
		for (Iterator iter = children.iterator(); iter.hasNext(); )
		{
			help = help | (((StandardNode)iter.next()).getName().compareTo(node.getName())==0);
		}
		return help;
	}
	
	public boolean hasSubNode(StandardNode node)
	{
		boolean help = false;
		if (hasChildrenWithSameName(node))
			return true;
		else
		{
			for(Iterator iter = children.iterator(); iter.hasNext();)
			{
				help = help | ((StandardNode)iter.next()).hasSubNode(node);
			}
		}
		return help;
	}
	
	public void removeChild(StandardNode node)
	{
		log.debug("Removeing Node '"+node.getName()+"' from Node '"+this.getName()+"'.");
		children.remove(node);
	}

}
