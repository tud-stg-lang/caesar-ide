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

	public PointcutNode(CjPointcutDeclaration pointCutDeclarationArg,
			JClassDeclaration classDeclarationArg, String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.initImages();
		this.pointCutDeclaration = pointCutDeclarationArg;
		this.classDeclaration = classDeclarationArg;
	}

	protected void initImages() {
	}

	
	public String getText(String text) {
		return null;
	}

	
	public int compareTo(Object o) {
		return 0;
	}

	/**
	 * @return Returns the classDeclaration.
	 */
	public JClassDeclaration getClassDeclaration() {
		return this.classDeclaration;
	}
	/**
	 * @param classDeclarationArg The classDeclaration to set.
	 */
	public void setClassDeclaration(JClassDeclaration classDeclarationArg) {
		this.classDeclaration = classDeclarationArg;
	}
	/**
	 * @return Returns the pointCutDeclaration.
	 */
	public CjPointcutDeclaration getPointCutDeclaration() {
		return this.pointCutDeclaration;
	}
	/**
	 * @param pointCutDeclarationArg The pointCutDeclaration to set.
	 */
	public void setPointCutDeclaration(CjPointcutDeclaration pointCutDeclarationArg) {
		this.pointCutDeclaration = pointCutDeclarationArg;
	}
}