package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.AdviceDeclaration;

/**
 * Adds additional methods for AdviceDeclarations.
 * Needed by SignatureResolver Visitor in order to resolve the advice signature.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AdviceDeclarationNode extends CaesarProgramElementNode {

    private AdviceDeclaration adviceDeclaration;
    private String classFullQualifiedName;

	public AdviceDeclarationNode(
        AdviceDeclaration adviceDeclaration,        
        String classFullQualifiedName,
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
        this.classFullQualifiedName = classFullQualifiedName;
	}

	public AdviceDeclaration getAdviceDeclaration() {
		return adviceDeclaration;
	}
    
    public String getClassFullQualifiedName() {
        return classFullQualifiedName;
    }

}
