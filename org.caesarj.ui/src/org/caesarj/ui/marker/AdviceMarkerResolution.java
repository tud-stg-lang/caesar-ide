package org.caesarj.ui.marker;

import org.apache.log4j.Logger;
import org.aspectj.asm.LinkNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class AdviceMarkerResolution implements IMarkerResolution {
	private LinkNode link;

	static Logger logger = Logger.getLogger(AdviceMarkerResolution.class);
	
	public AdviceMarkerResolution(LinkNode link) {
		super();
		this.link = link;
	}

	public String getLabel() {
		return "Goto Advice: " + link.getName();
	}

	public void run(IMarker marker) {
		IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (w == null)
			return;
		IWorkbenchPage page = w.getActivePage();
		if (page == null)
			return;
		IFileEditorInput input = new FileEditorInput((IFile) marker.getResource());
		IEditorPart editorPart = page.findEditor(input);
		try {
			editorPart = IDE.openEditor(page, (IFile) marker.getResource());
		} catch (PartInitException e) {
			MessageDialog.openError(w.getShell(), "ERROR","Unable to open Editor!"); //$NON-NLS-1$
		}
	}

}
