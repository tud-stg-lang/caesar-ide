/*
 * Created on 22.11.2004
 */
package org.caesarj.ui.editor;

import java.util.Iterator;

import org.aspectj.asm.LinkNode;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.RelationNode;
import org.aspectj.asm.StructureNode;
import org.caesarj.compiler.asm.CaesarProgramElementNode;
import org.caesarj.ui.CaesarPluginImages;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author meffert 
 *
 * TODO [documentation]
 */
public class CaesarOutlineViewLabelProvider extends LabelProvider {

	public String getText(Object element) {
		if(element instanceof CaesarProgramElementNode){
			return TextFactory.getText((CaesarProgramElementNode)element);
		}else if(element instanceof StructureNode){
			return TextFactory.getText((StructureNode)element);
			//return ((StructureNode)element).getName() + " (Class:" + element.getClass().getName() + ")";
		}
		return "Not a StructureNode";
	}
	
	public Image getImage(Object element) {
		if(element instanceof CaesarProgramElementNode){
			return ImageFactory.getImage((CaesarProgramElementNode)element);
		}else if(element instanceof StructureNode){
			return ImageFactory.getImage(((StructureNode)element));
		}
		return null;
	}
	
	public static class TextFactory {
		public static String getText(StructureNode node){
			String text = node.getName();
			if(node instanceof ProgramElementNode){
				if(ProgramElementNode.Kind.CODE != ((ProgramElementNode)node).getProgramElementKind()){
					text += "(" + ((ProgramElementNode)node).getProgramElementKind().toString() + ")";
				}
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
			}
			return text;
		}
		public static String getText(CaesarProgramElementNode node){
			String text = node.getName();
			if(CaesarProgramElementNode.Kind.PACKAGE == node.getCaesarKind()){
				// PACKAGE node
				
			}else if(CaesarProgramElementNode.Kind.IMPORTS == node.getCaesarKind()){
				// IMPORTS Node
				
			}else if(CaesarProgramElementNode.Kind.PACKAGE_IMPORT == node.getCaesarKind()){
				// PACKAGE IMPORT Node
				text = text.replace('/', '.') + ".*";
			}else if(CaesarProgramElementNode.Kind.CLASS_IMPORT == node.getCaesarKind()){
				// CLASS IMPORT Node
				text = text.replace('/', '.');
			}else if(CaesarProgramElementNode.Kind.CLASS == node.getCaesarKind()){
				// CLASS Node
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
			}else if(CaesarProgramElementNode.Kind.VIRTUAL_CLASS == node.getCaesarKind()){
				// VIRTUAL CLASS Node
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
			}else if(CaesarProgramElementNode.Kind.ASPECT == node.getCaesarKind()){
				// ASPECT Node
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
			}else if(CaesarProgramElementNode.Kind.INTERFACE == node.getCaesarKind()){
				// INTERFACE node
				
			}else if(CaesarProgramElementNode.Kind.CONSTRUCTOR == node.getCaesarKind()){
				// CONSTRUCTOR Node
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
				
				text += "(";
				// iterate through parameters to extract parameter informations
				Iterator it = node.getParameters().iterator();
				while(it.hasNext()){
					CaesarProgramElementNode child = (CaesarProgramElementNode)it.next();
					if(! child.getName().equals("_$outer")){
						text += extractClassName(child.getType()) /*+ " " + child.getName()*/;
						if(it.hasNext()) text += ", ";
					}
				}
				text += ")";
			}else if(CaesarProgramElementNode.Kind.METHOD == node.getCaesarKind()){
				// METHOD Node
				// TODO [question]: are there any generated _Impl methods?
				if(text.lastIndexOf("_Impl") > -1)
					text = text.substring(0, text.lastIndexOf("_Impl"));
				
				text += "(";
				// iterate through parameters to extract parameter informations
				Iterator it = node.getParameters().iterator();
				while(it.hasNext()){
					CaesarProgramElementNode child = (CaesarProgramElementNode)it.next();
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
			}else if(CaesarProgramElementNode.Kind.FIELD == node.getCaesarKind()){
				// FIELD node
				text += ": " + extractClassName(node.getType());
			}else if(CaesarProgramElementNode.Kind.ADVICE == node.getCaesarKind()){
				// ADVICE node
				
			}else{
				// mark not yet processed elements.
				text += " (!)" + node.getCaesarKind().toString();
			}
			return text;
		}
		
		/**
		 * Extracts the classname of a string returned by CaesarProgramElementNode.getType().
		 * Removes the path/package information and the semikolon at the end.
		 * @param input - string returned by CaesarProgramElementNode.getType()
		 * @return
		 */
		protected static String extractClassName(String input){
			// TODO [refactoring]: should this be moved to CaesarProgramElementNode or AsmBuilder?
			String className = input;
			if(input.lastIndexOf('/') > -1){
				className = input.substring(input.lastIndexOf('/') + 1, input.length() - 1);
			}else if(input.lastIndexOf('.') > -1){
				className = input.substring(input.lastIndexOf('.') + 1, input.length());
			}
			return className;
		}
	}
	
	public static class ImageFactory {
		public static Image getImage(StructureNode node){
			Image image = null;
			if(node instanceof ProgramElementNode){
				// only return image if kind is CODE
				if(ProgramElementNode.Kind.CODE == ((ProgramElementNode)node).getProgramElementKind()){
					image = new CaesarImageDescriptor((ProgramElementNode)node, CaesarPluginImages.DESC_CODE).createImage();
				}
			}else if(node instanceof LinkNode){
				ProgramElementNode pNode = ((LinkNode) node).getProgramElementNode();
				if (pNode instanceof CaesarProgramElementNode && ((CaesarProgramElementNode)pNode).getCaesarKind() == CaesarProgramElementNode.Kind.ADVICE) {
			/*return new CaesarElementImageDescriptor(
					CaesarPluginImages.DESC_JOINPOINT_BACK, null,
					BIG_SIZE).createImage();*/
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_JOINPOINT_BACK).createImage();
				} else {
			/*return new CaesarElementImageDescriptor(
					CaesarPluginImages.DESC_JOINPOINT_FORWARD,
					null, BIG_SIZE).createImage();*/
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_JOINPOINT_FORWARD).createImage();
				}
			}else if(node instanceof RelationNode){
				/*return new CaesarElementImageDescriptor(
						CaesarPluginImages.DESC_ADVICE, null, BIG_SIZE)
						.createImage();*/
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_ADVICE).createImage();
			}
			return image;
		}
		public static Image getImage(CaesarProgramElementNode node) {
			Image image = null;
			
			if(CaesarProgramElementNode.Kind.PACKAGE == node.getCaesarKind()){
				// PACKAGE node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OUT_PACKAGE).createImage();
			}else if(CaesarProgramElementNode.Kind.IMPORTS == node.getCaesarKind()){
				// IMPORTS node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OUT_IMPORTS).createImage();
			}else if(CaesarProgramElementNode.Kind.PACKAGE_IMPORT == node.getCaesarKind()){
				// PACKAGE IMPORT node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_IMPORTS).createImage();
			}else if(CaesarProgramElementNode.Kind.CLASS_IMPORT == node.getCaesarKind()){
				// CLASS IMPORT node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_IMPORTS).createImage();
			}else if(CaesarProgramElementNode.Kind.CLASS == node.getCaesarKind()){
				// CLASS Node
				if(CaesarProgramElementNode.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC CLASS
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_CLASS_PUBLIC).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED CLASS
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_CLASS_PROTECTED).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE CLASS
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_CLASS_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT).createImage();
				}
			}else if(CaesarProgramElementNode.Kind.VIRTUAL_CLASS == node.getCaesarKind()){
				// VIRTUAL CLASS Node
				if(CaesarProgramElementNode.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC CLASS
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED CLASS
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PROTECTED).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE CLASS
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_OBJS_INNER_CCLASS_DEFAULT).createImage();
				}
			}else if(CaesarProgramElementNode.Kind.ASPECT == node.getCaesarKind()){
				// ASPECT Node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_ASPECT).createImage();
			}else if(CaesarProgramElementNode.Kind.INTERFACE == node.getCaesarKind()){
				// INTERFACE node
				if(CaesarProgramElementNode.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC INTERFACE
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED INTERFACE
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE INTERFACE
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT).createImage();
				}
			}else if(CaesarProgramElementNode.Kind.CONSTRUCTOR == node.getCaesarKind()){
				// CONSTRUCTOR Node
				if(CaesarProgramElementNode.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC CONSTRUCTOR
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PUBLIC).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED CONSTRUCTOR
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PROTECTED).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE CONSTRUCTOR
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_DEFAULT).createImage();
				}
			}else if(CaesarProgramElementNode.Kind.METHOD == node.getCaesarKind()){
				// METHOD Node
				if(CaesarProgramElementNode.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC METHOD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PUBLIC).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED METHOD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PROTECTED).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE METHOD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_MISC_DEFAULT).createImage();
				}
			}else if(CaesarProgramElementNode.Kind.FIELD == node.getCaesarKind()){
				// FIELD node
				if(CaesarProgramElementNode.Accessibility.PUBLIC == node.getAccessibility()){
					// PUBLIC FIELD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_FIELD_PUBLIC).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PROTECTED == node.getAccessibility()){
					// PROTECTED FIELD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_FIELD_PROTECTED).createImage();
				}else if(CaesarProgramElementNode.Accessibility.PRIVATE == node.getAccessibility()){
					// PRIVATE FIELD
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_FIELD_PRIVATE).createImage();
				}else{
					// DEFAULT image
					image = new CaesarImageDescriptor(node, JavaPluginImages.DESC_FIELD_DEFAULT).createImage();
				}
			}else if(CaesarProgramElementNode.Kind.ADVICE == node.getCaesarKind()){
				// ADVICE node
				image = new CaesarImageDescriptor(node, CaesarPluginImages.DESC_ADVICE_NODE, false).createImage();
			}
			return image;
		}
	}
}
