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
 * $Id: CJDTConfigSettings.java,v 1.7 2005-01-24 16:57:22 aracic Exp $
 */

package org.caesarj.ui;

import java.util.Hashtable;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.JavaUI;
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
		Hashtable map = JavaCore.getOptions();
		return ((String)map.get(JavaCore.COMPILER_PB_UNUSED_IMPORT)).equals(JavaCore.IGNORE) ? true : false;
	}
	
	static public void disableUnusedImports() {
		Hashtable map = JavaCore.getOptions();
		map.put(JavaCore.COMPILER_PB_UNUSED_IMPORT, JavaCore.IGNORE);
		JavaCore.setOptions(map);
	}
	
	static public void enableUnusedImports() {
		Hashtable map = JavaCore.getOptions();
		map.put(JavaCore.COMPILER_PB_UNUSED_IMPORT, JavaCore.IGNORE);
		JavaCore.setOptions(map);
	}
	
	static public boolean isCaesarJEditorDefault() {
		IEditorRegistry editorRegistry = WorkbenchPlugin.getDefault().getEditorRegistry();
		IEditorDescriptor desc = editorRegistry.getDefaultEditor("*.java"); //$NON-NLS-1$
		
		return "CaesarEditor".equals(desc.getLabel()); //$NON-NLS-1$
	}
	
	static public void enableCaesarJEditorDefault() {
		EditorRegistry editorRegistry = (EditorRegistry)WorkbenchPlugin.getDefault().getEditorRegistry();
		IFileEditorMapping[] array = WorkbenchPlugin.getDefault().getEditorRegistry().getFileEditorMappings();
		editorRegistry.setFileEditorMappings((FileEditorMapping[])array);
		editorRegistry.setDefaultEditor("*.java", "org.caesarj.ui.editor.CaesarEditor"); //$NON-NLS-1$ //$NON-NLS-2$
		editorRegistry.saveAssociations();
	
	}
	static public void disableCaesarJEditorDefault() {
		EditorRegistry editorRegistry = (EditorRegistry)WorkbenchPlugin.getDefault().getEditorRegistry();
		IFileEditorMapping[] array = WorkbenchPlugin.getDefault().getEditorRegistry().getFileEditorMappings();
		editorRegistry.setFileEditorMappings((FileEditorMapping[])array);
		editorRegistry.setDefaultEditor("*.java", JavaUI.ID_CU_EDITOR); //$NON-NLS-1$
		editorRegistry.saveAssociations();
			
	}

}
