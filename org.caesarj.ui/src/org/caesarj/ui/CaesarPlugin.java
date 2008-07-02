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
 * $Id: CaesarPlugin.java,v 1.31 2008-07-02 18:30:30 gasiunas Exp $
 */

package org.caesarj.ui;

import org.caesarj.launching.CjStepFilterOptionManager;
import org.caesarj.ui.editor.CJIndexManager;
import org.caesarj.ui.javamodel.CJCompilationUnitDocumentProvider;
import org.caesarj.ui.javamodel.CJCompilationUnitManager;
import org.caesarj.ui.javamodel.ResourceChangeListener;
import org.caesarj.ui.preferences.CaesarJPreferences;
import org.caesarj.ui.project.CaesarJProjectTools;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.search.indexing.IndexManager;
import org.eclipse.jdt.internal.debug.ui.IJDIPreferencesConstants;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ICompilationUnitDocumentProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

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
	
	private String aspectjRuntimePath = null;

	private String caesarRuntimePath = null;

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
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setStepFilter();
		
		CaesarPlugin.getWorkspace().addResourceChangeListener(
				new ResourceChangeListener(),
				IResourceChangeEvent.PRE_CLOSE
						| IResourceChangeEvent.PRE_DELETE
						| IResourceChangeEvent.POST_CHANGE
						| IResourceChangeEvent.PRE_BUILD);
		
		IndexManager  indexManager = new CJIndexManager();
		JavaModelManager.getJavaModelManager().indexManager = indexManager;
		indexManager.reset();
		
		CJCompilationUnitManager.INSTANCE.initCompilationUnits(CaesarPlugin.getWorkspace());
		CaesarJProjectTools.refreshPackageExplorer();
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
		/*if (CaesarJPreferences.isAutoSwitch()) {
			if (part instanceof CaesarEditor) {
				CJDTConfigSettings.setAnalyzeAnnotations(false);
			} else if (part instanceof CompilationUnitEditor) {
				CJDTConfigSettings.setAnalyzeAnnotations(true);
			}
		}*/
	}
	
	protected final static String[] ACTIVE_FILTER = {};
	protected final static String[] INACTIVE_FILTER = {"org.caesarj.*", "org.aspectj.*"};
	public final static String[] FILTER = {"org.caesarj.*", "org.aspectj.*"
		//,"com.ibm.*","com.sun.*","java.*","javax.*","org.omg.*","sun.*","sunw.*","java.lang.ClassLoader"
	};
	
	/**
	 * Initializes the preferences for this plugin if necessary.
	 */
	protected void initializeDefaultPluginPreferences() {
		IPreferenceStore store = getPreferenceStore();
		
		store.setDefault(CaesarJPreferences.CAESAR_AUTO_SWITCH, false);
		store.setDefault(CaesarJPreferences.CAESAR_PREF_CONFIG_DONE, false);
		store.setDefault(CaesarJPreferences.CAESAR_ANALIZE_ANNOTATIONS, false);
		store.setDefault(CaesarJPreferences.CAESAR_IS_DEFAULT_EDITOR, true);
		store.setDefault(CaesarJPreferences.CAESAR_RUN_WEAVER, true);
	}

	/**
	 * Adds step filter.
	 */
	protected void setStepFilter(){
		IPreferenceStore store = JDIDebugUIPlugin.getDefault().getPreferenceStore();
		String afd = store.getDefaultString(IJDIPreferencesConstants.PREF_ACTIVE_FILTERS_LIST);
		String af = store.getString(IJDIPreferencesConstants.PREF_ACTIVE_FILTERS_LIST);
		int i;
		if(ACTIVE_FILTER.length > 0){
			for(i = 0; i < ACTIVE_FILTER.length; i++){
				if(afd.indexOf(ACTIVE_FILTER[i]) == -1){
					afd += "," + ACTIVE_FILTER[i];
				}
				if(af.indexOf(ACTIVE_FILTER[i]) == -1){
					af += "," + ACTIVE_FILTER[i];
				}
			}
			store.setDefault(IJDIPreferencesConstants.PREF_ACTIVE_FILTERS_LIST, afd);
			store.setValue(IJDIPreferencesConstants.PREF_ACTIVE_FILTERS_LIST, af);
		}
		String iafd = store.getDefaultString(IJDIPreferencesConstants.PREF_INACTIVE_FILTERS_LIST);
		String iaf = store.getString(IJDIPreferencesConstants.PREF_INACTIVE_FILTERS_LIST);
		if(INACTIVE_FILTER.length > 0){
			for(i = 0; i < INACTIVE_FILTER.length; i++){
				if(iafd.indexOf(INACTIVE_FILTER[i]) == -1){
					iafd += "," + INACTIVE_FILTER[i];
				}
				if(iaf.indexOf(INACTIVE_FILTER[i]) == -1){
					iaf += "," + INACTIVE_FILTER[i];
				}
			}
			store.setDefault(IJDIPreferencesConstants.PREF_INACTIVE_FILTERS_LIST, iafd);
			store.setValue(IJDIPreferencesConstants.PREF_INACTIVE_FILTERS_LIST, iaf);
		}				
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// Remove property-listener 
		DebugUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(CjStepFilterOptionManager.getDefault());
        super.stop(context);
    }
	
	private CJCompilationUnitDocumentProvider fCompilationUnitDocumentProvider = null;
	
	public synchronized ICompilationUnitDocumentProvider getCompilationUnitDocumentProvider() {
		if (fCompilationUnitDocumentProvider == null)
			fCompilationUnitDocumentProvider= new CJCompilationUnitDocumentProvider();
		return fCompilationUnitDocumentProvider;
	}
}