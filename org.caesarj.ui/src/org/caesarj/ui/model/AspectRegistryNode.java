package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;

/**
 * ASM Node marking inner advice Registry classes.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AspectRegistryNode extends CaesarProgramElementNode {

	public AspectRegistryNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocationArg,
		int modifiers,
		String formalComment,
		List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment, childrenArg);
		this.initImages();
	}

	public String getText(String text) {
		return null;
	}

	protected void initImages() {
	}

}
