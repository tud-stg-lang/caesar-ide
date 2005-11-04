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

import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.statement.CjDeployStatement;
import org.caesarj.compiler.ast.phylum.statement.JBreakStatement;
import org.caesarj.compiler.ast.phylum.statement.JCatchClause;
import org.caesarj.compiler.ast.phylum.statement.JContinueStatement;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JThrowStatement;

/**
 * A visitor for the abstract symtax tree, extracting the elements for a given 
 * sourcecode-linenumber which allow breakpoints.
 *   
 * @author meffert
 */
public class BreakpointAstVisitor {


	private int lineNumber;
	private Vector elements;
	private JMethodDeclaration methDec;
	private JFieldDeclaration fieldDec;
	private JTypeDeclaration typeDec;
	
	private Stack typeStack = new Stack();
	
	private String typeName;
	
	/**
	 * @param line 		the sourcecode-linenumber 
	 */
	public BreakpointAstVisitor(int line){
		this.lineNumber = line;
		this.elements = new Vector();
		this.methDec = null;
		this.fieldDec = null;
		this.typeDec = null;
		this.typeName = null;
	}
	
	/**
	 * @return		the elements of the ast that allow line-breakpoints
	 */
	public Vector getBreakpointableElements(){
		return this.elements;
	}
	
	/**
	 * @return		the method declaration for the sourcecode line
	 */
	public JMethodDeclaration getMethodDeclaration(){
		return this.methDec;
	}
	
	/**
	 * @return		the field declaration for the sourcecode line
	 */
	public JFieldDeclaration getFieldDeclaration(){
		return this.fieldDec;
	}
	
	/**
	 * @return		the type declaration which contains the field-dec. or method-dec.
	 */
	public JTypeDeclaration getTypeDeclaration(){
		return this.typeDec;
	}
	
	/**
	 * This method is necessary for generating the fully qualified typename for 
	 * externalized cclasses.
	 * 
	 * @return		the typename for the type declaration
	 */
	public String getTypeName(){
		return this.typeName;
	}
	
	/**
	 * Set the qualified typename for the typedeclaration.
	 */
	private void setTypeName(){
		String qName = "";
		Iterator it = typeStack.iterator();
		JTypeDeclaration dec = null;
		while(it.hasNext()){
			dec = (JTypeDeclaration) it.next();
			if(!qName.equals("")){
				qName += "$" + dec.getIdent();
			}else{
				qName = dec.getIdent();
			}
		}
		this.typeName = qName;
	}
	
	
	// ----------------------------------------------------------------------
	// Visitor part
	// ----------------------------------------------------------------------
	
	public boolean visit(JPhylum self) {
    	return true;
    }
	
	public boolean visit(JExpression self){
		if(self.getTokenReference().getLine() == lineNumber){
    		elements.add(self);
    	}
		return true;
	}
	
	public boolean visit(JBreakStatement self){
		if(self.getTokenReference().getLine() == lineNumber){
    		elements.add(self);
    	}
		return true;
	}
	public boolean visit(JContinueStatement self){
		if(self.getTokenReference().getLine() == lineNumber){
    		elements.add(self);
    	}
		return true;
	}
	public boolean visit(JExpressionStatement self){
		if(self.getTokenReference().getLine() == lineNumber){
    		elements.add(self);
    	}
		return true;
	}
	public boolean visit(JReturnStatement self){
		if(self.getTokenReference().getLine() == lineNumber){
    		elements.add(self);
    	}
		return true;
	}
	public boolean visit(CjDeployStatement self){
		if(self.getTokenReference().getLine() == lineNumber){
    		elements.add(self);
    	}
		return true;
	}
	public boolean visit(JThrowStatement self){
		if(self.getTokenReference().getLine() == lineNumber){
    		elements.add(self);
    	}
		return true;
	}

	public boolean visit(JCatchClause self){
		// [mef] : dont add catchclause, instead add parameter for this catchclause
		// 		update: not possible to add parameters! 
		// BP for e.g.: 
		// catch(				/* BP possible in eclipse BUT NOT in VM */
		//		Exception e)	/* BP impossible in eclipse BUT in VM
		if(self.getTokenReference().getLine() == lineNumber){
    		elements.add(self);
    	}
		return true;
	}
	
	public boolean visit(JMethodDeclaration self){
		if(self.getTokenReference().getLine() == lineNumber){
			methDec = self;
			typeDec = (JTypeDeclaration) typeStack.peek();
			setTypeName();
    	}
		return true;
	}
	public boolean visit(CjAdviceDeclaration self){
		// method-breakpoints are not supported by advice-declarations
		return true;
	}
	
	public boolean visit(JFieldDeclaration self){
		if(self.getTokenReference().getLine() == lineNumber){
			fieldDec = self;
			typeDec = (JTypeDeclaration) typeStack.peek();
			setTypeName();
		}
		return true;
	}
	
	public boolean visit(JTypeDeclaration self){
		typeStack.push(self);
		return true;
	}
	public boolean endVisit(JTypeDeclaration self){
		typeStack.pop();
		return true;
	}
}
