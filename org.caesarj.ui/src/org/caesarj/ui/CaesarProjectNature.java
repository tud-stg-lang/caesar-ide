package org.caesarj.ui;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IWorkbench;


/**
 * @author ia
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CaesarProjectNature implements IProjectNature {
    private IProject project;

    /**
     * Driven when this project nature is 'given' to a project,
     * it adds the appropriate builder to the project build specification.
     * scan the current list of builders, if it contains
     * 'org.eclipse.jdt.core.javabuilder' replace that entry with our entry,
     * otherwise simply insert our builder as a new entry.
     */
    public void configure() throws CoreException {
        System.out.println("CaesarProjectNature.configure");
        IProjectDescription projectDescription = project.getDescription();
        ICommand command = projectDescription.newCommand();
        command.setBuilderName(CaesarPlugin.ID_BUILDER);
        System.out.println("builder command = "+command.toString());

        ICommand[] buildCommands = projectDescription.getBuildSpec();
        ICommand[] newBuildCommands;
        if (contains(buildCommands, JavaCore.BUILDER_ID)) {
            newBuildCommands =
                swap(buildCommands, JavaCore.BUILDER_ID, command );
        } else {
            newBuildCommands = insert(buildCommands, command);        
        }
        
        for(int i=0; i<newBuildCommands.length;i++) {        
            System.out.println(i+") builder command = "+newBuildCommands[i].toString());
        }
        
        projectDescription.setBuildSpec(newBuildCommands);
        project.setDescription(projectDescription, null);
    
        IWorkbench workbench = CaesarPlugin.getDefault().getWorkbench();
        workbench.showPerspective( "org.eclipse.jdt.ui.JavaPerspective" , workbench.getActiveWorkbenchWindow() );
    }

    /**
     * Remove the AspectJ Builder from the list, replace with the javabuilder
     */
    public void deconfigure() throws CoreException {
        System.out.println("CaesarProjectNature.deconfigure");
        IProjectDescription description = project.getDescription();
        ICommand[] buildCommands = description.getBuildSpec();
        ICommand command = description.newCommand();
        command.setBuilderName(JavaCore.BUILDER_ID);
    
        ICommand[] newBuildCommands;
        if ( contains( buildCommands, CaesarPlugin.ID_BUILDER ) ) {
            newBuildCommands = swap( buildCommands, CaesarPlugin.ID_BUILDER, command );
        } else {
            newBuildCommands = remove( buildCommands, CaesarPlugin.ID_BUILDER );
        }           
    
        description.setBuildSpec(newBuildCommands);
        project.setDescription(description, null);
    }

    /**
     * @see IProjectNature#getProject
     */
    public IProject getProject() {
        return project;
    }

    /**
     * @see IProjectNature#setProject
     */
    public void setProject(IProject value) {
        project = value;
    }

    /**
     * Check if the given biuld command list contains a given command
     */
    private boolean contains(ICommand[] commands, String builderId) {
        boolean found = false;
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getBuilderName().equals(builderId)) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * In a list of build commands, swap all occurences of one entry for
     * another
     */
    private ICommand[] swap(
        ICommand[] sourceCommands,
        String oldBuilderId,
        ICommand newCommand) 
    {
        ICommand[] newCommands = new ICommand[sourceCommands.length];
        for ( int i = 0; i < sourceCommands.length; i++ ) {
            if ( sourceCommands[i].getBuilderName( ).equals( oldBuilderId ) ) {
                newCommands[i] = newCommand;
            } else {
                newCommands[i] = sourceCommands[i];
            }
        }   
        return newCommands; 
    }

    /**
     * Insert a new build command at the front of an existing list
     */
    private ICommand[] insert( ICommand[] sourceCommands, ICommand command ) {
        ICommand[] newCommands = new ICommand[ sourceCommands.length + 1 ];
        newCommands[0] = command;
        for (int i = 0; i < sourceCommands.length; i++ ) {
            newCommands[i+1] = sourceCommands[i];
        }       
        return newCommands;     
    }

    /**
     * Remove a build command from a list
     */
        /**
     * Insert a new build command at the front of an existing list
     */
    private ICommand[] remove( ICommand[] sourceCommands, String builderId ) {
        ICommand[] newCommands = new ICommand[ sourceCommands.length - 1 ];
        int newCommandIndex = 0;
        for (int i = 0; i < sourceCommands.length; i++ ) {
            if ( !sourceCommands[i].getBuilderName( ).equals( builderId ) ) {
                newCommands[newCommandIndex++] = sourceCommands[i];
            }
        }       
        return newCommands;     
    }
}
