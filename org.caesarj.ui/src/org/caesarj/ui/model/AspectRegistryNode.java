package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.eclipse.swt.graphics.Image;

/**
 * ASM Node marking inner advice Registry classes.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AspectRegistryNode extends CaesarProgramElementNode {

	public AspectRegistryNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.initImages();
	}

	public Image getImage() {
		return super.getImage();
	}

	public String getText(String text) {
		return null;
	}

	protected void initImages() {
	}

}
