package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JPackageImport;

/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class InterfaceNode extends CaesarProgramElementNode {

	/**
	 * @param signature
	 * @param kind
	 * @param children
	 */
	public InterfaceNode(String signature, Kind kind, List children) {
		super(signature, kind, children);
	}

	/**
	 * @param signature
	 * @param kind
	 * @param sourceLocation
	 * @param modifiers
	 * @param formalComment
	 * @param children
	 */
	public InterfaceNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
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
	public InterfaceNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses) {
		super(
			signature,
			kind,
			sourceLocation,
			modifiers,
			formalComment,
			children,
			importedPackages,
			importedClasses);
	}

	/* (Kein Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) throws ClassCastException {
		return super.compareTo(o);
	}

}
