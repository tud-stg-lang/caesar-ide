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
 * $Id: HierarchyNode.java,v 1.10 2005-05-12 08:41:35 thiago Exp $
 */

package org.caesarj.ui.views.hierarchymodel;

import org.apache.log4j.Logger;
import org.caesarj.runtime.AdditionalCaesarTypeInformation;
import org.eclipse.core.internal.runtime.ListenerList;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;

/**
 * @author Jochen
 *
 */
public class HierarchyNode implements ISelection {
	public HierarchyNode()
	{
		kind = "no kind specified";
		name = "no name specified";
		additionalName = "no name specified";
		additionalNameSet = false;
		typeInformation = null;
		ll = new ListenerList();
	}
	
	public HierarchyNode getParent()
	{
		return this;
	}
	public Object[] getChildren()
	{
		return null;
	}
	
	protected String kind;
	protected String name;
	protected String additionalName;
	protected boolean additionalNameSet;
	protected ListenerList ll;
	protected AdditionalCaesarTypeInformation typeInformation;
	protected static Logger log = Logger.getLogger(HierarchyNode.class);
	public static String CLASS = new String("class");
	public static String PARENTS = new String("parents");
	public static String NESTED = new String("nested");
	public static String SUPER = new String("super");
	public static String NESTEDCLASSES = new String("nestedclasses");
	public static String NESTEDSUB = new String("nestedsub");
	public static String NESTEDSUPER = new String("nestedsuper");
	public static String ROOT = new String("rootnode");
	public static String LIST = new String("list");
	public static String EMPTY = new String("empty");
	
	public boolean hasAdditionalName()
	{
		return additionalNameSet;
	}
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
		propertyChange(new PropertyChangeEvent(this, "kind", this.kind, kind));
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
		propertyChange(new PropertyChangeEvent(this, "name", this.name, name));
		this.name = name;
	}
	
	/*
	public synchronized void addPropertyChangeListener (String name, IHierarchyPropertyChangeListener listener)
	{
		ll.add(listener);
	}
	
	public synchronized void removePropertyListener ( String name, IHierarchyPropertyChangeListener listener)
	{
		ll.remove(listener);
	}
	*/
	public void firePropertyChange(PropertyChangeEvent event)
	{
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event!=null)
		{
			log.debug("Proberty '"+event.getProperty()+"' changed.");
		}
		else
		{
			log.debug("Unspecified proberty changed.");
		}
		/* Not necessary in this context
		IHierarchyPropertyChangeListener listener;
		Object[] listeners = ll.getListeners();
		for (int i = 0; i<listeners.length; i++)
		{
			listener=(IHierarchyPropertyChangeListener)listeners[i];
			listener.propertyChange(event);
		}
		*/
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	public boolean isEmpty() {
		return false;
	}
	public AdditionalCaesarTypeInformation getTypeInformation() {
		return typeInformation;
	}
	public void setTypeInformation(AdditionalCaesarTypeInformation typeInformation) {
		this.typeInformation = typeInformation;
	}
	public String getAdditionalName() {
		return additionalName;
	}
	public void setAdditionalName(String additionalName) {
		if (additionalNameSet)
			this.additionalName = this.additionalName + " - " + additionalName;
		else
			this.additionalName = additionalName;
		additionalNameSet = true;
	}
}
