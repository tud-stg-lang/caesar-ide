package org.caesarj.launching;

import java.util.HashMap;
import java.util.Map;

import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaMethodBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jface.preference.IPreferenceStore;

public class CaesarLaunchConfigurationDelegate extends JavaLaunchDelegate
{
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		prepareUseStepFilter(configuration);
		super.launch(configuration, mode, launch, monitor);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate#getMainTypeName(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public String getMainTypeName(ILaunchConfiguration configuration)
			throws CoreException {
		
		ProjectProperties properties = ProjectProperties.create(getJavaProject(configuration).getProject());

		String mainType = super.getMainTypeName(configuration);
		
		// test if maintype is a caesar class
		JavaQualifiedName mainTypeQ = new JavaQualifiedName(mainType.replace('.','/'));
		CaesarTypeNode typeNode = properties.getKjcEnvironment().getCaesarTypeSystem().getCaesarTypeGraph().getType(mainTypeQ);
		if(typeNode != null){
			// convert maintype to implementation class
			mainType = mainTypeQ.convertToImplName().convertPkgSeperator(".").toString();
		}
		return mainType;
	}
	
	protected void prepareUseStepFilter(ILaunchConfiguration config) throws CoreException{
		if (isUseStepFilter(config)) {
			// This listener does not remove itself from the debug plug-in
			// as an event listener (there is no dispose notification for
			// launch delegates). However, since there is only one delegate
			// instantiated per config type, this is tolerable.
			DebugPlugin.getDefault().addDebugEventListener(this);
			IPreferenceStore store = DebugUIPlugin.getDefault().getPreferenceStore();
			store.addPropertyChangeListener(CjStepFilterOptionManager.getDefault());
		}
	}

	protected boolean isUseStepFilter(ILaunchConfiguration config) throws CoreException {
		return config.getAttribute(
				CaesarMainTab.ATTR_USE_STEP_FILTER, false);
	}
	
	/**
	 * Handles the "stop-in-main" and "use-step-filter" option.
	 * 
	 * @param events
	 *            the debug events.
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			DebugEvent event = events[i];
			if (event.getKind() == DebugEvent.CREATE
					&& event.getSource() instanceof IJavaDebugTarget) {
				
				IJavaDebugTarget target = (IJavaDebugTarget) event.getSource();
				ILaunch launch = target.getLaunch();
				if (launch != null) {
					ILaunchConfiguration configuration = launch.getLaunchConfiguration();
					if (configuration != null) {
						try {
							if (isStopInMain(configuration)) {
								String mainType = getMainTypeName(configuration);
								if (mainType != null) {
									Map map = new HashMap();
									map.put(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN,IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN);
									
									IJavaMethodBreakpoint bp = JDIDebugModel
											.createMethodBreakpoint(
													ResourcesPlugin.getWorkspace().getRoot(),
													mainType, 
													"main", //$NON-NLS-1$
													"([Ljava/lang/String;)V", //$NON-NLS-1$
													true, false, false, -1, -1,
													-1, 1, false, map); //$NON-NLS-1$
									bp.setPersisted(false);
									target.breakpointAdded(bp);
								}
							}
							if (isUseStepFilter(configuration)) {
								CjStepFilterOptionManager.getDefault().activateCaesarStepFilter(target);
							}
							DebugPlugin.getDefault().removeDebugEventListener(this);
							
						} catch (CoreException e) {
							LaunchingPlugin.log(e);
						}
					}
				}
			}
		}
	}
	
	public IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode) throws CoreException {
		if(mode.equals(ILaunchManager.DEBUG_MODE)){
			// Create CaesarVMDebugger
			IVMInstall vm = verifyVMInstall(configuration);
			IVMRunner runner = new CaesarVMDebugger(vm);
			return runner;
		}
		return super.getVMRunner(configuration, mode);
	}
}
