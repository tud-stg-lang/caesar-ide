/*
 * Created on 06.12.2004
 */
package org.caesarj.ui.editor;

import java.util.HashMap;

import org.aspectj.asm.ProgramElementNode;
import org.caesarj.compiler.asm.CaesarProgramElementNode;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author meffert
 */
public class CaesarOutlineViewLexicalSorter extends ViewerSorter {
	protected static HashMap categoryMap;
	static {
		categoryMap = new HashMap();
		categoryMap.put(CaesarProgramElementNode.Kind.PACKAGE, new Integer(0));
		categoryMap.put(CaesarProgramElementNode.Kind.IMPORTS, new Integer(1));
		categoryMap.put(CaesarProgramElementNode.Kind.ASPECT, new Integer(5));
		categoryMap.put(CaesarProgramElementNode.Kind.INTERFACE, new Integer(6));
		categoryMap.put(CaesarProgramElementNode.Kind.CLASS, new Integer(7));
		categoryMap.put(CaesarProgramElementNode.Kind.VIRTUAL_CLASS, new Integer(8));
		categoryMap.put(CaesarProgramElementNode.Kind.FIELD, new Integer(10));
		categoryMap.put(CaesarProgramElementNode.Kind.CONSTRUCTOR, new Integer(11));
		categoryMap.put(CaesarProgramElementNode.Kind.METHOD, new Integer(12));
		categoryMap.put(CaesarProgramElementNode.Kind.ADVICE, new Integer(13));
		categoryMap.put(ProgramElementNode.Kind.CODE, new Integer(14));
	}
	
	public int category(Object element) {
		try {
			if(element instanceof CaesarProgramElementNode){
				return ((Integer) categoryMap.get(((CaesarProgramElementNode) element).getCaesarKind())).intValue();
			}else if(element instanceof ProgramElementNode){
				return ((Integer) categoryMap.get(((ProgramElementNode) element).getProgramElementKind())).intValue();
			}
			return 999;
		} catch (Exception e) {
			return 999;
		}
	}
}
