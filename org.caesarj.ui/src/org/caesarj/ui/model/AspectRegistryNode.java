package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.kjc.JClassDeclaration;

/**
 * ...
 * 
 * @author ivica
 */
public class AspectRegistryNode extends ProgramElementNode {

  	public AspectRegistryNode(
    	String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children
    ) {
		super(
			signature,
			kind,
			sourceLocation,
			modifiers,
			formalComment,
			children
        );
 	}
    
}
