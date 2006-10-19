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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Luzius Meisser
 */
public class CJCompilationUnitTools {
	
	public static PackageFragment getParentPackage(IFile ajFile){
		IJavaProject jp = JavaCore.create(ajFile.getProject());
		IJavaElement elem = JavaModelManager.determineIfOnClasspath(ajFile, jp);
		if (elem == null){
			//not on classpath -> default package
			IPackageFragmentRoot root = jp.getPackageFragmentRoot(ajFile.getParent());
			elem = root.getPackageFragment(IPackageFragment.DEFAULT_PACKAGE_NAME);
		}
		if (elem instanceof PackageFragment){
			return (PackageFragment)elem;
		}
		//should never happen
		
		return null;
	}
	
	public static void removeCUsfromJavaModelAndCloseEditors(IProject project) {
		List removed = CJCompilationUnitManager.INSTANCE.removeCUsfromJavaModel(project);
		Iterator iter = removed.iterator();
		while (iter.hasNext()) {
			closeEditorForFile((IFile) iter.next());
		}
	}

	protected static void removeFileFromModelAndCloseEditors(IFile file) {
		CJCompilationUnitManager.INSTANCE.removeFileFromModel(file);
		closeEditorForFile(file);
	}

	private static void closeEditorForFile(IFile file) {
		IWorkbenchPage page = JavaPlugin.getActivePage();
		if (page != null) {
			IEditorPart part = page.findEditor(new FileEditorInput(file));
			if (part != null)
				if (!page.closeEditor(part, true))
					//in case user cancels closeEditor, we should not
					// remove unit from model
					//TODO: maybe throw exception (?)
					return;
		}
	}
}
