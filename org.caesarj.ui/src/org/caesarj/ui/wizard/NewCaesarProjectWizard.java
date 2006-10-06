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
 * $Id: NewCaesarProjectWizard.java,v 1.10 2006-10-06 17:04:38 gasiunas Exp $
 */

package org.caesarj.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.caesarj.ui.CaesarPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardFirstPage;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardSecondPage;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * NewCaesarProjectWizard
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class NewCaesarProjectWizard extends NewElementWizard implements IExecutableExtension {
    
    private static Logger log = Logger.getLogger(NewCaesarProjectWizard.class);
    
    protected JavaProjectWizardFirstPage fFirstPage;
    protected JavaProjectWizardSecondPage fSecondPage;
    private IConfigurationElement fConfigElement;
    
    /**
     * Create a new wizard
     */
    public NewCaesarProjectWizard() {
    	setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWJPRJ);
		//setDialogSettings(AspectJUIPlugin.getDefault().getDialogSettings());
		setWindowTitle("New CaesarJ Project Creation Wizard");
    }
    
    /*
     * @see Wizard#addPages
     */	
    public void addPages() {
        super.addPages();
        fFirstPage= new JavaProjectWizardFirstPage();
        addPage(fFirstPage);
        fFirstPage.setTitle("Create a CaesarJ Project");
		fFirstPage.setDescription("Create a CaesarJ project in the workspace.");
		fSecondPage= new JavaProjectWizardSecondPage(fFirstPage);
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
		    BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
		    IJavaProject javaProject = fSecondPage.getJavaProject();
	 		IProject project = javaProject.getProject();
	 		selectAndReveal(project);
			boolean completed = finalizeNewProject(javaProject, fFirstPage.getDetect());
			BasicNewProjectResourceWizard.updatePerspective(this.fConfigElement);
			res = completed;			
		}
		return res;
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
		return JavaCore.create(fFirstPage.getProjectHandle());
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
				addCaesarJNature(javaProject,true);
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
	
	/**
	 * Adds the CaesarJ Nature to the project
	 * 
	 * @param project
	 * @param prompt whether to prompt the user, or go with the defaults
	 * @throws CoreException
	 */
	private static void addCaesarJNature(final IJavaProject project, final boolean prompt)
			throws CoreException {
		// wrap up the operation so that an autobuild is not triggered in the
		// middle of the conversion
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				internal_addCaesarJNature(project, prompt);
			}
		};
		try {
			op.run(null);
		} catch (InvocationTargetException ex) {
		} catch (InterruptedException e) {
		}
	}
	
	private static void internal_addCaesarJNature(IJavaProject javaProject, boolean prompt) throws CoreException {
		//checkOutputFoldersForAJFiles(project);
		
		IProject project = javaProject.getProject();
		
		// add the CaesarJ Nature
		IProjectDescription description = project.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 1, prevNatures.length);
		newNatures[0] = CaesarPlugin.ID_NATURE;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);

		// add runtime libraries to the build path.
		CaesarPlugin caesarPlugin = CaesarPlugin.getDefault();
		
		addClassPath(javaProject, caesarPlugin.getAspectJRuntimeClasspath());
        addClassPath(javaProject, caesarPlugin.getCaesarRuntimeClasspath());			
	
		refreshPackageExplorer();
	}
	
	private static Job refreshJob;

	private static int previousExecutionTime;
	
	public static void refreshPackageExplorer() {
		int delay = 5*previousExecutionTime;
		if (delay < 250) {
			delay = 250;
		} else if (delay > 5000) {
			delay = 5000;
		}
		getRefreshPackageExplorerJob().schedule(delay);
	}

	// reuse the same Job to avoid excessive updates
	private static Job getRefreshPackageExplorerJob() {
		if (refreshJob == null) {
			refreshJob = new RefreshPackageExplorerJob();
		}
		return refreshJob;
	}

	private static class RefreshPackageExplorerJob extends UIJob {
		RefreshPackageExplorerJob() {
			super("Refresh package explorer");
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			long start = System.currentTimeMillis();
			PackageExplorerPart pep = PackageExplorerPart
					.getFromActivePerspective();
			if (pep != null) {
				pep.getTreeViewer().refresh();
			}
			previousExecutionTime = (int)(System.currentTimeMillis() - start);
			//System.out.println("refresh explorer: elapsed="+previousExecutionTime);
			return Status.OK_STATUS;
		}
	}
    
    private static void addClassPath(IJavaProject javaProject, String classPath) {
        try {
            IClasspathEntry[] originalCP = javaProject.getRawClasspath();            
            IClasspathEntry classpathEntry =
                JavaCore.newVariableEntry(
                    new Path(classPath), // library location
                    null,               // no source
                    null                // no source
                    );
            // Update the raw classpath with the new ajrtCP entry.
            int originalCPLength = originalCP.length;
            IClasspathEntry[] newClasspath =
                new IClasspathEntry[originalCPLength + 1];
            System.arraycopy(originalCP, 0, newClasspath, 0, originalCPLength);
            newClasspath[originalCPLength] = classpathEntry;
            javaProject.setRawClasspath(newClasspath, new NullProgressMonitor());
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }
}
