package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.export.CClass;
import org.caesarj.ui.CaesarPluginImages;

public class CClassNode extends ClassNode {

	private CClass cclass;

	public CClassNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg, CClass cclass) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.cclass=cclass;
		this.initImages();
	}

	protected void initImages() {
		this.PUBLIC = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC;
		this.PRIVATE = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PRIVATE;
		this.PROTECTED = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PROTECTED;
		this.DEFAULT = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_DEFAULT;
	}

	public CClass getCClass() {
		return cclass;
	}
}