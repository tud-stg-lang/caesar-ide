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
