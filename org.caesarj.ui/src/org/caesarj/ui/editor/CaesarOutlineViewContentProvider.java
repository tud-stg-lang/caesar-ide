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
 * $Id: CaesarOutlineViewContentProvider.java,v 1.2 2005-01-24 16:57:22 aracic Exp $
 */

package org.caesarj.ui.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.aspectj.asm.LinkNode;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.RelationNode;
import org.aspectj.asm.StructureNode;
import org.caesarj.compiler.asm.CaesarProgramElementNode;
import org.caesarj.ui.builder.Builder;
import org.caesarj.ui.marker.AdviceMarker;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * @author meffert
 *
 * TODO [documentation]
 */
public class CaesarOutlineViewContentProvider implements ITreeContentProvider {

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		Vector elements = new Vector();
		
		if (inputElement instanceof CaesarProgramElementNode){
			// add children
			elements.addAll(((CaesarProgramElementNode) inputElement).getChildren());
		}
		else{
			if(inputElement != null){
				Logger.getLogger(this.getClass()).info("getElements() called with instanceof " + inputElement.getClass().getName());
			}else{
				Logger.getLogger(this.getClass()).info("getElements() called with NULL.");
			}
		}
		return elements.toArray();
	}

	public Object[] getChildren(Object parentElement) {
		//Logger.getLogger(this.getClass()).info("getChildren()" + parentElement);
		Vector elements = new Vector();
		if(parentElement instanceof CaesarProgramElementNode){
			//Logger.getLogger(this.getClass()).info("getChildren() called with instanceof CaesarProgramElementNode."  + parentElement.getClass().getName());
			// if parentElement is of kind PACKAGE dont add children!
			if(CaesarProgramElementNode.Kind.PACKAGE == ((CaesarProgramElementNode)parentElement).getCaesarKind()){
				
			}else if(CaesarProgramElementNode.Kind.IMPORTS == ((CaesarProgramElementNode)parentElement).getCaesarKind()){
				// remove the import java/lang
				Iterator it = ((CaesarProgramElementNode)parentElement).getChildren().iterator();
				while(it.hasNext()){
					CaesarProgramElementNode child = (CaesarProgramElementNode)it.next();
					if(child.getName().compareTo("java/lang") != 0){
						elements.add(child);
					}
				}
			}else{
				// add children
				// add all children except if child is of Kind ADVICE_REGISTRY, then add childs children to node
				Iterator it = ((CaesarProgramElementNode)parentElement).getChildren().iterator();
				while(it.hasNext()){
					Object child = it.next();
					if(child instanceof CaesarProgramElementNode){
						if(CaesarProgramElementNode.Kind.ADVICE_REGISTRY == ((CaesarProgramElementNode)child).getCaesarKind()){
							elements.addAll(((CaesarProgramElementNode)child).getChildren());
						}else{
							boolean addAsChild = true;
							if (CaesarProgramElementNode.Kind.METHOD == ((CaesarProgramElementNode)child).getCaesarKind()){
								if (((CaesarProgramElementNode)child).getName().equals("aspectOf")
										|| ((CaesarProgramElementNode)child).getName().indexOf('$') != -1)
								{
									// do not add child!
									addAsChild = false;
								}
							}
							if (CaesarProgramElementNode.Kind.FIELD == ((CaesarProgramElementNode)child).getCaesarKind()) {
								if (((CaesarProgramElementNode)child).getName().indexOf('$') != -1) {
									// do not add child
									addAsChild = false;
								}
							}
							if(addAsChild) elements.add(child);
						}
					}else{
						elements.add(child);
					}
				}
				//elements.addAll(((CaesarProgramElementNode)parentElement).getChildren());
				
				// add relations
				elements.addAll(((CaesarProgramElementNode)parentElement).getRelations());
				
				// Iterate through relations and set markers for all RelationNodes.
				it = ((CaesarProgramElementNode)parentElement).getRelations().iterator();
				while(it.hasNext()){
					Object next = it.next();
					if(next instanceof RelationNode){
						setMarkers((CaesarProgramElementNode)parentElement, (RelationNode)next);
					}
				}
			}
		}else if(parentElement instanceof ProgramElementNode){
			Logger.getLogger(this.getClass()).info("getChildren() called with instanceof ProgramElementNode." + ((ProgramElementNode)parentElement).getProgramElementKind().toString());
			// add children
			elements.addAll(((ProgramElementNode)parentElement).getChildren());
			// add relations
			elements.addAll(((ProgramElementNode)parentElement).getRelations());
			
			// Iterate through relations and set markers for all RelationNodes.
			Iterator it = ((ProgramElementNode)parentElement).getRelations().iterator();
			while(it.hasNext()){
				Object next = it.next();
				if(next instanceof RelationNode){
					setMarkers((ProgramElementNode)parentElement, (RelationNode)next);
				}
			}
		}else if(parentElement instanceof StructureNode){
			Logger.getLogger(this.getClass()).info("getChildren() called with instanceof StructureNode." + parentElement.getClass().getName());
			// add children
			elements.addAll(((StructureNode)parentElement).getChildren());
		}else{
			if(parentElement != null){
				Logger.getLogger(this.getClass()).info("getChildren() called with instanceof " + parentElement.getClass().getName());
			}else{
				Logger.getLogger(this.getClass()).info("getChildren() called with NULL.");
			}
		}
		return elements.toArray();
	}

	public Object getParent(Object element) {
		Logger.getLogger(this.getClass()).info("getParent()" + element);
		if(element instanceof StructureNode){
			return ((StructureNode) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof CaesarProgramElementNode){
			//Logger.getLogger(this.getClass()).info("hasChildren() called with instanceof CaesarProgramElementNode. " + element.getClass().getName());
			if(CaesarProgramElementNode.Kind.PACKAGE == ((CaesarProgramElementNode)element).getCaesarKind()){
				return false;
			}else if(CaesarProgramElementNode.Kind.IMPORTS == ((CaesarProgramElementNode)element).getCaesarKind()){
				// the import java/lang always exists => do not display it.
				return ((CaesarProgramElementNode)element).getChildren().size() > 1;
			}
			System.out.println(((CaesarProgramElementNode)element).getCaesarKind().toString() + " - Relations: " + ((CaesarProgramElementNode)element).getRelations().size());
			// return children + relations > 0
			return ((CaesarProgramElementNode)element).getChildren().size() > 0
						|| ((CaesarProgramElementNode)element).getRelations().size() > 0;
		}else if(element instanceof ProgramElementNode){
			// if element is instanceof ProgramElementNode (Kind == CODE?) 
			// return sizeof children + sizeof Relations
			return ((ProgramElementNode)element).getChildren().size() > 0 
						|| ((ProgramElementNode)element).getRelations().size() > 0;
		}else if(element instanceof StructureNode){
			// if element is instanceof ProgramElementNode (Kind == CODE?) 
			// return sizeof children + sizeof Relations
			return ((StructureNode)element).getChildren().size() > 0;
		}else{
			System.out.println("hasChildren(): Not a ProgramElementNode");
		}
		return false;
	}
	
	/**
	 * Used to add Markers to the editor.
	 * @param node - CaesarProgramElementNode representing the Position where to add the Marker
	 * @param relation - defines the Marker
	 */
	private void setMarkers(ProgramElementNode node, RelationNode relation){
		Logger.getLogger(this.getClass()).info("setMarkers() for relation node");
		
		//copied from ui.CaesarProgramElementNode
		
		Object[] nodes = relation.getChildren().toArray();
		HashMap args = new HashMap();
		String messageLocal = relation.getName().toUpperCase()
				+ ": "; //$NON-NLS-1$
		LinkNode lNode[] = new LinkNode[nodes.length];
		String tempString, className, adviceName;
		for (int i = 0; i < nodes.length; i++) {
			lNode[i] = (LinkNode) nodes[i];
			try {
				tempString = lNode[i].toLongString();
			} catch (Exception e) {
				continue;
			}
			tempString = tempString.substring(tempString.lastIndexOf(']') + 1);
			try {
				className = tempString
						.substring(0, tempString.lastIndexOf(':'));
				adviceName = tempString.substring(
						tempString.lastIndexOf(':') + 2,
						tempString.length() - 1);
				messageLocal += "!" + adviceName + ":" + className + "!  "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				args.put(AdviceMarker.ID, "AdviceLink"); //$NON-NLS-1$
			} catch (Exception e) {
				messageLocal += "!" + tempString.substring(1, tempString.length() - 1) + "()!  "; //$NON-NLS-1$//$NON-NLS-2$
				args.put(AdviceMarker.ID, "MethodeLink"); //$NON-NLS-1$
			}
		}

		IResource resource = ProjectProperties.findResource(node.getSourceLocation()
				.getSourceFile().getAbsolutePath(), Builder
				.getProjectForSourceLocation(node.getSourceLocation()));
		args.put(IMarker.LINE_NUMBER,
				new Integer(node.getSourceLocation().getLine()));
		args.put(IMarker.MESSAGE, messageLocal);
		args.put(AdviceMarker.LINKS, lNode);
		try {
			MarkerUtilities.createMarker(resource, args,
					AdviceMarker.ADVICEMARKER);
		} catch (CoreException e) {
			Logger.getLogger(this.getClass()).error("FEHLER BEIM MARKER ERZEUGEN", e); //$NON-NLS-1$
		}
	}
}
