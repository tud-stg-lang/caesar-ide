package org.caesarj.ui;

import java.util.Hashtable;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;


public class CJDTConfigSettings {	
	
	static public boolean isAnalyzeAnnotationsDisabled() {
		IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();		
		return (store.getBoolean(PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS) == false) ? true : false;		
	}
	
	static public void disableAnalyzeAnnotations() {
		IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();		
		store.setValue(PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS, false);		
	}
	
	static public void enableAnalyzeAnnotations() {
		IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();		
		store.setValue(PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS, true);		
	}
	
	static public boolean isUnusedImportsDisabled() {
		Hashtable map = (Hashtable)JavaCore.getOptions();
		return ((String)map.get(JavaCore.COMPILER_PB_UNUSED_IMPORT)).equals(JavaCore.IGNORE) ? true : false;
	}
	
	static public void disableUnusedImports() {
		Hashtable map = (Hashtable)JavaCore.getOptions();
		map.put(JavaCore.COMPILER_PB_UNUSED_IMPORT, JavaCore.IGNORE);
		JavaCore.setOptions(map);
	}
	
	static public void enableUnusedImports() {
		Hashtable map = (Hashtable)JavaCore.getOptions();
		map.put(JavaCore.COMPILER_PB_UNUSED_IMPORT, JavaCore.IGNORE);
		JavaCore.setOptions(map);
	}
	
	static public boolean isCaesarJEditorDefault() {
		IEditorRegistry editorRegistry = WorkbenchPlugin.getDefault().getEditorRegistry();
		IEditorDescriptor desc = editorRegistry.getDefaultEditor("*.java");
		//TODO weiﬂ nicht was geht
		return CaesarPlugin.getResourceString(CaesarPlugin.ID_EDITOR).equals(desc.getLabel());
	}
	
	static public void enableCaesarJEditorDefault() {
		EditorRegistry editorRegistry = (EditorRegistry)WorkbenchPlugin.getDefault().getEditorRegistry();
		IFileEditorMapping[] array = WorkbenchPlugin.getDefault().getEditorRegistry().getFileEditorMappings();
		editorRegistry.setFileEditorMappings((FileEditorMapping[])array);
		editorRegistry.setDefaultEditor("*.java", "CompilationUnitEditor");
		editorRegistry.saveAssociations();	
	}

}
