package org.caesarj.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
			local= Platform.asLocalURL(installLocation);
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
