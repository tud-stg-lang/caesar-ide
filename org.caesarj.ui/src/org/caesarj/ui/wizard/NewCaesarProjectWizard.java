package org.caesarj.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.caesarj.ui.CaesarPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * NewCaesarProjectWizard
 * 
 * @author Ivica Aracic
 */
public class    NewCaesarProjectWizard
extends         BasicNewProjectResourceWizard
implements      IExecutableExtension {
    
    private static Logger log = Logger.getLogger(NewCaesarProjectWizard.class);
    
    private NewJavaProjectWizardPage fJavaPage;
    private WizardNewProjectCreationPage fMainPage;
    private IConfigurationElement fConfigElement;
    
    /**
     * Create a new wizard
     */
    public NewCaesarProjectWizard() {
        super();
        setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWJPRJ);
        //setDialogSettings(AspectJPlugin.getDefault().getDialogSettings());
        //setWindowTitle(AspectJPlugin.getResourceString("NewAspectjProjectCreationWizard.title"));
    }
    
    /*
     * @see Wizard#addPages
     */
    public void addPages() {
            
        //super.addPages();
        fMainPage = new WizardNewProjectCreationPage("NewAspectjProjectCreationWizard");
         
        /*  
        fMainPage.setTitle(
            AspectJPlugin.getResourceString("NewAspectjProjectCreationWizard.MainPage.title"));
        fMainPage.setDescription(
            AspectJPlugin.getResourceString("NewAspectjProjectCreationWizard.MainPage.description"));
        */  
        addPage(fMainPage);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        fJavaPage = new NewJavaProjectWizardPage(root, fMainPage);
        addPage(fJavaPage);
    }
    
    /*
     * @see Wizard#performFinish
     */
    public boolean performFinish() {
        log.debug("finish");
        
        IRunnableWithProgress op =
            new WorkspaceModifyDelegatingOperation(fJavaPage.getRunnable());
        try {
            getContainer().run(false, true, op);
        } catch (InvocationTargetException e) {
            /*
            String title =
                AspectJPlugin.getResourceString("NewAspectjProjectCreationWizard.op_error.title");
            String message =
                AspectJPlugin.getResourceString("NewAspectjProjectCreationWizard.op_error.message");
            ExceptionHandler.handle(e, getShell(), title, message);
            */
            return false;            
        } catch (InterruptedException e) {
            return false;
        }
        BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
    
        IProject project = fJavaPage.getNewJavaProject().getProject();
        
        IJavaProject javaProject = fJavaPage.getNewJavaProject();

        /*
         * SETUP PROJECT NATURE
         */
        try {  
            log.debug("setting up project nature");      
            IProjectDescription description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length] = CaesarPlugin.ID_NATURE;                       
            description.setNatureIds(newNatures);
            project.setDescription(description, null);            
        }
        catch (Exception e) {
            log.debug("> Error creating new Caesar project: " + e);
            e.printStackTrace();
		}
        
        _addCaesarJarToClasspath(javaProject);

        return true;
    }
    
    private void _addCaesarJarToClasspath(IJavaProject javaProject) {
    
        try {
            // Locate the runtime jar
            String ajrtPath =
                CaesarPlugin.
                getDefault().            
                getAspectjrtClasspath();

            IClasspathEntry[] originalCP = javaProject.getRawClasspath();
            IClasspathEntry ajrtCP =
                JavaCore.newLibraryEntry(
                    new Path(ajrtPath), // library location
                    null,               // no source
                    null                // no source
                    );
            // Update the raw classpath with the new ajrtCP entry.
            int originalCPLength = originalCP.length;
            IClasspathEntry[] newCP =
                new IClasspathEntry[originalCPLength + 1];
            System.arraycopy(originalCP, 0, newCP, 0, originalCPLength);
            newCP[originalCPLength] = ajrtCP;
            javaProject.setRawClasspath(newCP, new NullProgressMonitor());
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Stores the configuration element for the wizard.  The config element will be used
     * in <code>performFinish</code> to set the result perspective.
     */
    public void setInitializationData(
        IConfigurationElement cfig,
        String propertyName,
        Object data) {
        fConfigElement = cfig;
    }
}
