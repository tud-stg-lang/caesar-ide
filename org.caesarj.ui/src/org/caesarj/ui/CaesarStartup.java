/*
 * Created on 18.11.2004
 */
package org.caesarj.ui;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IStartup;

/**
 * @author Vaidas Gasiunas
 *
 * Startup extension point 
 */
public class CaesarStartup implements IStartup {

	/* 
	 * Called on Eclipse startup
	 */
	public void earlyStartup() {
		// load here the environment variable
		// otherwise error when importing a project which contains this variable in .classpath
	    JavaCore.getClasspathVariable(CaesarPlugin.CAESAR_HOME);	    
	}
}
