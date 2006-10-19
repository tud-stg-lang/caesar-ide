/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Luzius Meisser - initial implementation
 *******************************************************************************/
package  org.caesarj.ui.javamodel;

import org.caesarj.ui.project.CaesarJProjectTools;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;

/**
 * Notifies the AJCompilationUnitManager if files got added or removed.
 * 
 * @author Luzius Meisser
 *  
 */
public class ResourceDeltaVisitor implements IResourceDeltaVisitor {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public ResourceDeltaVisitor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) {
		IResource myRes = delta.getResource();
		if (myRes.getType() == IResource.FILE) {
			switch (delta.getKind()) {
			case IResourceDelta.REMOVED:
				CJCompilationUnitTools.removeFileFromModelAndCloseEditors((IFile)myRes);
				CaesarJProjectTools.refreshPackageExplorer();
				break;
			case IResourceDelta.ADDED:
				CJCompilationUnitManager.INSTANCE.getCJCompilationUnit((IFile)myRes);
				CaesarJProjectTools.refreshPackageExplorer();
				break;
			}
		}
		return true;
	}
}