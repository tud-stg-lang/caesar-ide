package org.caesarj.ui.model;

import org.aspectj.asm.ProgramElementNode;
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

	public CodeNode(ProgramElementNode pNode) {
		super(pNode.getSignature(), pNode.getProgramElementKind(), pNode
				.getSourceLocation(), 0, pNode
				.getFormalComment(), pNode.getChildren());
		this.setRelations(pNode.getRelations());
		this.setBytecodeName(pNode.getBytecodeName());
		this.setBytecodeSignature(pNode.getBytecodeSignature());
		this.setMessage(pNode.getMessage());
		this.setImplementor(pNode.isImplementor());
		this.setRelations(pNode.getRelations());
		this.setRunnable(pNode.isRunnable());
		this.setOverrider(pNode.isOverrider());
		this.setSourceLocation(pNode.getSourceLocation());
		this.name = pNode.getName();
		this.initImages();
	}

	public String getText(String text) {
		String label = text.substring(text.lastIndexOf("]") + 1); //$NON-NLS-1$
		label = label.replaceAll("_Impl", "");
		return label;
	}

	protected void initImages() {
	}

	public Image getImage() {
		return new CaesarElementImageDescriptor(CaesarPluginImages.DESC_CODE,
				null, BIG_SIZE).createImage();
	}

}