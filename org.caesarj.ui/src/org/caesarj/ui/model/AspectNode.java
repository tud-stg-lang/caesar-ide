package org.caesarj.ui.model;

import java.util.List;

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
public class AspectNode extends CaesarProgramElementNode {

	public AspectNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.initImages();
	}

	public String getText(String text) {
		return text
				.substring(text.lastIndexOf("]") + 2).replaceAll("_Impl", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public Image getImage() {
		return new CaesarElementImageDescriptor(CaesarPluginImages.DESC_ASPECT,
				null, BIG_SIZE).createImage();
	}

	protected void initImages() {
	}

}