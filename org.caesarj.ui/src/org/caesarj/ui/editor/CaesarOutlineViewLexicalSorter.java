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
 * $Id: CaesarOutlineViewLexicalSorter.java,v 1.3 2005-03-24 12:31:55 meffert Exp $
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
			Integer categoryInteger;
			if(element instanceof CaesarProgramElementNode){
				CaesarProgramElementNode.Kind kind = ((CaesarProgramElementNode) element).getCaesarKind();
				categoryInteger = (Integer) categoryMap.get(kind);
				if(categoryInteger != null) {
					//return ((Integer) categoryMap.get(((CaesarProgramElementNode) element).getCaesarKind())).intValue();
					return categoryInteger.intValue();
				}
			}else if(element instanceof ProgramElementNode){
				ProgramElementNode.Kind pKind = ((ProgramElementNode) element).getProgramElementKind();
				categoryInteger = (Integer) categoryMap.get(pKind);
				if(categoryInteger != null) {
					//return ((Integer) categoryMap.get(((ProgramElementNode) element).getProgramElementKind())).intValue();
					return categoryInteger.intValue();
				}
			}
			return 999;
		} catch (Exception e) {
			return 999;
		}
	}
}
