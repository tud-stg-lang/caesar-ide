package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.eclipse.jdt.internal.ui.JavaPluginImages;

/**
 * @author Shadow
 * 
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu
 * ändern: Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und
 * Kommentare
 */
public class ClassNode extends CaesarProgramElementNode {

	protected void initImages() {
		this.PUBLIC = JavaPluginImages.DESC_OBJS_INNER_CLASS_PUBLIC;
		this.PRIVATE = JavaPluginImages.DESC_OBJS_INNER_CLASS_PRIVATE;
		this.PROTECTED = JavaPluginImages.DESC_OBJS_INNER_CLASS_PROTECTED;
		this.DEFAULT = JavaPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT;
	}

	public ClassNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.initImages();
	}

	public int compareTo(Object o) throws ClassCastException {
		return super.compareTo(o);
	}

	public String getText(String text) {
		return text
				.substring(text.lastIndexOf("]") + 2).replaceFirst("_Impl", ""); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

}