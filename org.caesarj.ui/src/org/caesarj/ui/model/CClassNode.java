package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.ui.CaesarPluginImages;

public class CClassNode extends ClassNode {

	public CClassNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.initImages();
	}

	protected void initImages() {
		this.PUBLIC = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC;
		this.PRIVATE = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PRIVATE;
		this.PROTECTED = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PROTECTED;
		this.DEFAULT = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_DEFAULT;
	}
}