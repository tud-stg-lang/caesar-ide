/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: ProjectProperties.java,v 1.17 2010-10-21 13:44:49 satabin Exp $
 */

package org.caesarj.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.asm.CaesarJAsmManager;
import org.eclipse.core.internal.resources.Container;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Central Class for obtaining all relevant data from an IProject Object
 * 
 * TODO - Create a hook to remove projectproperties when projects are closed
 * TODO - Comment everything
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 */
public class ProjectProperties {
    
	/**
	 * Keep track of all project properties
	 */
	private static Hashtable projects = new Hashtable(5);
	
	private IProject project = null;
	private IJavaProject javaProject = null;
    private String outputPath = null;
    private List<String> sourcePaths = null;
    private String projectLocation = null;
    private StringBuffer classPath = null;
    private StringBuffer inPath = null;
    private List sourceFiles = null;
    private CaesarJAsmManager asmManager = null;
    private int worked = 0;
    
    private KjcEnvironment kjcEnv = null;
    
    /**
     * Creates a new not initialized ProjectProperties object.
     * 
     * @param project
     */
    private ProjectProperties(IProject project) {
    	this.project = project;
    	outputPath = "";
    	projectLocation = "";
    }
    
    /**
     * Factory method for creating a ProjectProperties object for a IProject.
     * 
     * @param project an IProject object
     * @return the ProjectProperties object associated to the IProject. Creates a new
     * instance if it doesn't exist.
     */
    public static ProjectProperties create(IProject project) {
    	Object properties = projects.get(project);
    	if (properties == null) {
    		properties = new ProjectProperties(project);
    		projects.put(project, properties);
    	}
    	return (ProjectProperties) properties;
    }

    /**
     * Initializes the ProjectProperties object.
     * 
     * @param project
     * @throws JavaModelException
     * @throws CoreException
     */
    public void refresh() throws JavaModelException, CoreException {
    	
    	classPath = new StringBuffer();
    	sourceFiles = new ArrayList();
    	
        javaProject = JavaCore.create(project);
        String projectLocalPrefix = File.separator + project.getName();
            
        /*
         * get paths
         */
        this.projectLocation = project.getLocation().removeLastSegments(1).toOSString();
        this.outputPath = javaProject.getOutputLocation().toOSString();
        this.sourcePaths = new ArrayList<String>();
        
        /*
         * get source files
         */
        IClasspathEntry[] classPathEntries = javaProject.getResolvedClasspath(false);

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
                if (! this.sourcePaths.contains(classPathEntries[i].getPath().toOSString())) {
                	this.sourcePaths.add(classPathEntries[i].getPath().toOSString());
                }
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
                
                // add the lib to inPath if specified in the .classpath file
                // as an inpath resource
                for(IClasspathAttribute attr : classPathEntries[i].getExtraAttributes()) {
                	if(attr.getName().equals("inpath") && attr.getValue().equals("true")) {
                		if(this.inPath.length()>0) {
        					this.inPath.append(File.pathSeparator);
        				}
                		this.inPath.append(cp);
                		break;
                	}
                }

                this.classPath.append(cp);
            }
        }
    }
    
    /**
     * 
     * @param resource
     * @param sourceFilesArg
     * @throws CoreException
     */
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
	
    /**
     * Represents the absolute path of the project directory
     * 
     * @return the project location directory
     */
	public String getProjectLocation() {
		return this.projectLocation;
	}
    
	/**
	 * Represents the relative path, from the Project Location,
	 * of the directory where the classes will be copied after
	 * compilation.
	 * 
	 * @return
	 */
    public String getOutputPath() {
        return this.outputPath;
    }

    /**
     * Return a list of strings representing the paths were we
     * can get sources for the project.
     * All paths are relative to the project location.
     * @return
     */
    public String[] getSourcePaths() {
    	return this.sourcePaths.toArray(new String[0]);
    }
    
    /**
     * A colon separated list of java resources, used as classpath for
     * the compiler
     * 
     * @return
     */
    public String getClassPath() {
        return this.classPath.toString();
    }
    
    /**
     * A colon seperated list of java resources, used as inpath for
     * the compiler
     * 
     * @return
     */
    public String getInPath() {
    	return this.inPath.toString();
    }

    /**
     * Represents the relative path, from the Project Location,
     * of the directory containing the sources.
     * 
     * @return
     */
	public Collection getSourceFiles() {		
		return this.sourceFiles;
	}
    
	public IProject getProject() {
		return project;
	}
	public IJavaProject getJavaProject() {
		return javaProject;
	}
	
	public CaesarJAsmManager getAsmManager() {
		return this.asmManager;
	}

	public void setAsmManager(CaesarJAsmManager asmManager) {
	    this.asmManager = asmManager;
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
    
	/**
	 * @return Returns the kjcEnv.
	 */
	public KjcEnvironment getKjcEnvironment() {
		return kjcEnv;
	}
	/**
	 * @param kjcEnv The kjcEnv to set.
	 */
	public void setKjcEnvironment(KjcEnvironment kjcEnv) {
		this.kjcEnv = kjcEnv;
	}

	public int getWorked() {
		return worked;
	}

	public void setWorked(int worked) {
		this.worked = worked;
	}
}
