package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
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
	public FieldNode(String signature, Kind kind, List childrenArg) {
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
	public FieldNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocationArg,
		CType typeArg,
		int modifiers,
		String formalComment,
		List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment, childrenArg);
		this.initImages();
		this.type = typeArg;
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

	public String getShortType() {
		String typDef = this.type.toString();
		return typDef.substring(typDef.lastIndexOf('.') + 1);
	}

	/* (Kein Javadoc)
	 * @see org.caesarj.ui.model.CaesarProgramElementNode#getText(java.lang.String)
	 */
	public String getText(String text) {
		String label = text.substring(text.lastIndexOf("]") + 2); //$NON-NLS-1$
		label += " : " + this.getShortType(); //$NON-NLS-1$
		return label;
	}

	protected void initImages() {
		this.PUBLIC = JavaPluginImages.DESC_FIELD_PUBLIC;
		this.PRIVATE = JavaPluginImages.DESC_FIELD_PRIVATE;
		this.PROTECTED = JavaPluginImages.DESC_FIELD_PROTECTED;
		this.DEFAULT = JavaPluginImages.DESC_FIELD_DEFAULT;
	}

}
