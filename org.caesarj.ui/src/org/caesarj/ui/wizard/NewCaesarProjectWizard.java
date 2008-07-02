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
 * $Id: NewCaesarProjectWizard.java,v 1.13 2008-07-02 18:30:30 gasiunas Exp $
 */

package org.caesarj.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.project.CaesarJProjectTools;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.actions.ShowInPackageViewAction;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * NewCaesarProjectWizard
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class NewCaesarProjectWizard extends NewElementWizard implements IExecutableExtension {
    
    private static Logger log = Logger.getLogger(NewCaesarProjectWizard.class);
    
    protected NewJavaProjectWizardPageOne fFirstPage;
    protected NewJavaProjectWizardPageTwo fSecondPage;
    private IConfigurationElement fConfigElement;
    
    /**
     * Create a new wizard
     */
    public NewCaesarProjectWizard() {
    	setDefaultPageImageDescriptor(CaesarPluginImages.DESC_WIZBAN_NEWCJPROJ);
		//setDialogSettings(AspectJUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("New CaesarJ Project Creation Wizard");
    }
    
    /*
     * @see Wizard#addPages
     */	
    public void addPages() {
        super.addPages();
        fFirstPage= new NewJavaProjectWizardPageOne();
        addPage(fFirstPage);
        fFirstPage.setTitle("Create a CaesarJ Project");
		fFirstPage.setDescription("Create a CaesarJ project in the workspace.");
		fSecondPage= new NewJavaProjectWizardPageTwo(fFirstPage);
        fSecondPage.setTitle("Build Settings");
        fSecondPage.setDescription("Define the build settings");
        addPage(fSecondPage);
    }		
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
    	fSecondPage.performFinish(monitor); // use the full progress monitor
    }
            
    /*
     * @see Wizard#performFinish
     */
    public boolean performFinish() {
    	log.debug("finish");
    	
		boolean res= super.performFinish();
		if (res) {
			final IJavaElement newElement= getCreatedElement();

			IWorkingSet[] workingSets= fFirstPage.getWorkingSets();
			if (workingSets.length > 0) {
				PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(newElement, workingSets);
			}

			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			selectAndReveal(fSecondPage.getJavaProject().getProject());	
			
			finalizeNewProject(fSecondPage.getJavaProject(), false);

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPart activePart= getActivePart();
					if (activePart instanceof IPackagesViewPart) {
						(new ShowInPackageViewAction(activePart.getSite())).run(newElement);
					}
				}
			});
		}
		return res;
	}
    
    private IWorkbenchPart getActivePart() {
		IWorkbenchWindow activeWindow= getWorkbench().getActiveWorkbenchWindow();
		if (activeWindow != null) {
			IWorkbenchPage activePage= activeWindow.getActivePage();
			if (activePage != null) {
				return activePage.getActivePart();
			}
		}
		return null;
	}
    
    protected void handleFinishException(Shell shell, InvocationTargetException e) {
        String title= NewWizardMessages.JavaProjectWizard_op_error_title; //$NON-NLS-1$
        String message= NewWizardMessages.JavaProjectWizard_op_error_create_message; //$NON-NLS-1$
        ExceptionHandler.handle(e, getShell(), title, message);
    }
    
    /*
     * Stores the configuration element for the wizard.  The config element will be used
     * in <code>performFinish</code> to set the result perspective.
     */
    public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
        fConfigElement= cfig;
    }
    
    /* (non-Javadoc)
     * @see IWizard#performCancel()
     */
    public boolean performCancel() {
        fSecondPage.performCancel();
        return super.performCancel();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    public boolean canFinish() {
        return super.canFinish();
    }

	public IJavaElement getCreatedElement() {
		return fSecondPage.getJavaProject();
	}
    
    /**
	 * Builds and adds the necessary properties to the new project and updates the workspace view
	 */
	private boolean finalizeNewProject(IJavaProject javaProject, boolean alreadyExists) {
		IProject project = javaProject.getProject();
        final IProject thisProject = project;
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			
			// The nature to add is the PluginID+NatureID - it is not the
			// name of the class implementing IProjectNature !!
			// When the nature is attached, the project will be driven through
			// INatureProject.configure() which will replace the normal javabuilder
			// with the caesarj builder.
			if(!alreadyExists) {
				CaesarJProjectTools.addCaesarJNature(javaProject,true);
			}
			
			else {
				dialog.run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException {
						monitor.beginTask("", 2); //$NON-NLS-1$
						try {
							monitor.setTaskName("Building project...");
							thisProject.build(
									IncrementalProjectBuilder.FULL_BUILD,
									new SubProgressMonitor(monitor, 2));
						} catch (CoreException e) {
						} finally {
							monitor.done();
						}
					}
				});
			}
		} catch(InterruptedException e) {
			// build cancelled by user
			return false;
		} catch(InvocationTargetException e) {
			String title = "Error";
			String message = "An error occured while trying to create the project";
			ExceptionHandler.handle(e, getShell(), title, message);
		} catch(CoreException e) {
		}
		
		project = thisProject;
		//AspectJPlugin.getDefault().setCurrentProject( project );
		selectAndReveal(project);
		return true;
	}
}
