package org.caesarj.ui.marker;

import org.apache.log4j.Logger;
import org.aspectj.asm.LinkNode;
import org.caesarj.ui.builder.Builder;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Shadow
 * 
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu
 * ändern: Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und
 * Kommentare
 */
public class AdviceMarkerResolutionGenerator implements
		IMarkerResolutionGenerator, IMarkerResolutionGenerator2 {

	static Logger logger = Logger
			.getLogger(AdviceMarkerResolutionGenerator.class);

	public IMarkerResolution[] getResolutions(IMarker marker) {
		try {
			LinkNode advices[] = (LinkNode[]) marker
					.getAttribute(AdviceMarker.LINKS);
			IMarkerResolution res[] = new AdviceMarkerResolution[advices.length];
			for (int i = 0; i < advices.length; i++)
				res[i] = new AdviceMarkerResolution(advices[i], marker);
			return res;
		} catch (CoreException e) {
			logger.error("Fehler beim auslesen der LINKS aus AdviceMarker", e);
		}
		return null;
	}

	/*
	 * (Kein Javadoc)
	 * 
	 * @see org.eclipse.ui.IMarkerResolutionGenerator2#hasResolutions(org.eclipse.core.resources.IMarker)
	 */
	public boolean hasResolutions(IMarker marker) {
		return true;
	}

	public class AdviceMarkerResolution implements IMarkerResolution {
		private LinkNode link;

		boolean toAdvice = false;

		public AdviceMarkerResolution(LinkNode link, IMarker marker) {
			super();
			this.link = link;
			try {
				this.toAdvice = marker.getAttribute(AdviceMarker.ID).equals(
						"AdviceLink");
			} catch (CoreException e) {
			}
		}

		public String getLabel() {
			return this.toAdvice ? "Open Advice: " + link.getName():"Open Methode: " + link.getName();
		}

		public void run(IMarker marker) {
			IWorkbenchWindow w = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (w == null)
				return;
			IWorkbenchPage page = w.getActivePage();
			if (page == null)
				return;
			org.eclipse.jdt.internal.ui.javaeditor.JavaAnnotationImageProvider tes;
			IFileEditorInput input = new FileEditorInput((IFile) marker
					.getResource());
			IEditorPart editorPart = page.findEditor(input);
			try {
				editorPart = IDE.openEditor(page, this.getLinkLocation(), true);
			} catch (PartInitException e) {
				MessageDialog.openError(w.getShell(),
						"ERROR", "Unable to open Editor!"); //$NON-NLS-1$
			}
		}

		private IFile getLinkLocation() {
			String fullPath = this.link.getProgramElementNode()
					.getSourceLocation().getSourceFile().getAbsolutePath();
			return (IFile) ProjectProperties.findResource(fullPath, Builder
					.getLastBuildTarget());
		}

	}
}