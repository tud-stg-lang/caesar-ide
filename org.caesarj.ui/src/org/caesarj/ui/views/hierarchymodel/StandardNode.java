/*
 * Created on 02.09.2004
 *
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
