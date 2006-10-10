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
 * $Id: AdviceMarkerGenerator.java,v 1.2 2006-10-10 22:05:17 gasiunas Exp $
 */

package org.caesarj.ui.marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.asm.HierarchyWalker;
import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IProgramElement;
import org.caesarj.compiler.asm.CaesarProgramElement;
import org.caesarj.compiler.asm.LinkNode;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Generate advice markers on all files of a project
 * 
 * @author Vaidas Gasiunas
 */
public class AdviceMarkerGenerator extends HierarchyWalker {

	IProject project = null;
    
	public void generateMarkers(IProject project, IHierarchy hierarchy) {
		this.project = project;
	    this.process(hierarchy.getRoot());	    
	}
	
	/**
	 * Print the node and advance the depth
	 */
	public IProgramElement process(IProgramElement node) {
	    // Print the node
		if (node instanceof CaesarProgramElement) {
			// Avoid visiting contents of externalized classes twice
			if (((CaesarProgramElement)node).getCaesarKind()
					 .equals(CaesarProgramElement.Kind.EXTERNAL_COLLABORATION)) {
				return node;
			}
		}
		return super.process(node);
	}
	
	/**
	 * Print the node and advance the depth
	 */
	public void preProcess(IProgramElement node) {
	    // Print the node
	    setMarkers(node);
	}
	
	/**
	 * Used to add Markers to the editor.
	 * @param node - CaesarProgramElement representing the Position where to add the Marker
	 * @param relation - defines the Marker
	 */
	private void setMarkers(IProgramElement parent){
		Logger.getLogger(this.getClass()).info("setMarkers() for relation node");
		
		String messageLocal = "";
		HashMap args = new HashMap();
		List lElems = new ArrayList();
		
		Iterator i = parent.getChildren().iterator();
		while(i.hasNext()) {
	        
	        IProgramElement elem = (IProgramElement) i.next();
	        if (elem instanceof LinkNode) {
	            // Add if it is a link node
	        	LinkNode node = (LinkNode)elem;
	        	
	        	Iterator i2 = node.getChildren().iterator();
	    		while(i2.hasNext()) {
	    			
	    			IProgramElement elem2 = (IProgramElement) i2.next();
	    	        if (elem2 instanceof LinkNode) {
	    	            // Add if it is a link node
	    	        	IProgramElement target = ((LinkNode)elem2).getTargetElement();
	    	        	
	    	        	//if (!lElems.contains(target)) {
		    	        	if (target.getKind().equals(IProgramElement.Kind.ADVICE)) {
		    	        		args.put(AdviceMarker.ID, "AdviceLink");
		    	        	}
		    	        	else {
		    	        		args.put(AdviceMarker.ID, "MethodeLink");
		    	        	}
		    	        	
		    	        	if (!messageLocal.equals("")) {
		    	        		messageLocal += ", ";
		    	        	}
		    	        	
		    	        	if (target.getParent() != null) {
		    	        		String parentName = target.getParent().getName();
		    	        		parentName = parentName.replaceAll("_Impl.*", "");
		    	        		messageLocal += parentName + "." + target.getName();
		    	        	}
		    	        	else {
		    	        		messageLocal += target.getName();
		    	        	}
		    	        	lElems.add(target);
	    	        	//}
	    	        } 
	    		}
	        }
	    }
		
		if (!lElems.isEmpty()) {
			IResource resource = ProjectProperties.findResource(parent.getSourceLocation()
					.getSourceFile().getAbsolutePath(), project);
			args.put(IMarker.LINE_NUMBER,
					new Integer(parent.getSourceLocation().getLine()));
			args.put(IMarker.MESSAGE, messageLocal);
			args.put(AdviceMarker.LINKS, lElems.toArray(new IProgramElement[0]));
			try {
				IMarker marker = resource.createMarker(AdviceMarker.ADVICEMARKER);
				marker.setAttributes(args);
			} catch (CoreException e) {
				Logger.getLogger(this.getClass()).error("FEHLER BEIM MARKER ERZEUGEN", e); //$NON-NLS-1$
			}
		}
	}

	
}