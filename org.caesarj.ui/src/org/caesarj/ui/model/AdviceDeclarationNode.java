package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.ui.CaesarElementImageDescriptor;
import org.caesarj.ui.CaesarPluginImages;
import org.eclipse.swt.graphics.Image;

/**
 * Adds additional methods for AdviceDeclarations. Needed by SignatureResolver
 * Visitor in order to resolve the advice signature.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AdviceDeclarationNode extends CaesarProgramElementNode {

	private String classFullQualifiedName;

	public AdviceDeclarationNode(
			String classFullQualifiedNameArg, String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.initImages();
		this.classFullQualifiedName = classFullQualifiedNameArg;
	}


	public String getClassFullQualifiedName() {
		return this.classFullQualifiedName;
	}

	public String getText(String text) {
		return this.name;
		//return text.substring(text.lastIndexOf("]") + 2); //$NON-NLS-1$
	}

	public Image getImage() {
		return new CaesarElementImageDescriptor(
				CaesarPluginImages.DESC_JOINPOINT, null, BIG_SIZE)
				.createImage();
	}

	protected void initImages() {
	}

}