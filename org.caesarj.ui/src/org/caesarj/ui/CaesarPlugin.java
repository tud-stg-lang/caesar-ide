package org.caesarj.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.caesarj.ui.editor.CaesarEditor;
import org.caesarj.ui.editor.CaesarTextTools;
import org.caesarj.ui.preferences.CaesarJPreferences;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarPlugin extends AbstractUIPlugin implements
		ISelectionListener {
	// singleton
	private static CaesarPlugin plugin;

	public static final String 
		CAESAR_RUNTIME_LIB = "caesar-runtime.jar",
		ASPECTJ_RUNTIME_LIB = "aspectjrt.jar";

	public static final String PLUGIN_ID = "org.caesarj"; //$NON-NLS-1$

	public static final String ID_EDITOR = PLUGIN_ID + ".editor.CaesarEditor"; //$NON-NLS-1$

	public static final String ID_BUILDER = PLUGIN_ID + ".builder.builder"; //$NON-NLS-1$

	public static final String ID_OUTLINE = PLUGIN_ID + ".caesaroutlineview"; //$NON-NLS-1$

	public static final String ID_NATURE = PLUGIN_ID + ".caesarprojectnature"; //$NON-NLS-1$

	public static final String CAESAR_HOME = "CAESAR_HOME";

	private Display display = Display.getCurrent();

	private ResourceBundle resourceBundle = null;

	private CaesarTextTools caesarTextTools = null;

	private String aspectjRuntimePath = null;

	private String caesarRuntimePath = null;

	private String caesarCompilerPath = null;

	private String bcelPath = null;

	private static boolean selectionListener = true;

	/*
	 * private void updateTemplate(TemplatePersistenceData data) {
	 * TemplatePersistenceData[] datas=
	 * JavaPlugin.getDefault().getCodeTemplateStore().getTemplateData(true); for
	 * (int i= 0; i < datas.length; i++) { String id= datas[i].getId(); if (id !=
	 * null && id.equals(data.getId())) {
	 * datas[i].setTemplate(data.getTemplate()); break; } } }
	 * 
	 * private void import_() {
	 * 
	 * String path="";
	 * 
	 * if (path == null) return;
	 * 
	 * try { TemplateReaderWriter reader= new TemplateReaderWriter(); File file=
	 * new File(path); if (file.exists()) { Reader input= new FileReader(file);
	 * TemplatePersistenceData[] datas= reader.read(input); for (int i= 0; i <
	 * datas.length; i++) { updateTemplate(datas[i]); } }
	 * 
	 * fCodeTemplateTree.refresh();
	 * updateSourceViewerInput(fCodeTemplateTree.getSelectedElements()); } catch
	 * (FileNotFoundException e) { openReadErrorDialog(e); } catch (IOException
	 * e) { openReadErrorDialog(e); } }
	 */

	/**
	 * The constructor.
	 */
	public CaesarPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			this.resourceBundle = ResourceBundle
					.getBundle("caesar.CaesarPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
		if (selectionListener) {
			plugin.getWorkbench().getActiveWorkbenchWindow()
					.getSelectionService().addSelectionListener(plugin);
			selectionListener = false;
		}
		
		// load here the environment variable
		// otherwise error when importing a project which contains this variable in .classpath
	    JavaCore.getClasspathVariable(CAESAR_HOME);
	}

	/*
	 * So sollte es richtig sein ... Plugin startet dann blos nicht. public
	 * CaesarPlugin() { super(); plugin = this; try { resourceBundle =
	 * ResourceBundle.getBundle("caesar.CaesarPluginResources"); } catch
	 * (MissingResourceException x) { resourceBundle = null; } }
	 */

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
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
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
		return this.display;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return this.resourceBundle;
	}

	/**
	 * Returns Caesar text tools containing Caesar code scanner
	 */
	public CaesarTextTools getCaesarTextTools() {
		IPreferenceStore textToolPreferences;

		if (this.caesarTextTools == null) {
			textToolPreferences = JavaPlugin.getDefault().getPreferenceStore();
			this.caesarTextTools = new CaesarTextTools(textToolPreferences);
		}

		return this.caesarTextTools;
	}

	public String getCaesarRuntimeClasspath() {
		if (this.caesarRuntimePath == null)
			this.caesarRuntimePath = getPathFor(CAESAR_RUNTIME_LIB);

		return this.caesarRuntimePath;
	}

	public String getAspectJRuntimeClasspath() {
		if (this.aspectjRuntimePath == null) {
			this.aspectjRuntimePath = getPathFor(ASPECTJ_RUNTIME_LIB);
		}

		return this.aspectjRuntimePath;
	}

	private String getPathFor(String lib) {
		String res = CAESAR_HOME + "/" + lib;
		return res;
	}
	
	/*
	private String getPathFor(String lib) {
		StringBuffer cpath = new StringBuffer();

		IPluginRegistry reg = Platform.getPluginRegistry();

		int maj = 1;
		int min = 1;
		int svc = 1;
		try {
			StringTokenizer tok = new StringTokenizer(VERSION, "."); //$NON-NLS-1$
			maj = Integer.parseInt(tok.nextToken());
			min = Integer.parseInt(tok.nextToken());
			svc = Integer.parseInt(tok.nextToken());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// first look for the version we really want...
		IPluginDescriptor ajdePluginDesc = reg.getPluginDescriptor(PLUGIN_ID,
				new PluginVersionIdentifier(maj, min, svc));

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
			if (pluginLoc.startsWith("file:")) { //$NON-NLS-1$
				cpath.append(pluginLoc.substring("file:".length())); //$NON-NLS-1$
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
	*/

	public void selectionChanged(IWorkbenchPart part, ISelection selectionArg) {
		if (CaesarJPreferences.isCAESARAutoSwitch()) {
			if (part instanceof CaesarEditor) {
				CJDTConfigSettings.disableAnalyzeAnnotations();
			} else if (part instanceof CompilationUnitEditor) {
				CJDTConfigSettings.enableAnalyzeAnnotations();
			}
		}
	}
}