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

	// Determine display depth. If depth > 4 then we use high color images. Otherwise low color
	// images are used
	static {
		String pathSuffix = "icons/"; //$NON-NLS-1$
		try {
			fgIconBaseURL =
				new URL(CaesarPlugin.getDefault().getDescriptor().getInstallURL(), pathSuffix);
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
	
	public static final String IMG_ERROR = NAME_PREFIX + "error.gif";
	public static final String IMG_ADVICE = NAME_PREFIX + "advice.gif";
	public static final String IMG_ASPECT = NAME_PREFIX + "aspect.gif";
	public static final String IMG_JOINPOINT = NAME_PREFIX + "joinPoint.gif";
	public static final String IMG_OUT_PACKAGE = NAME_PREFIX + "packd_obj.gif";
	public static final String IMG_OUT_IMPORTS = NAME_PREFIX + "impc_obj.gif";
	public static final String IMG_IMPORTS = NAME_PREFIX + "imp_obj.gif";
	public static final String IMG_CODE = NAME_PREFIX + "code.gif";

	

	public static final ImageDescriptor DESC_ERROR = createManaged(IMG_ERROR);
	public static final ImageDescriptor DESC_ADVICE = createManaged(IMG_ADVICE);
	public static final ImageDescriptor DESC_ASPECT = createManaged(IMG_ASPECT);
	public static final ImageDescriptor DESC_JOINPOINT = createManaged(IMG_JOINPOINT);
	public static final ImageDescriptor DESC_OUT_PACKAGE = createManaged(IMG_OUT_PACKAGE);
	public static final ImageDescriptor DESC_OUT_IMPORTS = createManaged(IMG_OUT_IMPORTS);
	public static final ImageDescriptor DESC_IMPORTS = createManaged(IMG_IMPORTS);
	public static final ImageDescriptor DESC_CODE = createManaged(IMG_CODE);
	public static final ImageDescriptor DESC_COLLAB_CO = JavaPluginImages.DESC_OVR_CONSTRUCTOR;

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

	private static ImageDescriptor create(String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String name) throws MalformedURLException {
		if (fgIconBaseURL == null)
			throw new MalformedURLException();

		StringBuffer buffer = new StringBuffer(name);
		return new URL(fgIconBaseURL, buffer.toString());
	}

	//	---- Helper methods to access icons on the file system --------------------------------------

}