package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.JClassDeclaration;
import org.caesarj.compiler.ast.JConstructorDeclaration;

/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class ConstructorDeclarationNode extends CaesarProgramElementNode {

	private JConstructorDeclaration constructorDeclaration;
	private JClassDeclaration classDeclaration;

	public ConstructorDeclarationNode(
		JConstructorDeclaration constructorDeclaration,
		JClassDeclaration classDeclaration,
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);

		this.constructorDeclaration = constructorDeclaration;
		this.classDeclaration = classDeclaration;
	}

	public JConstructorDeclaration getConstructorDeclaration() {
		return constructorDeclaration;
	}

	public JClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}
}
