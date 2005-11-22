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
 * $Id: CaesarPluginImages.java,v 1.17 2005-11-22 12:50:19 meffert Exp $
 */

package org.caesarj.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Bundle of most images used by the Java plugin.
 */
public class CaesarPluginImages {

	private static final String NAME_PREFIX = "org.eclipse.caesar.ui."; //$NON-NLS-1$
	private static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();

	private static URL fgIconBaseURL = null;

	static {
		String pathSuffix = "icons/"; //$NON-NLS-1$
		try {
			fgIconBaseURL =
				new URL(CaesarPlugin.getDefault().getBundle().getEntry("/"), pathSuffix);
		} catch (MalformedURLException e) {
			// do nothing
		}
	}

	// The plugin registry
	private static ImageRegistry fgImageRegistry = null;
	private static HashMap fgAvoidSWTErrorMap = null;

	/*
	 * Available cached Images in the Java plugin image registry.
	 */
	
	public static final String IMG_ERROR = NAME_PREFIX + "error.gif"; //$NON-NLS-1$
	public static final String IMG_ADVICE = NAME_PREFIX + "advice.gif"; //$NON-NLS-1$
	public static final String IMG_ASPECT = NAME_PREFIX + "aspect.gif"; //$NON-NLS-1$
	public static final String IMG_POINTCUT = NAME_PREFIX + "PointCut.gif"; //$NON-NLS-1$
	public static final String IMG_JOINPOINT_FORWARD = NAME_PREFIX + "joinPointForward.gif";
	public static final String IMG_JOINPOINT_BACK = NAME_PREFIX + "joinPointBack.gif";
	public static final String IMG_OUT_PACKAGE = NAME_PREFIX + "packd_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OUT_IMPORTS = NAME_PREFIX + "impc_obj.gif"; //$NON-NLS-1$
	public static final String IMG_IMPORTS = NAME_PREFIX + "imp_obj.gif"; //$NON-NLS-1$
	public static final String IMG_CODE = NAME_PREFIX + "code.gif"; //$NON-NLS-1$
	private static final String IMG_CCLASS_PUB = NAME_PREFIX + "cclass_public_obj.gif"; //$NON-NLS-1$	
	private static final String IMG_CCLASS_PRI = NAME_PREFIX + "cclass_private_obj.gif"; //$NON-NLS-1$
	private static final String IMG_CCLASS_PRO = NAME_PREFIX + "cclass_protected_obj.gif"; //$NON-NLS-1$
	public static final String IMG_CCLASS = NAME_PREFIX + "cclass_default_obj.gif"; //$NON-NLS-1$
	private static final String IMG_CCLASS_IMPLICID = NAME_PREFIX + "cclass_implicid_obj.gif"; //$NON-NLS-1$
	private static final String IMG_CCLASS_IMPLICID_DISABLED= NAME_PREFIX + "cclass_implicid_dis_obj.gif"; //$NON-NLS-1$
	private static final String IMG_ADVICE_NODE = NAME_PREFIX + "advice_node.gif";
	//TODO IMAGE HIERACHY
	private static final String IMG_HIER_MODE_SUPER = NAME_PREFIX + "super_hi.gif"; //$NON-NLS-1$
	private static final String IMG_HIER_MODE_SUB = NAME_PREFIX + "sub_hi.gif"; //$NON-NLS-1$
	
	

	public static final ImageDescriptor DESC_ERROR = createManaged(IMG_ERROR);
	public static final ImageDescriptor DESC_ADVICE = createManaged(IMG_ADVICE);
	public static final ImageDescriptor DESC_ASPECT = createManaged(IMG_ASPECT);
	public static final ImageDescriptor DESC_JOINPOINT_FORWARD = createManaged(IMG_JOINPOINT_FORWARD);
	public static final ImageDescriptor DESC_JOINPOINT_BACK = createManaged(IMG_JOINPOINT_BACK);
	public static final ImageDescriptor DESC_POINTCUT = createManaged(IMG_POINTCUT);
	public static final ImageDescriptor DESC_OUT_PACKAGE = createManaged(IMG_OUT_PACKAGE);
	public static final ImageDescriptor DESC_OUT_IMPORTS = createManaged(IMG_OUT_IMPORTS);
	public static final ImageDescriptor DESC_IMPORTS = createManaged(IMG_IMPORTS);
	public static final ImageDescriptor DESC_CODE = createManaged(IMG_CODE);
	public static final ImageDescriptor DESC_COLLAB_CO = JavaPluginImages.DESC_OVR_CONSTRUCTOR;
	public static final ImageDescriptor DESC_OBJS_INNER_CCLASS_PUBLIC = createManaged(IMG_CCLASS_PUB);
	public static final ImageDescriptor DESC_OBJS_INNER_CCLASS_PRIVATE = createManaged(IMG_CCLASS_PRI);
	public static final ImageDescriptor DESC_OBJS_INNER_CCLASS_PROTECTED = createManaged(IMG_CCLASS_PRO);
	public static final ImageDescriptor DESC_OBJS_INNER_CCLASS_DEFAULT = createManaged(IMG_CCLASS);
	public static final ImageDescriptor DESC_OBJS_INNER_CCLASS_IMPLICID = createManaged(IMG_CCLASS_IMPLICID);
	public static final ImageDescriptor DESC_OBJS_INNER_CCLASS_IMPLICID_DISABLED= createManaged(IMG_CCLASS_IMPLICID_DISABLED);
	public static final ImageDescriptor DESC_HIER_MODE_SUPER = createManaged(IMG_HIER_MODE_SUPER);
	public static final ImageDescriptor DESC_HIER_MODE_SUB = createManaged(IMG_HIER_MODE_SUB);
	public static final ImageDescriptor DESC_ADVICE_NODE =  createManaged(IMG_ADVICE_NODE);
	
	/**
	 * Returns the image managed under the given key in this registry.
	 * 
	 * @param key the image's key
	 * @return the image managed under the given key
	 */
	public static Image get(String key) {
		return getImageRegistry().get(key);
	}

	private static ImageDescriptor createManaged(String name) {
		try {
			ImageDescriptor result =
				ImageDescriptor.createFromURL(makeIconFileURL(name.substring(NAME_PREFIX_LENGTH)));
			if (fgAvoidSWTErrorMap == null) {
				fgAvoidSWTErrorMap = new HashMap();
			}
			fgAvoidSWTErrorMap.put(name, result);
			if (fgImageRegistry != null) {
				JavaPlugin.logErrorMessage("Image registry already defined"); //$NON-NLS-1$
			}
			return result;
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/*
	 * Helper method to access the image registry from the JavaPlugin class.
	 */
	/* package */
	static ImageRegistry getImageRegistry() {
		if (fgImageRegistry == null) {
			fgImageRegistry = new ImageRegistry();
			for (Iterator iter = fgAvoidSWTErrorMap.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				fgImageRegistry.put(key, (ImageDescriptor) fgAvoidSWTErrorMap.get(key));
			}
			fgAvoidSWTErrorMap = null;
		}
		return fgImageRegistry;
	}

//	private static ImageDescriptor create(String name) {
//		try {
//			return ImageDescriptor.createFromURL(makeIconFileURL(name));
//		} catch (MalformedURLException e) {
//			return ImageDescriptor.getMissingImageDescriptor();
//		}
//	}

	private static URL makeIconFileURL(String name) throws MalformedURLException {
		if (fgIconBaseURL == null)
			throw new MalformedURLException();

		StringBuffer buffer = new StringBuffer(name);
		return new URL(fgIconBaseURL, buffer.toString());
	}

	//	---- Helper methods to access icons on the file system --------------------------------------

}