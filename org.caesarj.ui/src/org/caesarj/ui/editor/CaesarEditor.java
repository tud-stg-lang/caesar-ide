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
 * $Id: CaesarEditor.java,v 1.30 2006-10-19 09:11:30 gasiunas Exp $
 */

package org.caesarj.ui.editor;

import org.apache.log4j.Logger;
import org.caesarj.ui.CaesarPlugin;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.javamodel.CJCompilationUnitManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jdt.ui.IWorkingCopyManagerExtension;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * CaesarEditor
 * 
 * TODO [feature] solve the problem with annotations, at the moment user has to
 * disable "Analyse Annotations while typing" TODO [feature] completer for
 * caesar keywords TODO [feature] add new file type (e.g.: .cj) in order to
 * avoid conflicts with coexisting pure java projects TODO [feature] add new
 * Wizardtype for Caesar Files
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */

public class CaesarEditor extends CompilationUnitEditor {

	/**
	 * @author Jochen
	 * 
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */

	private static Logger log = Logger.getLogger(CaesarEditor.class);

	private CaesarJContentOutlinePage outlineView;
	
	private CaesarJEditorTitleImageUpdater aspectJEditorErrorTickUpdater;

	//private CompositeRuler caesarVerticalRuler;

	public CaesarEditor() {
		super();
		// set ruler context menu id so that the caesar-ruler actions are used.
		setRulerContextMenuId("#CJCompilationUnitRulerContext");
		/* initialize auto annotation on/off switching */
		CaesarPlugin.getDefault().initPluginUI();
		aspectJEditorErrorTickUpdater = new CaesarJEditorTitleImageUpdater();
	}
	
    public void init(IEditorSite site, IEditorInput input)  throws PartInitException {
    	super.init(site, input);
    	log.debug("Initializing CaesarJ Editor."); //$NON-NLS-1$
		try {
			IPreferenceStore store = this.getPreferenceStore();
			CaesarTextTools textTools = new CaesarTextTools(store);
			JavaSourceViewerConfiguration svConfig = new CaesarSourceViewerConfiguration(
					textTools, this, store);
			setSourceViewerConfiguration(svConfig);
			log.debug("CaesarJ Editor Initialized."); //$NON-NLS-1$
		} catch (Exception e) {
			log.error("Initalizing CaesarJ Editor.", e); //$NON-NLS-1$
		}
    }
	
	public IJavaElement getInputJavaElement() {
		return super.getInputJavaElement();
	}

	public Object getAdapter(Class key) {
		if (key.equals(IContentOutlinePage.class)) {
			if (this.outlineView == null) {
				this.outlineView = new CaesarJContentOutlinePage(this, 
						getInputJavaElement().getResource().getProject());
				//this.outlineView.setEnabled(true);
			}
			return this.outlineView;
		}
		return super.getAdapter(key);
	}

	public void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fInput = (IFileEditorInput) input;
			ICompilationUnit unit = null;
			//in case it is a .aj file, we need to register it in the
			// WorkingCopyManager
			JavaUI.getWorkingCopyManager().connect(input);	
			unit = CJCompilationUnitManager.INSTANCE
					.getCJCompilationUnit(fInput.getFile());
			if (unit != null){
				IAnnotationModel annotationModel = getDocumentProvider().getAnnotationModel(input);
				JavaModelManager.getJavaModelManager().discardPerWorkingCopyInfo((CompilationUnit)unit);
				unit.becomeWorkingCopy((IProblemRequestor) annotationModel, null);
				((IWorkingCopyManagerExtension)JavaUI.getWorkingCopyManager()).setWorkingCopy(input, unit);
			}
		}
	}

	public void dispose() {
		/* Fix TR35: must call super to free reference to the file */
		super.dispose(); 
		
		log.debug("dispose"); //$NON-NLS-1$
		if(this.outlineView != null){
			// Enable doesn't make sense anymore. For this reason the setEnabled above
			// was removed. Now we call the static method to remove this instance from 
			// the list
			//this.outlineView.setEnabled(false);
			CaesarJContentOutlinePage.removeInstance(this.outlineView);
		}
		if (aspectJEditorErrorTickUpdater != null) {
			aspectJEditorErrorTickUpdater.dispose();
			aspectJEditorErrorTickUpdater = null;
		}
	}
	
	/**
	 * Update the title image
	 */
	
	public synchronized void updatedTitleImage(Image image) {
		// only let us update the image 
	}

	public synchronized void customUpdatedTitleImage(Image image) {
		// only let us update the image 
		super.updatedTitleImage(image);
	}
	
	public  void resetTitleImage() {
		refreshJob.setElement(getInputJavaElement());
		refreshJob.schedule();
	}
	
	private UpdateTitleImageJob refreshJob = new UpdateTitleImageJob();
	
	private class UpdateTitleImageJob extends UIJob {
		private IJavaElement elem;
		
		UpdateTitleImageJob() {
			super("CaesarJ editor title update job");
			setSystem(true);
		}

		public void setElement(IJavaElement element) {
			elem = element;
		}
		
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (elem != null) {
				aspectJEditorErrorTickUpdater.updateEditorImage(elem);
			}
			return Status.OK_STATUS;
		}
	}
	
	protected class CaesarJEditorTitleImageUpdater {

		private ImageDescriptorRegistry registry = JavaPlugin.getImageDescriptorRegistry();
		private final Image baseImage = registry.get(CaesarPluginImages.DESC_CAESAR_EDITOR);
		private final ProblemsLabelDecorator problemsDecorator;
		
		public CaesarJEditorTitleImageUpdater() {		
			problemsDecorator = new ProblemsLabelDecorator(registry);
		}
				
		public boolean updateEditorImage(IJavaElement jelement) {
			Image titleImage= getTitleImage();
			if (titleImage == null) {
				return false;
			}
			Image newImage = problemsDecorator.decorateImage(baseImage, jelement);
			if (titleImage != newImage) {
				customUpdatedTitleImage(newImage);
				return true;
			}
			return false;
		}

		
		public void dispose() {
			problemsDecorator.dispose();
		}

	}
}