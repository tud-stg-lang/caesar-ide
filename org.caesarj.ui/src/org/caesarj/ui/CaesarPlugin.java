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
 * $Id: CaesarPlugin.java,v 1.22 2005-02-15 17:39:12 gasiunas Exp $
 */

package org.caesarj.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.caesarj.ui.editor.CaesarEditor;
import org.caesarj.ui.editor.CaesarTextTools;
import org.caesarj.ui.preferences.CaesarJPreferences;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
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
	
	private CaesarTextTools caesarTextTools = null;

	private String aspectjRuntimePath = null;

	private String caesarRuntimePath = null;

	private String caesarCompilerPath = null;
	
	private ResourceBundle resourceBundle = null;

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
	public CaesarPlugin() {
		plugin = this;
		
		try {
			this.resourceBundle = ResourceBundle
					.getBundle("caesar.CaesarPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
	}
	
	public void initPluginUI() {
		if (selectionListener) {
			plugin.getWorkbench().getActiveWorkbenchWindow()
					.getSelectionService().addSelectionListener(plugin);
			selectionListener = false;
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

	public Display getDisplay() {
		return Display.getCurrent();
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
	
	public void selectionChanged(IWorkbenchPart part, ISelection selectionArg) {
		if (CaesarJPreferences.isAutoSwitch()) {
			if (part instanceof CaesarEditor) {
				CJDTConfigSettings.setAnalyzeAnnotations(false);
			} else if (part instanceof CompilationUnitEditor) {
				CJDTConfigSettings.setAnalyzeAnnotations(true);
			}
		}
	}
}