package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.asm.IProgramElement;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.kjc.JClassDeclaration;

/**
 * ASM Node marking inner advice Registry classes.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AspectRegistryNode extends CaesarProgramElementNode {

  	public AspectRegistryNode(
    	String signature,
		IProgramElement.Kind kind,
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
