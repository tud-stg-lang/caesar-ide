package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.declaration.CjPointcutDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JClassDeclaration;

/**
 * @author Shadow
 * 
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu
 * ändern: Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und
 * Kommentare
 */
public class PointcutNode extends CaesarProgramElementNode {

	private CjPointcutDeclaration pointCutDeclaration;

	private JClassDeclaration classDeclaration;

	public PointcutNode(CjPointcutDeclaration pointCutDeclaration,
			JClassDeclaration classDeclaration, String signature, Kind kind,
			ISourceLocation sourceLocation, int modifiers,
			String formalComment, List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment,
				children);
		this.initImages();
		this.pointCutDeclaration = pointCutDeclaration;
		this.classDeclaration = classDeclaration;
	}

	protected void initImages() {
	}

	
	public String getText(String text) {
		return null;
	}

	
	public int compareTo(Object o) {
		return 0;
	}

}