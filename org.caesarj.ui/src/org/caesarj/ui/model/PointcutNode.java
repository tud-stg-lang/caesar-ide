package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.FjMethodDeclaration;
import org.caesarj.compiler.ast.JClassDeclaration;
import org.eclipse.swt.graphics.Image;

/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class PointcutNode extends CaesarProgramElementNode {

	private FjMethodDeclaration methodDeclaration;
	private JClassDeclaration classDeclaration;

	public PointcutNode(
		FjMethodDeclaration methodDeclaration,
		JClassDeclaration classDeclaration,
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.initImages();
		this.methodDeclaration = methodDeclaration;
		this.classDeclaration = classDeclaration;
	}

	public Image getImage() {
		return super.getImage();
	}

	public String getText(String text) {
		return null;
	}

	public int compareTo(Object arg0) throws ClassCastException {
		return super.compareTo(arg0);
	}

	protected void initImages() {
	}

}
