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
 * $Id: CaesarOutlineViewContentProvider.java,v 1.4 2005-04-11 09:04:00 thiago Exp $
 */

package org.caesarj.ui.editor;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.ProgramElement;
import org.caesarj.compiler.asm.CaesarProgramElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author meffert
 *
 * TODO [documentation]
 */
public class CaesarOutlineViewContentProvider implements ITreeContentProvider {

	private IProject project = null;
	
	public CaesarOutlineViewContentProvider(IProject project) {
		this.project = project;
	}
	
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	public Object[] getElements(Object inputElement) {
		Vector elements = new Vector();
		
		if (inputElement instanceof CaesarProgramElement){
			// add children
			elements.addAll(((CaesarProgramElement) inputElement).getChildren());
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
		if(parentElement instanceof CaesarProgramElement){
			//Logger.getLogger(this.getClass()).info("getChildren() called with instanceof CaesarProgramElementNode."  + parentElement.getClass().getName());
			// if parentElement is of kind PACKAGE dont add children!
			if(CaesarProgramElement.Kind.PACKAGE == ((CaesarProgramElement)parentElement).getCaesarKind()){
				
			}else if(CaesarProgramElement.Kind.IMPORTS == ((CaesarProgramElement)parentElement).getCaesarKind()){
				// remove the import java/lang
				Iterator it = ((CaesarProgramElement)parentElement).getChildren().iterator();
				while(it.hasNext()){
					CaesarProgramElement child = (CaesarProgramElement)it.next();
					if(child.getName().compareTo("java/lang") != 0){
						elements.add(child);
					}
				}
			}else{
				// add children
				// add all children except if child is of Kind ADVICE_REGISTRY, then add childs children to node
				Iterator it = ((CaesarProgramElement)parentElement).getChildren().iterator();
				while(it.hasNext()){
					Object child = it.next();
					if(child instanceof CaesarProgramElement){
						if(CaesarProgramElement.Kind.ADVICE_REGISTRY == ((CaesarProgramElement)child).getCaesarKind()){
							elements.addAll(((CaesarProgramElement)child).getChildren());
						}else{
							boolean addAsChild = true;
							if (CaesarProgramElement.Kind.METHOD == ((CaesarProgramElement)child).getCaesarKind()){
								if (((CaesarProgramElement)child).getName().equals("aspectOf")
										|| ((CaesarProgramElement)child).getName().indexOf('$') != -1)
								{
									// do not add child!
									addAsChild = false;
								}
							}
							if (CaesarProgramElement.Kind.FIELD == ((CaesarProgramElement)child).getCaesarKind()) {
								if (((CaesarProgramElement)child).getName().indexOf('$') != -1) {
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
				elements.addAll(((CaesarProgramElement)parentElement).getRelations());
				
				// Iterate through relations and set markers for all RelationNodes.
				it = ((CaesarProgramElement)parentElement).getRelations().iterator();
				while(it.hasNext()){
					Object next = it.next();
					if(next instanceof IRelationship){
						setMarkers((CaesarProgramElement)parentElement, (IRelationship)next);
					}
				}
			}
		}else if(parentElement instanceof ProgramElement){
			Logger.getLogger(this.getClass()).info("getChildren() called with instanceof ProgramElementNode." + 
			        ((ProgramElement)parentElement).getKind().toString());
			// add children
			elements.addAll(((ProgramElement)parentElement).getChildren());
			// add relations
			elements.addAll(((ProgramElement)parentElement).getRelations());
			
			// Iterate through relations and set markers for all RelationNodes.
			Iterator it = ((ProgramElement)parentElement).getRelations().iterator();
			while(it.hasNext()){
				Object next = it.next();
				if(next instanceof IRelationship){
					setMarkers((ProgramElement)parentElement, (IRelationship)next);
				}
			}
		}else if(parentElement instanceof IProgramElement){
			Logger.getLogger(this.getClass()).info("getChildren() called with instanceof StructureNode." + parentElement.getClass().getName());
			// add children
			elements.addAll(((IProgramElement)parentElement).getChildren());
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
		if(element instanceof IProgramElement){
			return ((IProgramElement) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof CaesarProgramElement){
			//Logger.getLogger(this.getClass()).info("hasChildren() called with instanceof CaesarProgramElement. " + element.getClass().getName());
			if(CaesarProgramElement.Kind.PACKAGE == ((CaesarProgramElement)element).getCaesarKind()){
				return false;
			}else if(CaesarProgramElement.Kind.IMPORTS == ((CaesarProgramElement)element).getCaesarKind()){
				// the import java/lang always exists => do not display it.
				return ((CaesarProgramElement)element).getChildren().size() > 1;
			}
			System.out.println(((CaesarProgramElement)element).getCaesarKind().toString() + " - Relations: " + ((CaesarProgramElement)element).getRelations().size());
			// return children + relations > 0
			return ((CaesarProgramElement)element).getChildren().size() > 0
						|| ((CaesarProgramElement)element).getRelations().size() > 0;
		}else if(element instanceof ProgramElement){
			// if element is instanceof ProgramElementNode (Kind == CODE?) 
			// return sizeof children + sizeof Relations
			return ((ProgramElement)element).getChildren().size() > 0 
						|| ((ProgramElement)element).getRelations().size() > 0;
		}else if(element instanceof IProgramElement){
			// if element is instanceof ProgramElementNode (Kind == CODE?) 
			// return sizeof children + sizeof Relations
			return ((IProgramElement)element).getChildren().size() > 0;
		}else{
			System.out.println("hasChildren(): Not a ProgramElementNode");
		}
		return false;
	}
	
	/**
	 * Used to add Markers to the editor.
	 * @param node - CaesarProgramElement representing the Position where to add the Marker
	 * @param relation - defines the Marker
	 */
	private void setMarkers(ProgramElement node, IRelationship relation){
		Logger.getLogger(this.getClass()).info("setMarkers() for relation node");
		
		//copied from ui.CaesarProgramElement
		/*
		 * TODO LINKNODES
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
				.getSourceFile().getAbsolutePath(), project);
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
		*/
	}
}
