/*
 * Created on 02.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HierarchyNode implements ISelection {
	public HierarchyNode()
	{
		kind = "no kind specified";
		name = "no name specified";
		additionalName = "no name specified";
		additionalNameSet = false;
		typeInforamtion = null;
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
	
	private String kind;
	private String name;
	private String additionalName;
	private boolean additionalNameSet;
	private ListenerList ll;
	private AdditionalCaesarTypeInformation typeInforamtion;
	protected static Logger log = Logger.getLogger(HierarchyNode.class);
	public static String CLASS = new String("class");
	public static String PARENTS = new String("parents");
	public static String NESTED = new String("nested");
	public static String SUPER = new String("super");
	public static String NESTEDPARENTS = new String("nestedparents");
	public static String NESTEDSUPER = new String("nestedsuper");
	public static String ROOT = new String("rootnode");
	public static String INCREMENTCLASSES = new String("incrementclasses");
	public static String LIST = new String("list");
	public static String EMTY = new String("emty");
	
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
	
	public synchronized void addPropertyChangeListener (String name, IHierarchyPropertyChangeListener listener)
	{
		ll.add(listener);
	}
	
	public synchronized void removePropertyListener ( String name, IHierarchyPropertyChangeListener listener)
	{
		ll.remove(listener);
	}
	
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
	public AdditionalCaesarTypeInformation getTypeInforamtion() {
		return typeInforamtion;
	}
	public void setTypeInforamtion(
			AdditionalCaesarTypeInformation typeInforamtion) {
		this.typeInforamtion = typeInforamtion;
	}
	public String getAdditionalName() {
		return additionalName;
	}
	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
		additionalNameSet = true;
	}
}
