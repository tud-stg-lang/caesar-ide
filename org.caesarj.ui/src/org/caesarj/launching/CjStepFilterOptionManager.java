package org.caesarj.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.caesarj.ui.CaesarPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.internal.debug.ui.IJDIPreferencesConstants;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class CjStepFilterOptionManager implements IPropertyChangeListener {
	
	/**
	 * Singleton options manager
	 */
	private static CjStepFilterOptionManager fgOptionsManager = null;
	
	/**
	 * Return the default options manager
	 */
	public static CjStepFilterOptionManager getDefault() {
		if (fgOptionsManager == null) {
			fgOptionsManager = new CjStepFilterOptionManager();
		}
		return fgOptionsManager;
	}
	
	/**
	 * Not to be instantiated
	 * 
	 * @see JavaDebugOptionsManager#getDefault();
	 */
	private CjStepFilterOptionManager() {
	}
	
	/**
	 * Activates the caesar specific step filters for the given debug target.
	 * 
	 * @param target
	 * @throws CoreException 
	 */
	public void activateCaesarStepFilter(IJavaDebugTarget target) throws CoreException {
		if(target.getLaunch().getLaunchConfiguration().getAttribute(
				CaesarMainTab.ATTR_USE_STEP_FILTER, false))
		{
			
			target.setFilterSynthetics(true);
			if(target.isStepFiltersEnabled()){
				List<String> filterList = new ArrayList<String>(Arrays.asList(target.getStepFilters()));
				for(int j=0; j < CaesarPlugin.FILTER.length; j++){
					if(!filterList.contains(CaesarPlugin.FILTER[j])){
						filterList.add(CaesarPlugin.FILTER[j]);
					}
				}
				String[] filterArray = new String[filterList.size()];
				filterList.toArray(filterArray);
				target.setStepFilters(filterArray);
			}else{
				target.setStepFilters(CaesarPlugin.FILTER);
			}
			target.setStepFiltersEnabled(true);
			
		}
	}

	/**
	 * Makes sure changes to debug property IInternalDebugUIConstants.PREF_USE_STEP_FILTERS are forwarded to
	 * debug targets (even if old value == new value). This enables full support for java filters with caesar 
	 * debug targets.
	 * 
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(IInternalDebugUIConstants.PREF_USE_STEP_FILTERS)) {
			Object newValue= event.getNewValue();
			boolean newBoolValue = false;
			if (newValue instanceof Boolean) {
				newBoolValue = ((Boolean)(newValue)).booleanValue();
			} else if (newValue instanceof String) {
				newBoolValue = Boolean.valueOf((String)newValue).booleanValue();
			}
			IDebugTarget[] targets = DebugPlugin.getDefault().getLaunchManager().getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				if (targets[i] instanceof IJavaDebugTarget) {
					IJavaDebugTarget target = (IJavaDebugTarget)targets[i];
					target.setStepFiltersEnabled(newBoolValue);
				}
			}
		}
	}
	
}
