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
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu
 * ändern: Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und
 * Kommentare
 */
public class ConstructorDeclarationNode extends CaesarProgramElementNode {

	private JConstructorDeclaration constructorDeclaration;

	private JClassDeclaration classDeclaration;

	public ConstructorDeclarationNode(
			JConstructorDeclaration constructorDeclarationArg,
			JClassDeclaration classDeclarationArg, String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.initImages();
		this.constructorDeclaration = constructorDeclarationArg;
		this.classDeclaration = classDeclarationArg;
	}

	public JConstructorDeclaration getConstructorDeclaration() {
		return this.constructorDeclaration;
	}

	public JClassDeclaration getClassDeclaration() {
		return this.classDeclaration;
	}

	public String getText(String text) {
		String label = text.substring(text.lastIndexOf("]") + 2); //$NON-NLS-1$
		label += "("; //$NON-NLS-1$
		JFormalParameter[] para = this.getConstructorDeclaration().getArgs();
		int paraSize = para.length;
		for (int i = 0; i < paraSize; i++) {
			String temp = para[i].getType().toString();
			label += temp.substring(temp.lastIndexOf('.') + 1, temp.length());
			if (i < paraSize - 1)
				label += ", "; //$NON-NLS-1$
		}
		label += ")"; //$NON-NLS-1$
		label = label.replaceAll("_Impl", "");
		if (this.parent instanceof CClassNode || this.parent instanceof AspectNode) {
			label = label.substring(0, label.indexOf('(')+1) + ")";
		}
		return label;
	}

	protected void initImages() {
		this.PUBLIC = JavaPluginImages.DESC_MISC_PUBLIC;
		this.PRIVATE = JavaPluginImages.DESC_MISC_PRIVATE;
		this.PROTECTED = JavaPluginImages.DESC_MISC_PROTECTED;
		this.DEFAULT = JavaPluginImages.DESC_MISC_DEFAULT;
	}
}