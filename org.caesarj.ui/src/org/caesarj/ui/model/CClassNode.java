package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.ui.CaesarPluginImages;


public class CClassNode extends CaesarProgramElementNode {

	/**
	 * @param signature
	 * @param kind
	 * @param children
	 */
	public CClassNode(String signature, Kind kind, List children) {
		super(signature, kind, children);
		// TODO Auto-generated constructor stub
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
			ISourceLocation sourceLocation, int modifiers,
			String formalComment, List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment,
				children);
		// TODO Auto-generated constructor stub
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
			ISourceLocation sourceLocation, int modifiers,
			String formalComment, List children,
			JPackageImport[] importedPackages, JClassImport[] importedClasses) {
		super(signature, kind, sourceLocation, modifiers, formalComment,
				children, importedPackages, importedClasses);
		// TODO Auto-generated constructor stub
	}

	
	protected void initImages() {
		PUBLIC = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PUBLIC;
		PRIVATE = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PRIVATE;
		PROTECTED = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_PROTECTED;
		DEFAULT = CaesarPluginImages.DESC_OBJS_INNER_CCLASS_DEFAULT;
	}

	
	public String getText(String text) {
		// TODO Auto-generated method stub
		return "CClass";
	}

}
