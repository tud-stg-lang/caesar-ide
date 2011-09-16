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
package org.caesarj.launching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.aspectj.asm.IProgramElement;
import org.caesarj.compiler.asm.CaesarProgramElement;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * Performs single click launching for local Caesar applications.
 * TODO: inform the user if no launchable entity could be found.
 * 
 * @author meffert
 */
public class CaesarLaunchShortcut implements ILaunchShortcut {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers.ISelection, java.lang.String)
	 */
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			searchAndLaunch(((IStructuredSelection)selection).toArray(), mode);
		}else{
			System.out.println("CjLaunchShortcut: selection is not a structured selection");
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart, java.lang.String)
	 */
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IJavaElement je = (IJavaElement) input.getAdapter(IJavaElement.class);
		if(je != null){
			searchAndLaunch(new Object[]{je}, mode);
		}else{
			System.out.println("CjLaunchShortcut: java element is null");
		}
	}

	/**
	 * Search for a main type and launch it.
	 * 
	 * @param search	the java elements to search for a main type
	 * @param mode		the launch mode
	 */
	protected void searchAndLaunch(Object[] search, String mode) {
		if(search == null){
			return;
		}
		String typeName = null;
		String projectName = null;
		for(int i=0; i<search.length && typeName == null; i++){
			if(search[i] instanceof IJavaElement){
				typeName = findRunnableType((IJavaElement)search[i]);
				if(typeName != null){
					projectName = ((IJavaElement)search[i]).getResource().getProject().getName();
				}
			}
		}
		
		if(typeName != null && projectName != null){
			launch(typeName, projectName, mode);
		}else{
			System.out.println("CjLaunchShortcut: no runnable type found");
		}
	}
	
	/**
	 * If the given java element contains a main type, the return value is the full
	 * qualified type name, otherwise it is null.
	 * 
	 * @param je	the java root element to search for a main type
	 * @return		the full qualified type name or null
	 */
	protected String findRunnableType(IJavaElement je){
		IResource res = je.getResource();
		if(res == null){
			return null;
		}
		String filename = res.getLocation().toFile().getAbsolutePath();
		IProject project = res.getProject();
		ProjectProperties properties = ProjectProperties.create(project);
		IProgramElement pe = findSourceFileNode(properties.getAsmManager().getHierarchy().getRoot(), filename);
		
		Object current = null;
		CaesarProgramElement currentCe = null;
		boolean foundRunnable = false;
		for (Iterator it = pe.getChildren().iterator(); it.hasNext() && !foundRunnable;) {
			current = it.next();
			if(current instanceof CaesarProgramElement){
				currentCe = (CaesarProgramElement)current;
				if(currentCe.getCaesarKind().isType()){
					foundRunnable = isTypeNodeRunnable(currentCe);
				}
			}
		}
		if(foundRunnable){
			return currentCe.getPackageName() + "." + currentCe.getName();
		}
		return null;
	}
	
	/**
	 * Finds the source file node for the given filename in the structuremodel with 
	 * given root element.
	 * 
	 * @param root		
	 * @param filename	absolute filename
	 * @return
	 */
	protected IProgramElement findSourceFileNode(IProgramElement root, String filename){
		if(root == null){
			return null;
		}
		if(filename == null){
			return null;
		}
		
		if (root.getKind() != null && root.getKind().isSourceFile()){
			if(root.getSourceLocation().getSourceFile().getAbsolutePath().equals(filename)){
				return root;	
			}else{
				return null;
			}
			
		} else {
		    IProgramElement res = null;
			for (Iterator it = root.getChildren().iterator(); it.hasNext() && res == null;) {
				res = findSourceFileNode((IProgramElement) it.next(), filename);
			}
			return res;
		}
	}
	
	
	/**
	 * @param typeNode	a CaesarProgramElement representing a type
	 * @return		true if typeNode contains a main method
	 */
	protected boolean isTypeNodeRunnable(CaesarProgramElement typeNode){
		boolean isRunnable = false;
		Object current = null;
		for (Iterator it = typeNode.getChildren().iterator(); it.hasNext() && !isRunnable;) {
			current = it.next();
			if(current instanceof CaesarProgramElement){
				isRunnable = ((CaesarProgramElement)current).isRunnable();
			}
		}
		return isRunnable;
	}
	
	/**
	 * Creates a default launch configuration based on the given type and project.
	 * @param type		full qualified type name
	 * @param project	project name
	 * @return	new default launch configuration based on the given type and project
	 */
	protected ILaunchConfiguration createConfiguration(String type, String project) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.caesarj.launching.localCaesarApplication");
			
			String configName = type;
			if(type.lastIndexOf(".") != -1){
				configName = type.substring(type.lastIndexOf(".") + 1);
			}
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, DebugPlugin.getDefault().getLaunchManager().generateLaunchConfigurationName(configName)); 
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, type);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project);
			//wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, true);
			//wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, (String)null);
			//wc.setAttribute(CaesarMainTab.ATTR_USE_STEP_FILTER, true);
			//wc.setAttribute(CaesarMainTab.ATTR_USE_STEP_FILTER, (String)null);
		
			config = wc.doSave();		
		} catch (CoreException ce) {
			JDIDebugUIPlugin.log(ce);			
		}
		return config;
	}
	
	/**
	 * Searches for an existing configuration for the given type and project. If no
	 * matching configuration can be found, a new one is created.  
	 * @param type	full qualified type name
	 * @param project	project name
	 * @return	a launch configuration for type and project
	 */
	protected ILaunchConfiguration findConfiguration(String type, String project){
		ILaunchConfigurationType configType = getCaesarLaunchConfigType();
		List candidateConfigs = Collections.EMPTY_LIST;
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs = new ArrayList(configs.length);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "").equals(type)) { //$NON-NLS-1$
					if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "").equals(project)) { //$NON-NLS-1$
						candidateConfigs.add(config);
					}
				}
			}
		} catch (CoreException e) {
			JDIDebugUIPlugin.log(e);
		}
		
		// If there are no existing configs associated with the IType, create one.
		// If there is exactly one config associated with the IType, return it.
		// Otherwise, if there is more than one config associated with the IType, prompt the
		// user to choose one.
		int candidateCount = candidateConfigs.size();
		if (candidateCount < 1) {
			return createConfiguration(type, project);
		} else if (candidateCount == 1) {
			return (ILaunchConfiguration) candidateConfigs.get(0);
		} else {
			// Prompt the user to choose a config.  A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching anything.
			ILaunchConfiguration config = chooseConfiguration(candidateConfigs);
			if (config != null) {
				return config;
			}
		}
		
		return null;
	}
	
	/**
	 * Show a selection dialog that allows the user to choose one of the specified
	 * launch configurations.  Return the chosen config, or <code>null</code> if the
	 * user cancelled the dialog.
	 * 
	 * @param configList	list of launch configurations
	 */
	protected ILaunchConfiguration chooseConfiguration(List configList) {
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle("LaunchConfig Select Title");  //$NON-NLS-1$
		dialog.setMessage("LaunchConfig Select Message"); //$NON-NLS-1$
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;		
	}
	
	/**
	 * Launches a configuration for the given type name and project
	 * @param type	full qualified type name
	 * @param project	project name
	 * @param mode	the launch mode
	 */
	protected void launch(String type, String project, String mode) {
		ILaunchConfiguration config = findConfiguration(type, project);
		if (config != null) {
			DebugUITools.launch(config, mode);
		}			
	}
	
	/**
	 * @return	launch configuration type for caesar
	 */
	protected ILaunchConfigurationType getCaesarLaunchConfigType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.caesarj.launching.localCaesarApplication");		
	}
	
	/**
	 * Convenience method to get the window that owns this action's Shell.
	 */
	protected Shell getShell() {
		return JDIDebugUIPlugin.getActiveWorkbenchShell();
	}
}
