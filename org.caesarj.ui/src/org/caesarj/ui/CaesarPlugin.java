package org.caesarj.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.caesarj.ui.editor.CaesarTextTools;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.preferences.JavadocConfigurationPropertyPage;
import org.eclipse.jdt.internal.ui.preferences.PreferencesMessages;
import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateReaderWriter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** 
 * The main plugin class to be used in the desktop.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarPlugin extends AbstractUIPlugin {
	// singleton
	private static CaesarPlugin plugin;
	private ImageDescriptorRegistry fImageDescriptorRegistry;

	public static final String CAESAR_RUNTIME_LIB = "caesar-runtime.jar",
		ASPECTJ_RUNTIME_LIB = "aspectjrt.jar",
		CAESAR_COMPILER_LIB = "caesar-compiler.jar",
		BCEL_LIB = "bcel.jar";

	private static Logger log = Logger.getLogger(CaesarPlugin.class);

	public static final String VERSION = "0.1.2";
	public static final String PLUGIN_ID = "org.caesarj";
	public static final String ID_EDITOR = PLUGIN_ID + ".editor.CaesarEditor";
	public static final String ID_BUILDER = PLUGIN_ID + ".builder.builder";
	public static final String ID_OUTLINE = PLUGIN_ID + ".caesaroutlineview";
	public static final String ID_NATURE = PLUGIN_ID + ".caesarprojectnature";

	private Display display = Display.getCurrent();
	private ResourceBundle resourceBundle = null;
	private CaesarTextTools caesarTextTools = null;

	private String aspectjRuntimePath = null;
	private String caesarRuntimePath = null;
	private String caesarCompilerPath = null;
	private String bcelPath = null;


	/*
	private void updateTemplate(TemplatePersistenceData data) {
		TemplatePersistenceData[] datas= JavaPlugin.getDefault().getCodeTemplateStore().getTemplateData(true);
		for (int i= 0; i < datas.length; i++) {
			String id= datas[i].getId();
			if (id != null && id.equals(data.getId())) {
				datas[i].setTemplate(data.getTemplate());
				break;
			}
		}
	}
	
	private void import_() {
		
		String path="";
		
		if (path == null)
			return;
		
		try {
			TemplateReaderWriter reader= new TemplateReaderWriter();
			File file= new File(path);
			if (file.exists()) {
				Reader input= new FileReader(file);
				TemplatePersistenceData[] datas= reader.read(input);
				for (int i= 0; i < datas.length; i++) {
					updateTemplate(datas[i]);
				}
			}

			fCodeTemplateTree.refresh();
			updateSourceViewerInput(fCodeTemplateTree.getSelectedElements());

		} catch (FileNotFoundException e) {
			openReadErrorDialog(e);
		} catch (IOException e) {
			openReadErrorDialog(e);
		}

	}*/
	
	/**
	 * The constructor.
	 */
	public CaesarPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		
		try {
			resourceBundle = ResourceBundle.getBundle("caesar.CaesarPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}
	/* So sollte es richtig sein ... Plugin startet dann blos nicht.
	public CaesarPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("caesar.CaesarPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}*/

	/**
	 * Returns the shared instance.
	 */
	public static CaesarPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = CaesarPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public Display getDisplay() {
		return display;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * Returns Caesar text tools containing Caesar code scanner
	 */
	public CaesarTextTools getCaesarTextTools() {
		IPreferenceStore textToolPreferences;

		if (caesarTextTools == null) {
			textToolPreferences = JavaPlugin.getDefault().getPreferenceStore();
			caesarTextTools = new CaesarTextTools(textToolPreferences);
		}

		return caesarTextTools;
	}

	public String getCaesarRuntimeClasspath() {
		if (caesarRuntimePath == null)
			caesarRuntimePath = getPathFor(CAESAR_RUNTIME_LIB);

		return caesarRuntimePath;
	}

	public String getAspectJRuntimeClasspath() {
		if (aspectjRuntimePath == null)
			aspectjRuntimePath = getPathFor(ASPECTJ_RUNTIME_LIB);

		return aspectjRuntimePath;
	}

	public String getCaesarCompilerClasspath() {
		if (caesarCompilerPath == null)
			caesarCompilerPath = getPathFor(CAESAR_COMPILER_LIB);

		return caesarCompilerPath;
	}

	public String getBcelClasspath() {
		if (bcelPath == null)
			bcelPath = getPathFor(BCEL_LIB);

		return bcelPath;
	}

	private String getPathFor(String lib) {
		StringBuffer cpath = new StringBuffer();

		IPluginRegistry reg = Platform.getPluginRegistry();

		int maj = 1;
		int min = 1;
		int svc = 1;
		try {
			StringTokenizer tok = new StringTokenizer(VERSION, ".");
			maj = Integer.parseInt(tok.nextToken());
			min = Integer.parseInt(tok.nextToken());
			svc = Integer.parseInt(tok.nextToken());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// first look for the version we really want...
		IPluginDescriptor ajdePluginDesc =
			reg.getPluginDescriptor(PLUGIN_ID, new PluginVersionIdentifier(maj, min, svc));

		if (ajdePluginDesc == null) {
			// then try *any* version
			ajdePluginDesc = reg.getPluginDescriptor(PLUGIN_ID);
		}

		String pluginLoc = null;
		if (ajdePluginDesc != null) {
			URL installLoc = ajdePluginDesc.getInstallURL();
			URL resolved = null;
			try {
				resolved = Platform.resolve(installLoc);
				pluginLoc = resolved.toExternalForm();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (pluginLoc != null) {
			if (pluginLoc.startsWith("file:")) {
				cpath.append(pluginLoc.substring("file:".length()));
				cpath.append(lib);
			}
		}

		String res = null;

		// Verify that the file actually exists at the plugins location
		if (new File(cpath.toString()).exists()) {
			res = cpath.toString();
		}

		return res;
	}
}
