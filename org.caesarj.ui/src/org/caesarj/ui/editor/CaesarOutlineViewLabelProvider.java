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
 * $Id: CaesarOutlineViewLabelProvider.java,v 1.4 2005-04-22 07:48:32 thiago Exp $
 */

package org.caesarj.ui.editor;

import java.util.Iterator;

import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.internal.ProgramElement;
import org.caesarj.compiler.asm.CaesarProgramElement;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.editor.model.LinkNode;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author meffert 
 *
 * TODO [documentation]
 */
public class CaesarOutlineViewLabelProvider extends LabelProvider {

    /**
     * 
     */
	public String getText(Object element) {
		if(element instanceof CaesarProgramElement) {
			return TextFactory.getText((CaesarProgramElement)element);
		} else if (element instanceof LinkNode) {
		    return TextFactory.getText((LinkNode) element);
		} else if (element instanceof IProgramElement) {
		    return TextFactory.getText((IProgramElement) element);
		}
		return "Unknown Node";
	}
	
	/**
	 * 
	 */
	public Image getImage(Object element) {
		if(element instanceof CaesarProgramElement){
			return ImageFactory.getImage((CaesarProgramElement)element);
		} else if (element instanceof LinkNode) {
		    return ImageFactory.getImage((LinkNode) element);
		} else if (element instanceof IProgramElement) {
		    return ImageFactory.getImage((IProgramElement) element);
		}
		return null;
	}
	
	
	/**
	 * 
	 * TODO - Comments
	 *
	 *
	 */
	public static class TextFactory {
	    
		public static String getText(IProgramElement node){
			String text = node.getName();
			if(ProgramElement.Kind.CODE != ((ProgramElement)node).getKind()){
			    text += "(" + ((ProgramElement)node).getKind().toString() + ")";
			}
			return text;
		}
		
		public static String getText(LinkNode node) {
		    
		    if (node.getType() == LinkNode.LINK_NODE_RELATIONSHIP) {
		        return node.getRelationship().getName();
		    } else {
		        IProgramElement targ = node.getTargetElement();
		    
			    String className = targ.getParent().getName();
			    int end = className.lastIndexOf("_Impl");
			    if (end < 0) {
			        end = className.lastIndexOf(".java");
			    }
			    if (end > -1) {
			        className = className.substring(0, end);
			    }
			    String text = className + " : " + targ.getName();
			    // Add the method sign
			    if (targ.getKind() == IProgramElement.Kind.METHOD) {
			        text += "()";
			    }
		    	return text;
		    }
				/*
				 * TODO CHECK LINKNODE!
			}else if(node instanceof LinkNode){
				String classname = node.toString(); 
				// node.toString() is of form: "[aspect] PricingDeployment_Impl: around"
				classname = classname.substring(classname.lastIndexOf(']') + 2);
				if(classname.lastIndexOf(':') > -1){
					classname = classname.substring(0, classname.lastIndexOf(':'));
					if(classname.lastIndexOf("_Impl") > -1){
						classname = classname.substring(0, classname.lastIndexOf("_Impl"));
					}
				}else{
					if(classname.lastIndexOf("_Impl") > -1){
						classname = classname.substring(0, classname.lastIndexOf("_Impl"));
					}
					classname += "()";
				}
				text += ": " + classname;
			}*/
				
			//return text;
		}
		public static String getText(CaesarProgramElement node){
			String text = node.getName();
			if(CaesarProgramElement.Kind.PACKAGE == node.getCaesarKind()){
				// PACKAGE node
				
			}else if(CaesarProgramElement.Kind.IMPORTS == node.getCaesarKind()){
				// IMPORTS Node
				
			}else if(CaesarProgramElement.Kind.PACKAGE_IMPORT == node.getCaesarKind()){
				// PACKAGE IMPORT Node
				text = text.replace('/', '.') + ".*";
			}else if(CaesarProgramElement.Kind.CLASS_IMPORT == node.getCaesarKind()){
				// CLASS IMPORT Node
				text = text.replace('/', '.');
			}else if(CaesarProgramElement.Kind.CLASS == node.getCaesarKind()){
				// CLASS Node
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
			}else if(CaesarProgramElement.Kind.VIRTUAL_CLASS == node.getCaesarKind()){
				// VIRTUAL CLASS Node
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
			}else if(CaesarProgramElement.Kind.ASPECT == node.getCaesarKind()){
				// ASPECT Node
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
			}else if(CaesarProgramElement.Kind.INTERFACE == node.getCaesarKind()){
				// INTERFACE node
				
			}else if(CaesarProgramElement.Kind.CONSTRUCTOR == node.getCaesarKind()){
				// CONSTRUCTOR Node
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
				
				text += "(";
				// iterate through parameters to extract parameter informations
				Iterator it = node.getParameters().iterator();
				while(it.hasNext()){
					CaesarProgramElement child = (CaesarProgramElement)it.next();
					if(! child.getName().equals("_$outer")){
						text += extractClassName(child.getType()) /*+ " " + child.getName()*/;
						if(it.hasNext()) text += ", ";
					}
				}
				text += ")";
			}else if(CaesarProgramElement.Kind.METHOD == node.getCaesarKind()){
				// METHOD Node
				// TODO [question]: are there any generated _Impl methods?
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
				
				text += "(";
				// iterate through parameters to extract parameter informations
				Iterator it = node.getParameters().iterator();
				while(it.hasNext()){
					CaesarProgramElement child = (CaesarProgramElement)it.next();
					text += extractClassName(child.getType()) /*+ " " + child.getName()*/;
					if(it.hasNext()) text += ", ";
				}
				text += ")";
				
				// add return type information
				String returnType = extractClassName(node.getReturnType());
				if(returnType.equals("V")){
					returnType = "void";
				}
				text += ": " + returnType;
			}else if(CaesarProgramElement.Kind.FIELD == node.getCaesarKind()){
				// FIELD node
				text += ": " + extractClassName(node.getType());
			}else if(CaesarProgramElement.Kind.ADVICE == node.getCaesarKind()){
				// ADVICE node

			} else if (CaesarProgramElement.Kind.POINTCUT == node.getCaesarKind()) {
			    // POINTCUT node
			    text += ": Pointcut";
			}else{
				// mark not yet processed elements.
				text += " (!)" + node.getCaesarKind().toString();
			}
			return text;
		}
		
		/**
		 * Extracts the classname of a string returned by CaesarProgramElement.getType().
		 * Removes the path/package information and the semikolon at the end.
		 * @param input - string returned by CaesarProgramElement.getType()
		 * @return
		 */
		protected static String extractClassName(String input){
			// TODO [refactoring]: should this be moved to CaesarProgramElement or AsmBuilder?
			String className = input;
			if(input.lastIndexOf('/') > -1){
				className = input.substring(input.lastIndexOf('/') + 1, input.length() - 1);
			}else if(input.lastIndexOf('.') > -1){
				className = input.substring(input.lastIndexOf('.') + 1, input.length());
			}
			return className;
		}
	}
	
	/**
	 * 
	 * TODO - Comments
	 *
	 *
	 */
	public static class ImageFactory {
		public static Image getImage(IProgramElement node) {
			Image image = null;
			
			// only return image if kind is CODE
			if(ProgramElement.Kind.CODE == node.getKind()) {
				image = new CaesarImageDescriptor((ProgramElement)node, CaesarPluginImages.DESC_CODE).createImage();
			}
			return image;
		}
	
		public static Image getImage(LinkNode node) {
		    
			/*
			 * TODO CHECK LINKNODE!!
			 
			}else if(node instanceof LinkNode){
				ProgramElement pNode = ((LinkNode) node).getProgramElement();
				if (pNode instanceof CaesarProgramElement && ((CaesarProgramElement)pNode).getCaesarKind() == CaesarProgramElement.Kind.ADVICE) {

					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_JOINPOINT_BACK).createImage();
				} else {
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_JOINPOINT_FORWARD).createImage();
				}
				/*
				 * TODO CHECK IT.. it was instanceof RelationNode
			}else if(node instanceof IRelationship){
				//return new CaesarElementImageDescriptor(
				//		CaesarPluginImages.DESC_ADVICE, null, BIG_SIZE)
				//		.createImage();
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_ADVICE).createImage();
			}*/
		    if (node.getType() == LinkNode.LINK_NODE_RELATIONSHIP) {
		        return new CaesarImageDescriptor(node, CaesarPluginImages.DESC_ADVICE).createImage();
		    } else {
		        if (node.getRelationship().getName().equals("advises")) {
		            return new CaesarImageDescriptor(node, CaesarPluginImages.DESC_JOINPOINT_FORWARD).createImage();
		        } else {
		            return new CaesarImageDescriptor(node, CaesarPluginImages.DESC_JOINPOINT_BACK).createImage();
		        }
		    }
		}
		public static Image getImage(CaesarProgramElement node) {
			Image image = null;
			
			if(CaesarProgramElement.Kind.PACKAGE == node.getCaesarKind()){
				// PACKAGE node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OUT_PACKAGE).createImage();
			}else if(CaesarProgramElement.Kind.IMPORTS == node.getCaesarKind()){
				// IMPORTS node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OUT_IMPORTS).createImage();
			}else if(CaesarProgramElement.Kind.PACKAGE_IMPORT == node.getCaesarKind()){
				// PACKAGE IMPORT node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_IMPORTS).createImage();
			}else if(CaesarProgramElement.Kind.CLASS_IMPORT == node.getCaesarKind()){
				// CLASS IMPORT node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_IMPORTS).createImage();
			}else if(CaesarProgramElement.Kind.CLASS == node.getCaesarKind()){
				// CLASS Node
				if(CaesarProgramElement.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC CLASS
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_CLASS_PUBLIC).createImage();
				}else if(CaesarProgramElement.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED CLASS
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_CLASS_PROTECTED).createImage();
				}else if(CaesarProgramElement.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE CLASS
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_CLASS_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT).createImage();
				}
			}else if(CaesarProgramElement.Kind.VIRTUAL_CLASS == node.getCaesarKind()){
				// VIRTUAL CLASS Node
				if(CaesarProgramElement.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC CLASS
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC).createImage();
				}else if(CaesarProgramElement.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED CLASS
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PROTECTED).createImage();
				}else if(CaesarProgramElement.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE CLASS
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OBJS_INNER_CCLASS_DEFAULT).createImage();
				}
			}else if(CaesarProgramElement.Kind.ASPECT == node.getCaesarKind()){
				// ASPECT Node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_ASPECT).createImage();
			}else if(CaesarProgramElement.Kind.INTERFACE == node.getCaesarKind()){
				// INTERFACE node
				if(CaesarProgramElement.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC INTERFACE
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC).createImage();
				}else if(CaesarProgramElement.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED INTERFACE
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED).createImage();
				}else if(CaesarProgramElement.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE INTERFACE
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT).createImage();
				}
			}else if(CaesarProgramElement.Kind.CONSTRUCTOR == node.getCaesarKind()){
				// CONSTRUCTOR Node
				if(CaesarProgramElement.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC CONSTRUCTOR
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PUBLIC).createImage();
				}else if(CaesarProgramElement.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED CONSTRUCTOR
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PROTECTED).createImage();
				}else if(CaesarProgramElement.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE CONSTRUCTOR
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_DEFAULT).createImage();
				}
			}else if(CaesarProgramElement.Kind.METHOD == node.getCaesarKind()){
				// METHOD Node
				if(CaesarProgramElement.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC METHOD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PUBLIC).createImage();
				}else if(CaesarProgramElement.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED METHOD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PROTECTED).createImage();
				}else if(CaesarProgramElement.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE METHOD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_DEFAULT).createImage();
				}
			}else if(CaesarProgramElement.Kind.FIELD == node.getCaesarKind()){
				// FIELD node
				if(CaesarProgramElement.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC FIELD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_FIELD_PUBLIC).createImage();
				}else if(CaesarProgramElement.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED FIELD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_FIELD_PROTECTED).createImage();
				}else if(CaesarProgramElement.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE FIELD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_FIELD_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_FIELD_DEFAULT).createImage();
				}
			}else if(CaesarProgramElement.Kind.ADVICE == node.getCaesarKind()){
				// ADVICE node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_ADVICE_NODE, false).createImage();
			}
			return image;
		}
	}
}
