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
 * $Id: ProjectProperties.java,v 1.8 2005-01-24 16:57:22 aracic Exp $
 */

package org.caesarj.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.internal.resources.Container;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Central Class for obtaining all relevant data from an IProject Object
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class ProjectProperties {
    
    private String outputPath;
    private String projectLocation;
    private StringBuffer classPath = new StringBuffer();
    private List sourceFiles = new ArrayList();
    
    
    public ProjectProperties(IProject project) throws JavaModelException, CoreException {
        IJavaProject jProject = JavaCore.create(project);        
           
        String projectLocalPrefix = File.separator + project.getName();
            
        /*
         * get paths
         */
        this.projectLocation = project.getLocation().removeLastSegments(1).toOSString();
        this.outputPath = jProject.getOutputLocation().toOSString();

        /*
         * get source files
         */
        IClasspathEntry[] classPathEntries = jProject.getResolvedClasspath(false);

        for(int i=0; i<classPathEntries.length; i++) {
            if(classPathEntries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                // 1st segment of the path has to be removed because it is added
                // again by findMember ending in duplicated first segment in the path
                getAllSourceFiles(
                    project.findMember(
                        classPathEntries[i].getPath().removeFirstSegments(1)
                    ),
                    this.sourceFiles
                );
            }               
            else if(classPathEntries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
        
                if(this.classPath.length()>0) {
					this.classPath.append(File.pathSeparator);
				}
        
                String cp = classPathEntries[i].getPath().toOSString(); 
                if(cp.startsWith(projectLocalPrefix)) {
                    cp =
                        this.projectLocation + 
                        classPathEntries[i].getPath().toOSString();
                }                

                this.classPath.append(cp);
            }
        }
    }

    private void getAllSourceFiles(IResource resource, List sourceFilesArg) throws CoreException {
        if(resource!=null) {        
            if(resource.getName().endsWith(".java") || resource.getName().endsWith(".cj")) { //$NON-NLS-1$ //$NON-NLS-2$
                sourceFilesArg.add(resource.getFullPath().toOSString());
            }
            else if(resource instanceof Container) {
                Container container = (Container)resource;
                IResource[] resources = container.members();
                for(int i=0; i<resources.length; i++) {
                    getAllSourceFiles(resources[i], sourceFilesArg);       
                }
            }
        }
    }
    
    public static IResource findResource(String fullPath, IProject p) {
    	
    	IPath projectPath= p.getLocation();
		IPath path = new Path(fullPath);
		
        if (projectPath.isPrefixOf(path)) {
            // remove project location and project name segment
            path = path.removeFirstSegments(projectPath.segmentCount());
        }
        // find resource relative to project
        IResource ret = p.findMember(path);
        return ret;        
    }


	public String getProjectLocation() {
		return this.projectLocation;
	}
        
    public String getOutputPath() {
        return this.outputPath;
    }

    public String getClassPath() {
        return this.classPath.toString();
    }

	public Collection getSourceFiles() {		
		return this.sourceFiles;
	}
    
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("projectLocation\n\t"+getProjectLocation()); //$NON-NLS-1$
        res.append("\noutputPath\n\t"+getOutputPath()); //$NON-NLS-1$
        res.append("\nclasspath\n\t"+getClassPath()); //$NON-NLS-1$
        res.append("\nsource files:\n"); //$NON-NLS-1$
        for(Iterator it=this.sourceFiles.iterator(); it.hasNext(); ) {
            res.append('\t');
            res.append(it.next());
            res.append('\n');
        }

        return res.toString();
    }
    
}
