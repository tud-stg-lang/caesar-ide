package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.kjc.JClassDeclaration;

/**
 * ASM Node marking inner advice Registry classes.
 * 
 * @author ivica
 */
public class AspectRegistryNode extends CaesarProgramElementNode {

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
