/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: AdviceMarkerResolutionGenerator.java,v 1.16 2005-11-05 17:22:49 gasiunas Exp $
 */

package org.caesarj.ui.marker;

import org.apache.log4j.Logger;
import org.caesarj.compiler.asm.LinkNode;
import org.caesarj.ui.editor.CaesarEditor;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Shadow
 * 
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu
 * ändern: Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und
 * Kommentare
 */
public class AdviceMarkerResolutionGenerator implements	IMarkerResolutionGenerator, IMarkerResolutionGenerator2 {

	static Logger logger = Logger
			.getLogger(AdviceMarkerResolutionGenerator.class);

	public IMarkerResolution[] getResolutions(IMarker marker) {
		try {
			LinkNode advices[] = (LinkNode[]) marker.getAttribute(AdviceMarker.LINKS);
			IMarkerResolution res[] = new AdviceMarkerResolution[advices.length];
			for (int i = 0; i < advices.length; i++) {
				res[i] = new AdviceMarkerResolution(advices[i], marker);
			}
			return res;
		} catch (CoreException e) {
			logger.error("Fehler beim auslesen der LINKS aus AdviceMarker", e); //$NON-NLS-1$
		}
		return null;
	}

	public boolean hasResolutions(IMarker marker) {
		return true;
	}

	public class AdviceMarkerResolution implements IMarkerResolution {
		private LinkNode link;

		boolean toAdvice = false;

		public AdviceMarkerResolution(LinkNode linkArg, IMarker marker) {
			super();
			this.link = linkArg;
			try {
				this.toAdvice = marker.getAttribute(AdviceMarker.ID).equals(
						"AdviceLink"); //$NON-NLS-1$
			} catch (CoreException e) {
			}
		}

		public String getLabel() {
			return this.toAdvice ? "Open Advice: " + this.link.getName() : "Open Method: " + this.link.getName(); //$NON-NLS-1$ //$NON-NLS-2$
		}

		public void run(IMarker marker) {
			IWorkbenchWindow w = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (w == null)
				return;
			IWorkbenchPage page = w.getActivePage();
			if (page == null)
				return;
			IProject activeProject = ((CaesarEditor) page.getActiveEditor())
					.getInputJavaElement().getJavaProject().getProject();
			try {
				IDE.openEditor(page, this.getLinkLocation(activeProject), true);
			} catch (PartInitException e) {
				MessageDialog.openError(w.getShell(),
						"ERROR", "Unable to open Editor!"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		private IFile getLinkLocation(IProject activeProject) {
		    
			String fullPath = this.link.getTargetElement()
					.getSourceLocation().getSourceFile().getAbsolutePath();
			return (IFile) ProjectProperties.findResource(fullPath,
					activeProject);
		}

	}
}