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
 */
package org.caesarj.ui.views;

import org.caesarj.ui.resources.CaesarJPluginResources;
import org.caesarj.ui.views.hierarchymodel.HierarchyNode;
import org.caesarj.ui.views.hierarchymodel.RootNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * The content provider for the hierarchy tree
 * 
 * @author Jochen
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 * 
 */
public class CaesarJHierarchyTreeContentProvider implements ITreeContentProvider {
	
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
	 * Called to get the elements to show when input is set as new input
	 * 
 	 * @param input 
	 * @return a list of nodes, as set by the inputchanged method
	 */
	public Object[] getElements(Object input) {
		
		try {
			RootNode node = (RootNode) input;
			// Check if the node is not of the empty kind
			if (! node.getKind().equals(HierarchyNode.EMPTY)) {
				Object[] elements = getChildren(node);
				// Check if the node has children
				if (elements.length > 0) {
					return elements;
				}
			}
		} catch (Exception e) {
			// do nothing. Return the message.
		}
		// Return an array with only a message for no information
		return new Object[] { CaesarJPluginResources.getResourceString("HierarchyView.hierarchy.noInformationAvailable")};
	}

	/**
	 * Returns the children from the element.
	 * If the element is not an hierarchy node, return an empty list.
	 */
	public Object[] getChildren(Object parent) {
		
		if (parent instanceof HierarchyNode) {
			return ((HierarchyNode) parent).getChildren();
		}
		return new Object[0];
	}

	/**
	 * Returns the parent of the element or null if the element is not
	 * an hierarchy node
	 * 
	 */
	public Object getParent(Object element) {
		if (element instanceof HierarchyNode) {
			return ((HierarchyNode) element).getParent();
		}
		return null;
	}

	/**
	 * Return true if the element is an hierarchy node and has children
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof HierarchyNode) {
			Object[] children =  ((HierarchyNode) element).getChildren();
			return (children != null && children.length > 0);
		}
		return false;
	}

}
