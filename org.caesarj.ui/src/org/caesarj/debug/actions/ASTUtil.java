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
package org.caesarj.debug.actions;

import java.util.Vector;

import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.visitor.VisitorSupport;

/**
 * A utility class for the abstract syntax tree.
 *  
 * @author meffert
 */
public class ASTUtil {

	BreakpointAstVisitor visitor;
	
	public ASTUtil(JCompilationUnit astRoot, int lineNumber){
		visitor = new BreakpointAstVisitor(lineNumber);
		astRoot.accept(new VisitorSupport(visitor));
	}
	
	public boolean canSetLineBreakpoint(){
		return getBreakpointableElements().size() > 0;
	}
	
	public boolean canSetMethodBreakpoint(){
		return getMethodDeclaration() != null;
	}
	
	public boolean canSetWatchpoint(){
		return getFieldDeclaration() != null;
	}
	
	/**
	 * @return the ast elements for the given line, which are breakpointable
	 */
	public Vector getBreakpointableElements(){
		return visitor.getBreakpointableElements();
	}
	
	/**
	 * @return the JMethodDeclaration for the given line or null
	 */
	public JMethodDeclaration getMethodDeclaration(){
		return visitor.getMethodDeclaration();
	}
	
	/**
	 * @return the field declaration for the line number
	 */
	public JFieldDeclaration getFieldDeclaration(){
		return visitor.getFieldDeclaration();
	}
	
	/**
	 * @return the type declaration for the field or method
	 */
	public JTypeDeclaration getTypeDeclaration(){
		return visitor.getTypeDeclaration();
	}
	
	/**
	 * This method is necessary for generating the fully qualified typename 
	 * for externalized cclasses.
	 * 
	 * @return the type name for the type declaration
	 */
	public String getTypeName(){
		return visitor.getTypeName();
	}
}
