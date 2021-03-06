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
 * $Id: CaesarOutlineViewContentProvider.java,v 1.10 2006-10-10 17:00:37 gasiunas Exp $
 */

package org.caesarj.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.aspectj.asm.IProgramElement;
import org.caesarj.compiler.asm.CaesarProgramElement;
import org.caesarj.compiler.asm.LinkNode;
import org.caesarj.compiler.asm.CaesarProgramElement.Kind;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author meffert
 *
 * TODO [documentation]
 */
public class CaesarOutlineViewContentProvider implements ITreeContentProvider {

	public CaesarOutlineViewContentProvider() {
	}
	
	/**
	 * Implementation for the ITreeContentProvider interface
	 * Called before the class is disposed
	 */
	public void dispose() {
	}

	/**
	 * Called when the provider has a new input to show.
	 * 
	 * @param viewer the element using this provider. Probably a ListViewer
	 * @param oldInput the input object that was the previous input for this provider
	 * @param newInput the new input object.
	 *  
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/**
	 * This method is called when we have a new input. It returns all the elements that will
	 * appear in the tree. If the input is a IProgramElement, it returns the children. Else,
	 * return an empty array.
	 */
	public Object[] getElements(Object input) {
	    
	    if (input instanceof IProgramElement) {
	        return ((IProgramElement) input).getChildren().toArray();
	    }
	    
	    // Log some error
	    if(input != null){
	        Logger.getLogger(this.getClass()).info("getElements() called with instanceof " + input.getClass().getName());
		}else{
			Logger.getLogger(this.getClass()).info("getElements() called with NULL.");
		}
	    return new Object[0];
	}

	/**
	 * Returns the children from the element.
	 * 
	 */
	public Object[] getChildren(Object parent) {
	    
	    if (parent instanceof LinkNode) {
	        return getChildren((LinkNode) parent);
	    }
	    if (parent instanceof CaesarProgramElement) {
	        return getChildren((CaesarProgramElement) parent);
	    }	   
	    if (parent instanceof IProgramElement) {
	        return getChildren((IProgramElement) parent);
	    }
	    
	    // Log error
        if (parent != null) {
            Logger.getLogger(this.getClass()).info("getChildren() called with instanceof " + parent.getClass().getName());
        } else {
            Logger.getLogger(this.getClass()).info("getChildren() called with NULL.");
        }
	    return new Object[0];
	}
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	protected Object[] getChildren(IProgramElement parent) {
	    return parent.getChildren().toArray();
	}
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	protected Object[] getChildren(LinkNode parent) {
	    return parent.getChildren().toArray();
	}
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	protected Object[] getChildren(CaesarProgramElement parent) {
	    
	    // Get the parent's kind
	    Kind kind = parent.getCaesarKind();
	    
	    // Do not get children from packages
	    if (CaesarProgramElement.Kind.PACKAGE == kind) {
	        return new Object[0];
	    }
	    
	    ArrayList elements = new ArrayList();
	    
	    // If the element is an import declaration list, get the children, removing java/lang
	    if (CaesarProgramElement.Kind.IMPORTS == kind) {
	        
	        Iterator i = parent.getChildren().iterator();
	        while(i.hasNext()) {
                CaesarProgramElement child = (CaesarProgramElement) i.next();
                if (child.getName().compareTo("java.lang") != 0) {
                    elements.add(child);
                }	            
	        }
	        return elements.toArray();
	    }
	    
        // add children
        // add all children except if child is of Kind ADVICE_REGISTRY,
        // then add childs children to node
	    Iterator i = parent.getChildren().iterator();
	    while(i.hasNext()) {
	        
	        IProgramElement childElement = (IProgramElement) i.next();
	        if (childElement instanceof LinkNode) {
	            // Add if it is a link node
	            elements.add(childElement);
	        } else if (! (childElement instanceof CaesarProgramElement)) {
	            // If it is an aspectj node, add all children
	            elements.addAll(childElement.getChildren());
	        } else {
	            // Transform to caesar types
	            CaesarProgramElement child = (CaesarProgramElement) childElement;
	            Kind childKind = ((CaesarProgramElement) child).getCaesarKind();
	            
	            // If it is an ADVICE_REGISTRY, add all its children, but not itself
	            if (CaesarProgramElement.Kind.ADVICE_REGISTRY == childKind) {
                    elements.addAll(child.getChildren());
                } else {
                    elements.add(child);
                }
	        }
	    }
	   
        return elements.toArray();
    }

	/**
     * Implementation for the ITreeContentProvider interface
     * 
     * Returns the parent if it is an IProgramElement or null
     */
	public Object getParent(Object element) {
		if(element instanceof IProgramElement){
			return ((IProgramElement) element).getParent();
		}
		return null;
	}

	/**
	 * Returns true if the element has children, false otherwise
	 */
	public boolean hasChildren(Object element) {
	    if (element instanceof LinkNode) {
	        return ((LinkNode) element).getChildren().size() > 0;
	    }
		if(element instanceof CaesarProgramElement){
			//Logger.getLogger(this.getClass()).info("hasChildren() called with instanceof CaesarProgramElement. " + element.getClass().getName());
			if(CaesarProgramElement.Kind.PACKAGE == ((CaesarProgramElement)element).getCaesarKind()){
				return false;
			}else if(CaesarProgramElement.Kind.IMPORTS == ((CaesarProgramElement)element).getCaesarKind()){
				// the import java/lang always exists => do not display it.
				return ((CaesarProgramElement)element).getChildren().size() > 1;
			}
			//System.out.println(((CaesarProgramElement)element).getCaesarKind().toString() + " - Relations: " + ((CaesarProgramElement)element).getRelations().size());
			// return children + relations > 0
			return ((CaesarProgramElement)element).getChildren().size() > 0
						|| ((CaesarProgramElement)element).getRelations().size() > 0;
		}else if(element instanceof IProgramElement){
			// if element is instanceof ProgramElementNode (Kind == CODE?) 
			// return sizeof children + sizeof Relations
			return ((IProgramElement)element).getChildren().size() > 0;
		}else{
			System.out.println("hasChildren(): Not a ProgramElementNode");
		}
		return false;
	}
}
