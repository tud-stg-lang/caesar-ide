package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.declaration.JClassDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JConstructorDeclaration;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.eclipse.jdt.internal.ui.JavaPluginImages;

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
		this.initImages();
		this.constructorDeclaration = constructorDeclaration;
		this.classDeclaration = classDeclaration;
	}

	public ConstructorDeclarationNode(String signature, Kind kind, List children) {
		super(signature, kind, children);
		this.initImages();
	}

	public ConstructorDeclarationNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.initImages();
	}

	public JConstructorDeclaration getConstructorDeclaration() {
		return constructorDeclaration;
	}

	public JClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}

	public String getText(String text) {
		String label = text.substring(text.lastIndexOf("]") + 2);
		label += "(";
		JFormalParameter[] para = this.getConstructorDeclaration().getArgs();
		int paraSize = para.length;
		for (int i = 0; i < paraSize; i++) {
			String temp = para[i].getType().toString();
			label += temp.substring(temp.lastIndexOf('.') + 1, temp.length());
			if (i < paraSize - 1)
				label += ", ";
		}
		label += ")";
		return label;
	}

	protected void initImages() {
		PUBLIC = JavaPluginImages.DESC_MISC_PUBLIC;
		PRIVATE = JavaPluginImages.DESC_MISC_PRIVATE;
		PROTECTED = JavaPluginImages.DESC_MISC_PROTECTED;
		DEFAULT = JavaPluginImages.DESC_MISC_DEFAULT;
	}
}
