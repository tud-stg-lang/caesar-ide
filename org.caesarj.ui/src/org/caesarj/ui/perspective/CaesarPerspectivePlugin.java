package org.caesarj.ui.perspective;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */
 
import org.eclipse.ui.plugin.*;
import org.eclipse.core.resources.*;

/**
 * The main plugin class for the Perspective plug-in
 */
public class CaesarPerspectivePlugin extends AbstractUIPlugin {
	//The shared instance.
	private static CaesarPerspectivePlugin plugin;

	/**
	 * The constructor.
	 */
	public CaesarPerspectivePlugin() {
		//super(descriptor); Chenged was deprecated
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static CaesarPerspectivePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
}
