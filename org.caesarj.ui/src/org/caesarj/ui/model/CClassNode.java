package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.ui.CaesarPluginImages;


public class CClassNode extends ClassNode {

	/**
	 * @param signature
	 * @param kind
	 * @param children
	 */
	public CClassNode(String signature, Kind kind, List childrenArg) {
		super(signature, kind, childrenArg);
		this.initImages();
	}

	/**
	 * @param signature
	 * @param kind
	 * @param sourceLocation
	 * @param modifiers
	 * @param formalComment
	 * @param children
	 */
	public CClassNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.initImages();
	}

	/**
	 * @param signature
	 * @param kind
	 * @param sourceLocation
	 * @param modifiers
	 * @param formalComment
	 * @param children
	 * @param importedPackages
	 * @param importedClasses
	 */
	public CClassNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg,
			JPackageImport[] importedPackages, JClassImport[] importedClasses) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg, importedPackages, importedClasses);
		this.initImages();
	}

	
	protected void initImages() {
		this.PUBLIC = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC;
		this.PRIVATE = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PRIVATE;
		this.PROTECTED = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PROTECTED;
		this.DEFAULT = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_DEFAULT;
	}
}
