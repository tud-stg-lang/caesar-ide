package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.ui.CaesarElementImageDescriptor;
import org.caesarj.ui.CaesarPluginImages;
import org.eclipse.swt.graphics.Image;
/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class CodeNode extends CaesarProgramElementNode {

	/**
	 * @param signature
	 * @param kind
	 * @param children
	 */
	public CodeNode(String signature, Kind kind, List childrenArg) {
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
	public CodeNode(
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
	public CodeNode(
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
		return text.substring(text.lastIndexOf("]") + 2); //$NON-NLS-1$
	}

	protected void initImages() {
	}

	public Image getImage() {
		return new CaesarElementImageDescriptor(
			CaesarPluginImages.DESC_CODE,
			null,
			BIG_SIZE)
			.createImage();
	}

}
