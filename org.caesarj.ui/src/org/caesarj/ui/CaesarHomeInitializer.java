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
 * $Id: CaesarHomeInitializer.java,v 1.4 2006-10-06 17:05:12 gasiunas Exp $
 */

package org.caesarj.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

/**
 * @author Vaidas Gasiunas
 *
 * Initializes CAESAR_HOME variable
 */
public class CaesarHomeInitializer extends ClasspathVariableInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ClasspathVariableInitializer#initialize(java.lang.String)
	 */
	public void initialize(String variable) {
		Bundle bundle= Platform.getBundle(CaesarPlugin.PLUGIN_ID); //$NON-NLS-1$
		if (bundle == null) {
			JavaCore.removeClasspathVariable(CaesarPlugin.CAESAR_HOME, null);
			return;
		}
		URL installLocation= bundle.getEntry("/"); //$NON-NLS-1$
		URL local= null;
		try {
			local= FileLocator.toFileURL(installLocation);
		} catch (IOException e) {
			JavaCore.removeClasspathVariable(CaesarPlugin.CAESAR_HOME, null);
			return;
		}
		try {
			String fullPath= new File(local.getPath()).getAbsolutePath();
			JavaCore.setClasspathVariable(CaesarPlugin.CAESAR_HOME, new Path(fullPath), null);
		} catch (JavaModelException e1) {
			JavaCore.removeClasspathVariable(CaesarPlugin.CAESAR_HOME, null);
		}

	}

}
