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

import java.util.Vector;

import org.caesarj.ui.resources.CaesarJPluginResources;
import org.caesarj.ui.views.hierarchymodel.HierarchyNode;
import org.caesarj.ui.views.hierarchymodel.LinearNode;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * The content provider for the mixin view list
 * 
 * @author Jochen
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 * 
 */
public class CaesarJHierarchyListContentProvider implements	IStructuredContentProvider {
	
	/**
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
			LinearNode node = (LinearNode) input;
			// Check if the node is not of the empty kind
			if (! node.getKind().equals(HierarchyNode.EMPTY)) {
				Vector elements = node.getAll();
				// Check if the node has children
				if (elements.size() > 0) {
					return elements.toArray();
				}
			}
		} catch (Exception e) {
			// do nothing. Return the message.
		}
		// Return an array with only a message for no information
		return new Object[] { CaesarJPluginResources.getResourceString("HierarchyView.hierarchy.noMixinAvailable")};
	}
}
