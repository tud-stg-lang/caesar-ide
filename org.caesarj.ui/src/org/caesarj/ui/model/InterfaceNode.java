package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.ui.CaesarElementImageDescriptor;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

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
	public InterfaceNode(
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
		this.initImages();
	}

	/* (Kein Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) throws ClassCastException {
		return super.compareTo(o);
	}

	/* (Kein Javadoc)
	 * @see org.caesarj.ui.model.CaesarProgramElementNode#getText(java.lang.String)
	 */
	public String getText(String text) {
		return text.substring(text.lastIndexOf("]") + 2);
	}

	protected void initImages() {
		PUBLIC = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC;
		PRIVATE = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE;
		PROTECTED = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED;
		DEFAULT = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT;
	}

	public Image getImage() {
		ImageDescriptor img;
		switch (this.getCAModifiers() % 8) {
			case 1 :
				img = PUBLIC;
				break;
			case 2 :
				img = PRIVATE;
				break;
			case 4 :
				img = PROTECTED;
				break;
			default :
				img = DEFAULT;
		}
		return new CaesarElementImageDescriptor(img, this, BIG_SIZE).createImage();
	}
}
