package org.caesarj.ui.model;

import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JPackageImport;
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

	static Logger logger = Logger.getLogger(CodeNode.class);
	/**
	 * @param signature
	 * @param kind
	 * @param children
	 */
	public CodeNode(String signature, Kind kind, List children) {
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
	public CodeNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
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

	public String getText(String text) {
		return text.substring(text.lastIndexOf("]") + 2);
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
