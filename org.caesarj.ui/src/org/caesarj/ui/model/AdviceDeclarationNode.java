package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.JClassDeclaration;
import org.caesarj.kjc.JFormalParameter;

/**
 * ...
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AdviceDeclarationNode extends CaesarProgramElementNode {

    private AdviceDeclaration adviceDeclaration;
    //private JClassDeclaration classDeclaration;
    private String classFullQualifiedName;

	public AdviceDeclarationNode(
        AdviceDeclaration adviceDeclaration,        
        //JClassDeclaration classDeclaration,
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
        //this.classDeclaration = classDeclaration;
        this.classFullQualifiedName = classFullQualifiedName;
	}

	public AdviceDeclaration getAdviceDeclaration() {
		return adviceDeclaration;
	}
    
    public String getClassFullQualifiedName() {
        return classFullQualifiedName;
    }
    
    /*
    public JClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }
    */
    
}
