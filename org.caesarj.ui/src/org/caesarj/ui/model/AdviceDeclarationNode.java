package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.ui.CaesarElementImageDescriptor;
import org.caesarj.ui.CaesarPluginImages;
import org.eclipse.swt.graphics.Image;

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
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.initImages();
		this.adviceDeclaration = adviceDeclaration;
		this.classFullQualifiedName = classFullQualifiedName;
	}

	public AdviceDeclaration getAdviceDeclaration() {
		return adviceDeclaration;
	}

	public String getClassFullQualifiedName() {
		return classFullQualifiedName;
	}

	public String getText(String text) {
		return text.substring(text.lastIndexOf("]") + 2);
	}

	public Image getImage() {
		return new CaesarElementImageDescriptor(
			CaesarPluginImages.DESC_JOINPOINT,
			null,
			BIG_SIZE,
			false)
			.createImage();
	}

	protected void initImages() {
	}

}
