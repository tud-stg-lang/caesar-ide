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
	public InterfaceNode(String signature, Kind kind, List childrenArg) {
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
	public InterfaceNode(
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
	public InterfaceNode(
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
		return text.substring(text.lastIndexOf("]") + 2); //$NON-NLS-1$
	}

	protected void initImages() {
		this.PUBLIC = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC;
		this.PRIVATE = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE;
		this.PROTECTED = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED;
		this.DEFAULT = JavaPluginImages.DESC_OBJS_INNER_INTERFACE_DEFAULT;
	}

	public Image getImage() {
		ImageDescriptor img;
		switch (this.getCAModifiers() % 8) {
			case 1 :
				img = this.PUBLIC;
				break;
			case 2 :
				img = this.PRIVATE;
				break;
			case 4 :
				img = this.PROTECTED;
				break;
			default :
				img = this.DEFAULT;
		}
		return new CaesarElementImageDescriptor(img, this, BIG_SIZE).createImage();
	}
}
