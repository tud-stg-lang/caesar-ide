package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.FjMethodDeclaration;
import org.caesarj.compiler.ast.JClassDeclaration;
import org.caesarj.compiler.types.CType;

/**
 * ASM Node marking a method declaration.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class MethodDeclarationNode extends CaesarProgramElementNode {

	private FjMethodDeclaration methodDeclaration;
	private JClassDeclaration classDeclaration;

	public MethodDeclarationNode(
		FjMethodDeclaration methodDeclaration,
		JClassDeclaration classDeclaration,
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.methodDeclaration = methodDeclaration;
		this.classDeclaration = classDeclaration;
	}

	public FjMethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
	}

	public CType getReturnTyp() {
		try {
			return this.methodDeclaration.getMethod().getReturnType();
		} catch (NullPointerException e) {
			//TODO Es fehlen Informationen die wahrscheinlich vom Compiler nicht richtig in die AST-Tree übernommen werden
			return null;
		}
	}

	public JClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}
}
