package org.caesarj.ui.model;

import java.util.ArrayList;
import java.util.List;

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
public class PackageNode extends CaesarProgramElementNode {

	public PackageNode(String signature, Kind kind, List childrenArg) {
		super(signature, kind, childrenArg);
		this.initImages();
	}

	public Object clone() {
		return new PackageNode(this.getSignature(), this
				.getProgramElementKind(), new ArrayList());
	}

	public Image getImage() {
		return new CaesarElementImageDescriptor(
				CaesarPluginImages.DESC_OUT_PACKAGE, null, BIG_SIZE)
				.createImage();
	}

	public String getText(String text) {
		String label = text.substring(text.lastIndexOf("]") + 2); //$NON-NLS-1$
		label = label.replaceAll("_Impl", "");
		return label;
	}

	protected void initImages() {
	}

}