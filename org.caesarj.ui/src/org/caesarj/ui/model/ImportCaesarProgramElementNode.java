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
public class ImportCaesarProgramElementNode extends CaesarProgramElementNode {

	public boolean rootFlag;
	/**
	 * @param signature
	 * @param kind
	 * @param sourceLocation
	 * @param modifiers
	 * @param formalComment
	 * @param children
	 */
	public ImportCaesarProgramElementNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses) {
		super("Imports", kind, sourceLocation, modifiers, formalComment, children);
		this.rootFlag = true;
		for (int i = 0; i < importedPackages.length; i++) {
			if (importedPackages[i].getName().compareTo("java/lang") != 0)
				this.children.add(
					new ImportCaesarProgramElementNode(
						importedPackages[i].getName() + ".*",
						kind,
						sourceLocation,
						0,
						formalComment,
						null));
		}
		for (int i = 0; i < importedClasses.length; i++) {
			this.children.add(
				new ImportCaesarProgramElementNode(
					importedClasses[i].getQualifiedName(),
					kind,
					sourceLocation,
					0,
					formalComment,
					null));
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
	public ImportCaesarProgramElementNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.rootFlag = false;
	}
}
