package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.kjc.JClassDeclaration;

/**
 * ...
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AdviceDeclarationNode extends ProgramElementNode {

    private AdviceDeclaration adviceDeclaration;
    private JClassDeclaration classDeclaration;

	public AdviceDeclarationNode(
        AdviceDeclaration adviceDeclaration,
        JClassDeclaration classDeclaration,
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
        
        this.adviceDeclaration = adviceDeclaration;
        this.classDeclaration = classDeclaration;
	}

	public AdviceDeclaration getAdviceDeclaration() {
		return adviceDeclaration;
	}
    
	public JClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}
    
}
