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
 * $Id: CaesarProjectNature.java,v 1.6 2005-01-24 16:57:22 aracic Exp $
 */

package org.caesarj.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;


/**
 * CaesarProjectNature, responsible for adding caesar specific build commands
 * and project behaviour.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarProjectNature implements IProjectNature {
    
    private static Logger log = Logger.getLogger(CaesarProjectNature.class);
    
    private IProject project;

    /**
     * Driven when this project nature is 'given' to a project,
     * it adds the appropriate builder to the project build specification.
     * scan the current list of builders, if it contains
     * 'org.eclipse.jdt.core.javabuilder' replace that entry with our entry,
     * otherwise simply insert our builder as a new entry.
     */
    public void configure() throws CoreException {
        IProjectDescription projectDescription = this.project.getDescription();
        ICommand command = projectDescription.newCommand();
        command.setBuilderName(CaesarPlugin.ID_BUILDER);
        log.debug("builder command = "+command.toString()); //$NON-NLS-1$

        ICommand[] buildCommands = projectDescription.getBuildSpec();
        ICommand[] newBuildCommands;
        if (contains(buildCommands, JavaCore.BUILDER_ID)) {
            newBuildCommands =
                swap(buildCommands, JavaCore.BUILDER_ID, command );
        } else {
            newBuildCommands = insert(buildCommands, command);        
        }
        
        for(int i=0; i<newBuildCommands.length;i++) {        
            log.debug(i+") builder command = "+newBuildCommands[i].toString()); //$NON-NLS-1$
        }
        
        projectDescription.setBuildSpec(newBuildCommands);
        this.project.setDescription(projectDescription, null);
    }

    /**
     * Remove the AspectJ Builder from the list, replace with the javabuilder
     */
    public void deconfigure() throws CoreException {
        IProjectDescription description = this.project.getDescription();
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
        this.project.setDescription(description, null);
    }

    /**
     * @see IProjectNature#getProject
     */
    public IProject getProject() {
        return this.project;
    }

    /**
     * @see IProjectNature#setProject
     */
    public void setProject(IProject value) {
        this.project = value;
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
