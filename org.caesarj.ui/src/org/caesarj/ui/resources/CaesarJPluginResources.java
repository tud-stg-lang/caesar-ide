/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.ui.resources;

import java.util.ResourceBundle;

/**
 * This Class manages statically the resources for the CaesarJ Plugin.
 * 
 * Resources here are Internationalized Strings and Images
 * 
 * TODO - Bring the images resources from CaesarPluginImages to here
 * 
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 */
public class CaesarJPluginResources {

	/**
	 * The resource bundle carries internationalized messages
	 */
	private ResourceBundle messages = null;
	
	/**
	 * A singleton instance of this class
	 */
	private static CaesarJPluginResources instance = new CaesarJPluginResources();
	
	/**
	 * Private constructor to avoid more instances
	 *
	 */
	private CaesarJPluginResources() {
		try {
			messages = ResourceBundle.getBundle("org.caesarj.ui.resources.CaesarJPluginResources"); //$NON-NLS-1$
		} catch (Exception e) {
			messages = null;
		}
	}
	
	/**
	 * Returns an internationalized message for the key.
	 * 
	 * If the resource bundle for the messages could not be initialized, this method returns
	 * the empty string. Otherwise, returns the correspondent message in the current Locale.
	 * 
	 * @param key The key for the message
	 * @return The corresponding message or the empty string if a problem occurs. Never returns null.
	 */
	public static String getResourceString(String key) {
		if (instance.messages == null) {
			return "";
		}
		return instance.messages.getString(key);
	}
	
	

}
