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
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu
 * ändern: Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und
 * Kommentare
 */
public class ImportCaesarProgramElementNode extends CaesarProgramElementNode {

	public boolean rootFlag;

	public ImportCaesarProgramElementNode(Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg,
			JPackageImport[] importedPackages, JClassImport[] importedClasses) {
		super(
				"Imports", kind, sourceLocationArg, modifiers, formalComment, childrenArg); //$NON-NLS-1$
		this.initImages();
		this.rootFlag = true;
		for (int i = 0; i < importedPackages.length; i++) {
			if (importedPackages[i].getName().compareTo("java/lang") != 0) { //$NON-NLS-1$
				this.children.add(new ImportCaesarProgramElementNode(
						importedPackages[i].getName() + ".*", //$NON-NLS-1$
						kind, sourceLocationArg, 0, formalComment, null));
			}
		}
		for (int i = 0; i < importedClasses.length; i++) {
			this.children.add(new ImportCaesarProgramElementNode(
					importedClasses[i].getQualifiedName(), kind,
					sourceLocationArg, 0, formalComment, null));
		}
	}

	/**
	 * @param signature
	 * @param kind
	 * @param sourceLocation
	 * @param modifiers
	 * @param formalComment
	 * @param children
	 */
	public ImportCaesarProgramElementNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.rootFlag = false;
		this.initImages();
	}

	public String getText(String text) {
		String label = text.substring(text.lastIndexOf("]") + 2); //$NON-NLS-1$
		label = label.replace('/', '.');
		return label;
	}

	public Image getImage() {
		Image r = null;
		if (this.rootFlag) {
			r = new CaesarElementImageDescriptor(
					CaesarPluginImages.DESC_OUT_IMPORTS, this, BIG_SIZE)
					.createImage();
		} else {
			r = new CaesarElementImageDescriptor(
					CaesarPluginImages.DESC_IMPORTS, this, BIG_SIZE)
					.createImage();
		}
		return r;
	}

	protected void initImages() {
		this.PUBLIC = CaesarPluginImages.DESC_OUT_IMPORTS;
		this.DEFAULT = CaesarPluginImages.DESC_IMPORTS;
	}

}