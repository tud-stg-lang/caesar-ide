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

import org.caesarj.ui.CaesarElementImageDescriptor;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.editor.CaesarJContentOutlinePage;
import org.caesarj.ui.views.hierarchymodel.HierarchyNode;
import org.caesarj.ui.views.hierarchymodel.LinearNode;
import org.caesarj.ui.views.hierarchymodel.RootNode;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * The label provider for the hierarchy tree and list.
 * 
 * @author Jochen
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 * 
 */
public class CaesarJHierarchyLabelProvider extends LabelProvider {

	
	public Image getImage(Object element) {
		if (element instanceof RootNode) {
			RootNode node = (RootNode) element;
			if (0 == node.getKind().compareTo(HierarchyNode.CLASS)
					| 0 == node.getKind().compareTo(HierarchyNode.SUPER)
					| 0 == node.getKind().compareTo(HierarchyNode.NESTED)
					| 0 == node.getKind().compareTo(HierarchyNode.NESTEDSUPER)
					| 0 == node.getKind().compareTo(HierarchyNode.NESTEDSUB))
				try {
					if (node.getTypeInformation().isImplicit())
						return new CaesarElementImageDescriptor(
								CaesarPluginImages.DESC_OBJS_INNER_CCLASS_IMPLICID,
								CaesarJContentOutlinePage.BIG_SIZE, node)
								.createImage();
					else
						return new CaesarElementImageDescriptor(
								CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC,
								CaesarJContentOutlinePage.BIG_SIZE, node)
								.createImage();
				} catch (NullPointerException e) {
					//For nodes, which do not have any typ information
					return new CaesarElementImageDescriptor(
							CaesarPluginImages.DESC_OBJS_INNER_CCLASS_IMPLICID,
							CaesarJContentOutlinePage.BIG_SIZE, node)
							.createImage();
				}

			else if (0 == node.getKind().compareTo(HierarchyNode.PARENTS)
					| 0 == node.getKind()
							.compareTo(HierarchyNode.NESTEDCLASSES))
				return JavaPluginImages.DESC_OBJS_IMPCONT.createImage();
			else
				return super.getImage(element);
		} else
			return null;
	}

	public String getText(Object element) {
		String help;
		if (element instanceof RootNode) {
			RootNode node = (RootNode) element;
			//help = node.getName();
			help = filterName(node.getName());
			if (node.hasAdditionalName()) {
				help = help + " (" + node.getAdditionalName() + ")";
			}
			help = replaceAll(help, "$", ".");
			/*
			 * if (node.isFurtherBinding()) { help = help + "(F)"; }
			 */
			return help;
		} else if (element instanceof LinearNode) {
			LinearNode node = (LinearNode) element;
			return replaceAll(node.getName(), "$", ".");
		} else if (element instanceof String) {
			return (String) element;
		} else {
			return "unknown object";
		}
	}

	private String replaceAll(String source, String orig_val, String new_val) {
		try {
			String help = source;
			for (; help.lastIndexOf(orig_val) > 0;) {
				help = help.substring(0, help.lastIndexOf(orig_val)) + new_val
						+ help.substring(help.lastIndexOf(orig_val) + 1);
			}
			return help;
		} catch (Exception e) {
			System.out.println("Replacing '" + orig_val + "' with '" + new_val
					+ "' in '" + source + "'.");
			return source;
		}
	}
	
	private String filterName(String name) {
		try {
			String help = new String(name);
			int slashPos = name.lastIndexOf("/");
			int dollarPos = name.lastIndexOf("$");
			if (slashPos > 0)
				help = name.substring(slashPos + 1);
			if (dollarPos > slashPos) {
				help = name.substring(dollarPos + 1);
			}
			return help;
		} catch (Exception e) {
			System.out.println("Filtering names for Hierarchy View.");
			return "Error";
		}
	}
}