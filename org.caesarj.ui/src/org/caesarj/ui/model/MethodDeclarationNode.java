package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureNode;
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
			return null;
		}
	}

	public JClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}

	public void addChild(StructureNode sNode) {
		if (((ProgramElementNode) sNode)
			.getProgramElementKind()
			.equals(ProgramElementNode.Kind.CODE)) {
			ProgramElementNode pNode = (ProgramElementNode) sNode;
			super.addChild(
				new CodeNode(
					pNode.getSignature(),
					ProgramElementNode.Kind.CODE,
					pNode.getSourceLocation(),
					0,
					pNode.getFormalComment(),
					pNode.getChildren()));
		} else
			super.addChild(sNode);
	}
}
