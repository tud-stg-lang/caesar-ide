package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CType;
import org.eclipse.jdt.internal.ui.JavaPluginImages;

/**
 * ASM Node marking a method declaration.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class MethodDeclarationNode extends CaesarProgramElementNode {

	private JMethodDeclaration methodDeclaration;

	private JTypeDeclaration classDeclaration;

	public MethodDeclarationNode(JMethodDeclaration methodDeclarationArg,
			JTypeDeclaration classDeclarationArg, String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.initImages();
		this.methodDeclaration = methodDeclarationArg;
		this.classDeclaration = classDeclarationArg;
	}

	public JMethodDeclaration getMethodDeclaration() {
		return this.methodDeclaration;
	}

	public CType getReturnTyp() {
		try {
			return this.methodDeclaration.getReturnType();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void initImages() {
		this.PUBLIC = JavaPluginImages.DESC_MISC_PUBLIC;
		this.PRIVATE = JavaPluginImages.DESC_MISC_PRIVATE;
		this.PROTECTED = JavaPluginImages.DESC_MISC_PROTECTED;
		this.DEFAULT = JavaPluginImages.DESC_MISC_DEFAULT;
	}

	public String getText(String text) {
		String label = text;
		label = label.substring(label.lastIndexOf("]") + 2); //$NON-NLS-1$
		label += "("; //$NON-NLS-1$
		JFormalParameter[] para = this.getMethodDeclaration().getArgs();
		int paraSize = para.length;
		for (int i = 0; i < paraSize; i++) {
			String arg = para[i].getType().toString();
			label += arg.substring(arg.lastIndexOf('.') + 1);
			if (i < paraSize - 1)
				label += ", "; //$NON-NLS-1$
		}
		label += ") : "; //$NON-NLS-1$
		CType type = this.getReturnTyp();
		if (type == null) {
			label += "no statement"; //$NON-NLS-1$
		} else if (type.toString().compareTo("") == 0) { //$NON-NLS-1$
			label += "void"; //$NON-NLS-1$
		} else
			label += type.toString().substring(
					type.toString().lastIndexOf('.') + 1);
		return label.replaceAll("_Impl", "");
	}

	public JTypeDeclaration getClassDeclaration() {
		return this.classDeclaration;
	}

}