package org.caesarj.ui.model;

import org.aspectj.asm.StructureNode;
import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.TypeX;
import org.caesarj.compiler.ast.phylum.declaration.CjAdviceDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.types.CType;

/**
 * Traverses the abstract model tree and resolves the signatures
 * for advices and methods
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class SignatureResolver extends AbstractAsmVisitor {

	public void visit(StructureNode node) {

		if (node instanceof AdviceDeclarationNode) {
			AdviceDeclarationNode adviceNode = (AdviceDeclarationNode) node;

			CjAdviceDeclaration adviceDeclaration = adviceNode.getAdviceDeclaration();
			//JClassDeclaration classDeclaration  = adviceNode.getClassDeclaration();

			String ident =
				NameMangler.adviceName(
					TypeX.forName(adviceNode.getClassFullQualifiedName()),
					adviceDeclaration.getKind().wrappee(),
					adviceDeclaration.getTokenReference().getLine());

			try {
				setBytecodeSignature(
					adviceNode,
					ident,
					adviceDeclaration.getMethod().getParameters(),
					adviceDeclaration.getReturnType());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode methodNode = (MethodDeclarationNode) node;
			JMethodDeclaration methodDeclaration = methodNode.getMethodDeclaration();

			try {
				setBytecodeSignature(
					methodNode,
					methodDeclaration.getIdent(),
					methodDeclaration.getMethod().getParameters(),
					methodDeclaration.getMethod().getReturnType());
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		} else if (node instanceof ConstructorDeclarationNode) {
			ConstructorDeclarationNode cNode = (ConstructorDeclarationNode) node;
			JConstructorDeclaration constructorDeclaration = cNode.getConstructorDeclaration();

			try {
				setBytecodeSignature(
					cNode,
					constructorDeclaration.getIdent(),
					constructorDeclaration.getMethod().getParameters(),
					constructorDeclaration.getMethod().getReturnType());
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

		super.visit(node);
	}
	
	private void setBytecodeSignature(
			CaesarProgramElementNode peNode,
			String ident,
			CType[] arguments,
			CType returnType) {
			StringBuffer byteCodeSig = new StringBuffer();

			byteCodeSig.append("("); //$NON-NLS-1$
			for (int i = 0; i < arguments.length; i++) {
				byteCodeSig.append(arguments[i].getSignature());
			}
			byteCodeSig.append(")"); //$NON-NLS-1$
			byteCodeSig.append(returnType.getSignature());

			peNode.setBytecodeName(ident);
			peNode.setBytecodeSignature(byteCodeSig.toString());
		}
}
