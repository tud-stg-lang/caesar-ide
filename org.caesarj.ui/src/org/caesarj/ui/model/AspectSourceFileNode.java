package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;

public class AspectSourceFileNode extends CaesarProgramElementNode {

	/**
	 * @param signature
	 * @param kind
	 * @param children
	 */
	public AspectSourceFileNode(String signature, Kind kind, List childrenArg) {
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
	public AspectSourceFileNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocationArg,
		int modifiers,
		String formalComment,
		List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment, childrenArg);
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
	public AspectSourceFileNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocationArg,
		int modifiers,
		String formalComment,
		List childrenArg,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses) {
		super(
			signature,
			kind,
			sourceLocationArg,
			modifiers,
			formalComment,
			childrenArg,
			importedPackages,
			importedClasses);
		this.initImages();
	}

	public String getText(String text) {
		return null;
	}

	public int compareTo(Object arg0) throws ClassCastException {
		return super.compareTo(arg0);
	}

	protected void initImages() {
	}

}
