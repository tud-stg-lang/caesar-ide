package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.FjMethodDeclaration;
import org.caesarj.compiler.ast.JClassDeclaration;
import org.caesarj.compiler.ast.JFormalParameter;
import org.caesarj.compiler.ast.JTypeDeclaration;
import org.caesarj.compiler.types.CType;
import org.eclipse.jdt.internal.ui.JavaPluginImages;

/**
 * ASM Node marking a method declaration.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class MethodDeclarationNode extends CaesarProgramElementNode {

	private FjMethodDeclaration methodDeclaration;
	private JTypeDeclaration classDeclaration;
	
	public MethodDeclarationNode(
		FjMethodDeclaration methodDeclaration,
		JTypeDeclaration classDeclaration,
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.initImages();this.methodDeclaration = methodDeclaration;
		this.classDeclaration = classDeclaration;
	}

	public FjMethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
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
		PUBLIC = JavaPluginImages.DESC_MISC_PUBLIC;
		PRIVATE = JavaPluginImages.DESC_MISC_PRIVATE;
		PROTECTED = JavaPluginImages.DESC_MISC_PROTECTED;
		DEFAULT = JavaPluginImages.DESC_MISC_DEFAULT;
	}

	public String getText(String text) {
		String label = text;
		label = label.substring(label.lastIndexOf("]") + 2);
		label += "(";
		JFormalParameter[] para = this.getMethodDeclaration().getArgs();
		int paraSize = para.length;
		for (int i = 0; i < paraSize; i++) {
			String arg = para[i].getType().toString();
			label += arg.substring(arg.lastIndexOf('.') + 1);
			if (i < paraSize - 1)
				label += ", ";
		}
		label += ") : ";
		CType type = this.getReturnTyp();
		if (type == null) {
			label += "no statement";
		} else if (type.toString().compareTo("") == 0)
			label += "void";
		else
			label += type.toString().substring(type.toString().lastIndexOf('.') + 1);
		return label;
	}

	public JTypeDeclaration getClassDeclaration() {
		return classDeclaration;
	}
	

	/* (Kein Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) throws ClassCastException {
		// TODO Automatisch erstellter Methoden-Stub
		return super.compareTo(o);
	}

}
