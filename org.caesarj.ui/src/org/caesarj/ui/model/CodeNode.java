package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.bridge.ISourceLocation;
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
public class CodeNode extends CaesarProgramElementNode {

	public CodeNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg, ProgramElementNode pNode) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg, pNode);
		this.initImages();
	}

	public String getText(String text) {
		return text.substring(text.lastIndexOf("]") + 2); //$NON-NLS-1$
	}

	protected void initImages() {
	}

	public Image getImage() {
		return new CaesarElementImageDescriptor(CaesarPluginImages.DESC_CODE,
				null, BIG_SIZE).createImage();
	}

}