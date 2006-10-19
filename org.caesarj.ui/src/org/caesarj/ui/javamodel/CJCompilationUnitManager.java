/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Luzius Meisser - initial implementation
 *******************************************************************************/

package org.caesarj.ui.javamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.caesarj.ui.project.CaesarJProjectTools;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.OpenableElementInfo;

/**
 * Maintains a cache containing ICompilationUnits for .aj files and is
 * responsible for their instantiation.
 * 
 * @author Luzius Meisser
 */
public class CJCompilationUnitManager {

	public final static CJCompilationUnitManager INSTANCE = new CJCompilationUnitManager();

	private HashMap compilationUnitStore = new HashMap();

	public CJCompilationUnit getCJCompilationUnit(IFile file) {
		CJCompilationUnit unit = getCJCompilationUnitFromCache(file);
		if (unit != null)
			return unit;
		if (creatingCUisAllowedFor(file))
			unit = createCU(file);
		return unit;
	}

	public CJCompilationUnit getCJCompilationUnitFromCache(IFile file) {
		return (CJCompilationUnit) compilationUnitStore.get(file);
	}
	
	private CJCompilationUnit createCU(IFile file) {
		CJCompilationUnit unit = new CJCompilationUnit(file);

		try {
			OpenableElementInfo info = (OpenableElementInfo) ((JavaElement) unit
					.getParent()).getElementInfo();
			info.removeChild(unit); // Remove identical CompilationUnit if it exists
			info.addChild(unit);
			unit.openWhenClosed(unit.createElementInfo(), null);

			//enable java search (experimental) - leads to exceptions when
			// using
			//AJIndexManager.addSource(unit);

			compilationUnitStore.put(file, unit);
		} catch (JavaModelException e) {
		}
		return unit;
	}

	private boolean creatingCUisAllowedFor(IFile file) {
		return file != null && CaesarJProjectTools.isCJSource(file);
	}
	
	public void initCompilationUnits(IProject project) {
		List l = new ArrayList(30);
		addProjectToList(project, l);
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			IFile ajfile = (IFile) iter.next();
			createCU(ajfile);
		}
	}
	
	public void initCompilationUnits(IWorkspace workspace) {
		ArrayList l = new ArrayList(20);
		IProject[] projects = workspace.getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			addProjectToList(project, l);
			Iterator iter = l.iterator();
			while (iter.hasNext()) {
				IFile f = (IFile) iter.next();
				createCU(f);
			}
			l.clear();
		}		
	}
	
	private void addProjectToList(IProject project, List l) {
		if (CaesarJProjectTools.isCJProject(project)) {
			try {
				IJavaProject jp = JavaCore.create(project);
				IClasspathEntry[] cpes = jp.getRawClasspath();
				for (int i = 0; i < cpes.length; i++) {
					IClasspathEntry entry = cpes[i];
					if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IPath p = entry.getPath();
						if (p.segmentCount() == 1)
							addAllCJFilesInFolder(project, l);
						else
							addAllCJFilesInFolder(project.getFolder(p
									.removeFirstSegments(1)), l);
					}
				}
			} catch (JavaModelException e) {
			}
		}
	}
	
	/**
	 * @param folder
	 * @param list
	 */
	private void addAllCJFilesInFolder(IContainer folder, List l) {
		if ((folder == null) || !folder.exists())
			return;
		try {
			IResource[] children = folder.members();
			for (int i = 0; i < children.length; i++) {
				IResource resource = children[i];
				if (resource.getType() == IResource.FOLDER)
					addAllCJFilesInFolder((IFolder) resource, l);
				else if ((resource.getType() == IResource.FILE)
						&& CaesarJProjectTools.isCJSourceName(resource.getName()))
					l.add(resource);
			}
		} catch (CoreException e) {
		}
	}
	
	public List removeCUsfromJavaModel(IProject project) {
		List l = new ArrayList(30);
		addProjectToList(project, l);
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			removeFileFromModel((IFile) iter.next());
		}
		return l;
	}
	
	public void removeFileFromModel(IFile file) {
		CJCompilationUnit unit = (CJCompilationUnit) compilationUnitStore
				.get(file);
		if (unit != null) {
			try {
				// Fix for bug 106813 - check if the project is open first
				if(file.getProject().isOpen()) {					
					OpenableElementInfo info = (OpenableElementInfo) ((JavaElement) unit
							.getParent()).getElementInfo();
					info.removeChild(unit);
				}
				JavaModelManager.getJavaModelManager().removeInfoAndChildren(
						unit);

			} catch (JavaModelException e) {
			}
			compilationUnitStore.remove(file);
		}
	}
}