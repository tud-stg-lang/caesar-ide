package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.eclipse.swt.graphics.Image;

/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class JavaSourceFileNode extends CaesarProgramElementNode {

	public JavaSourceFileNode(
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

	public int compareTo(Object o) throws ClassCastException {
		return super.compareTo(o);
	}

	public Image getImage() {
		return super.getImage();
	}

	public String getText(String text) {
		return null;
	}

	protected void initImages() {
	}

}
