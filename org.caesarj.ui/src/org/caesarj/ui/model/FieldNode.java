package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JPackageImport;
import org.caesarj.compiler.types.CType;
import org.eclipse.jdt.internal.ui.JavaPluginImages;

/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
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
	public FieldNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		CType type,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.initImages();
		this.type = type;
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
			this.initImages();
	}

	public String getShortType() {
		String typDef = this.type.toString();
		return typDef.substring(typDef.lastIndexOf('.') + 1);
	}

	/* (Kein Javadoc)
	 * @see org.caesarj.ui.model.CaesarProgramElementNode#getText(java.lang.String)
	 */
	public String getText(String text) {
		String label = text.substring(text.lastIndexOf("]") + 2);
		label += " : " + this.getShortType();
		return label;
	}

	protected void initImages() {
		PUBLIC = JavaPluginImages.DESC_FIELD_PUBLIC;
		PRIVATE = JavaPluginImages.DESC_FIELD_PRIVATE;
		PROTECTED = JavaPluginImages.DESC_FIELD_PROTECTED;
		DEFAULT = JavaPluginImages.DESC_FIELD_DEFAULT;
	}

}
