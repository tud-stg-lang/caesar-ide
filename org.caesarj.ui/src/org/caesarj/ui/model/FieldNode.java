package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JPackageImport;
import org.caesarj.compiler.types.CType;

/**
 * @author Shadow
 *
 * Folgendes ausw�hlen, um die Schablone f�r den erstellten Typenkommentar zu �ndern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class FieldNode extends CaesarProgramElementNode {

	private CType type;
	
	/**
	 * @param signature
	 * @param kind
	 * @param children
	 */
	public FieldNode(String signature, Kind kind, List children) {
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
	public FieldNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		CType type,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.type=type;
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
	public FieldNode(
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
	
	public String getShortType(){
		String typDef = this.type.toString();
		return typDef.substring(typDef.lastIndexOf('.')+1);
	}

}
