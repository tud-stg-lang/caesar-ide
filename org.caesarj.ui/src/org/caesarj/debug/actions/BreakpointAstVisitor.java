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

import org.caesarj.compiler.ast.phylum.JPhylum;
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
	
	/**
	 * @param line 		the sourcecode-linenumber 
	 */
	public BreakpointAstVisitor(int line){
		this.lineNumber = line;
		this.elements = new Vector();
	}
	
	/**
	 * @return		the elements of the ast that allow breakpoints
	 */
	public Vector getBreakpointableElements(){
		return this.elements;
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
}
