package org.caesarj.debug.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.caesarj.launching.CaesarMainTab;
import org.caesarj.ui.CaesarPlugin;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.internal.debug.core.JDIDebugPlugin;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;

import com.sun.jdi.VirtualMachine;

public class CaesarDebugTarget extends JDIDebugTarget {

	private boolean useCaesarStepFilter = false;
	private String[] stepFilters = null;
	private boolean isStepFiltersEnabled = false;
	
	
	public CaesarDebugTarget(ILaunch launch, VirtualMachine jvm, String name, boolean supportTerminate, boolean supportDisconnect, IProcess process, boolean resume) {
		super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
		
		try {
			useCaesarStepFilter = launch.getLaunchConfiguration().getAttribute(
					CaesarMainTab.ATTR_USE_STEP_FILTER, false);
			
		} catch (CoreException e) {
			JDIDebugPlugin.log(e);
		}
	}

	public static IDebugTarget newDebugTarget(final ILaunch launch, final VirtualMachine vm, final String name, final IProcess process, final boolean allowTerminate, final boolean allowDisconnect, final boolean resume) {
		final IJavaDebugTarget[] target = new IJavaDebugTarget[1];
		IWorkspaceRunnable r = new IWorkspaceRunnable() {
			public void run(IProgressMonitor m) {
				target[0]= new CaesarDebugTarget(launch, vm, name, allowTerminate, allowDisconnect, process, resume);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(r, null, 0, null);
		} catch (CoreException e) {
			JDIDebugPlugin.log(e);
		}
		return target[0];
	}
	
	/**
	 * @see IJavaDebugTarget#setFilterConstructors(boolean)
	 */
	public void setFilterConstructors(boolean filter) {
		super.setFilterConstructors(filter);
	}

	/**
	 * @see IJavaDebugTarget#setFilterStaticInitializers(boolean)
	 */
	public void setFilterStaticInitializers(boolean filter) {
		super.setFilterStaticInitializers(filter);	
	}
	
	/**
	 * @see IJavaDebugTarget#setFilterSynthetics(boolean)
	 */
	public void setFilterSynthetics(boolean filter) {
		if(useCaesarStepFilter){
			super.setFilterSynthetics(true);
		}else{
			super.setFilterSynthetics(filter);
		}
						
	}

	/**
	 * @see IJavaDebugTarget#setStepFilters(String[])
	 */
	public void setStepFilters(String[] list) {
		stepFilters = list;
		if(useCaesarStepFilter && isStepFiltersEnabled){
			resetStepFilters();
		}else{
			super.setStepFilters(list);
		}
	}
	
	/**
	 * @see IJavaDebugTarget#setStepFiltersEnabled(boolean)
	 */
	public void setStepFiltersEnabled(boolean enabled) {
		isStepFiltersEnabled = enabled;
		if(useCaesarStepFilter){
			resetStepFilters();
			super.setStepFiltersEnabled(true);
		}else{
			super.setStepFiltersEnabled(enabled);
		}
	}
	
	protected void resetStepFilters(){
		List<String> filterList = null;
		if(isStepFiltersEnabled){
			filterList = new ArrayList<String>(Arrays.asList(stepFilters));
		}else{
			filterList = new ArrayList<String>();
		}
		
		for(int j=0; j < CaesarPlugin.FILTER.length; j++){
			if(!filterList.contains(CaesarPlugin.FILTER[j])){
				filterList.add(CaesarPlugin.FILTER[j]);
			}
		}
		String[] filterArray = new String[filterList.size()];
		filterList.toArray(filterArray);
		super.setStepFilters(filterArray);
	}
}
