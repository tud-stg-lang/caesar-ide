package org.caesarj.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.caesarj.ui.editor.CaesarTextTools;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** 
 * The main plugin class to be used in the desktop.
 */
public class CaesarPlugin extends AbstractUIPlugin {
	// singleton
	private static CaesarPlugin plugin;
	
    public static final String RUNTIME_LIB = "caesar-runtime.jar";
    
    public static final String VERSION    = "0.1.1";
    public static final String PLUGIN_ID  = "org.caesarj.ui";
    public static final String ID_EDITOR  = PLUGIN_ID + ".editor.caesareditor";
    public static final String ID_BUILDER = PLUGIN_ID + ".builder.builder";
    public static final String ID_OUTLINE = PLUGIN_ID + ".caesaroutlineview";
    public static final String ID_NATURE  = PLUGIN_ID + ".caesarprojectnature";
        
    private Display display = Display.getCurrent();
    private ResourceBundle  resourceBundle  = null;
    private CaesarTextTools caesarTextTools = null;
    private String aspectjrtPath = null;    
    
	/**
	 * The constructor.
	 */
	public CaesarPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("caesar.CaesarPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}


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
		ResourceBundle bundle= CaesarPlugin.getDefault().getResourceBundle();
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

    /*
     * find the path to the caesar runtime libarary
     */
    public String getAspectjrtClasspath() {
        if (aspectjrtPath == null) {
            StringBuffer cpath = new StringBuffer();

            IPluginRegistry reg = Platform.getPluginRegistry();

            int maj = 1;
            int min = 1;
            int svc = 1;
            try {
                StringTokenizer tok = new StringTokenizer(VERSION,".");
                maj = Integer.parseInt(tok.nextToken());
                min = Integer.parseInt(tok.nextToken());
                svc = Integer.parseInt(tok.nextToken());
            } catch ( Exception ex ) {
                System.err.println( "Exception parsing AJDE version: " + ex );  
            }


            // first look for the version we really want...
            IPluginDescriptor ajdePluginDesc = 
                reg.getPluginDescriptor(PLUGIN_ID, new PluginVersionIdentifier(maj,min, svc));

            if (ajdePluginDesc == null) {
                // then try *any* version
                ajdePluginDesc = reg.getPluginDescriptor(PLUGIN_ID); 
            }

            String pluginLoc = null;
            if ( ajdePluginDesc != null ) {
                URL installLoc = ajdePluginDesc.getInstallURL();
                URL resolved = null;
                try {
                    resolved = Platform.resolve(installLoc);
                    pluginLoc = resolved.toExternalForm();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }           
            if ( pluginLoc != null ) {
                if ( pluginLoc.startsWith( "file:" )) {
                    cpath.append(pluginLoc.substring( "file:".length() ));  
                    cpath.append(RUNTIME_LIB);
                }
            }

            // Verify that the file actually exists at the plugins location
            // derived above. If not then it might be because we are inside
            // a runtime workbench. Check under the workspace directory.
            if (new File(cpath.toString()).exists())    
            {
                // File does exist under the plugins directory 
                aspectjrtPath = cpath.toString();
            }
        }
        
        return aspectjrtPath;   
    }
}
