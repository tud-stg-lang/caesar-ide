package org.caesarj.launching;

import java.text.MessageFormat;

import org.caesarj.ui.CaesarPlugin;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.resources.CaesarJPluginResources;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.launcher.JavaLaunchConfigurationTab;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class CaesarMainTab extends JavaLaunchConfigurationTab {

	// Project UI widgets
	protected Text fProjText;
	protected Button fProjButton;

	// Main class UI widgets
	protected Text fMainText;
	protected Button fStopInMainCheckButton;
	protected Button fUseStepFilterCheckButton;
	
	private ModifyListener fModifyListener= new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}
	};
	
	private SelectionAdapter fSelectionListener= new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			Object source= e.getSource();
			if (source == fProjButton) {
				handleProjectButtonSelected();
			} else {
				updateLaunchConfigurationDialog();
			}
		}
	};
	
	private static final String EMPTY_STRING= "";

	public static final String ATTR_USE_STEP_FILTER = CaesarPlugin.PLUGIN_ID + ".USE_STEP_FILTER";
	
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		comp.setLayout(topLayout);
		comp.setFont(font);
		
		createProjectEditor(comp);
		createVerticalSpacer(comp, 1);
		createMainTypeEditor(comp);
		createVerticalSpacer(comp, 1);
		
		fStopInMainCheckButton = createCheckButton(comp, CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.stopInMain")); //$NON-NLS-1$
		GridData gd = new GridData();
		fStopInMainCheckButton.setLayoutData(gd);
		fStopInMainCheckButton.addSelectionListener(fSelectionListener);
		
		fUseStepFilterCheckButton = createCheckButton(comp, CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.useStepFilter")); //$NON-NLS-1$
		GridData gd2 = new GridData();
		fUseStepFilterCheckButton.setLayoutData(gd2);
		fUseStepFilterCheckButton.addSelectionListener(fSelectionListener);
	}

	private void createProjectEditor(Composite parent) {
		Font font= parent.getFont();
		Group group= new Group(parent, SWT.NONE);
		group.setText(CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.projectLabel")); //$NON-NLS-1$
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setFont(font);

		fProjText = new Text(group, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjText.setLayoutData(gd);
		fProjText.setFont(font);
		fProjText.addModifyListener(fModifyListener);
		
		fProjButton = createPushButton(group, CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.browse"), null); //$NON-NLS-1$
		fProjButton.addSelectionListener(fSelectionListener);
	}

	private void createMainTypeEditor(Composite parent) {
		Font font= parent.getFont();
		Group mainGroup= new Group(parent, SWT.NONE);
		mainGroup.setText(CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.mainClassLabel")); //$NON-NLS-1$
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		mainGroup.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		mainGroup.setLayout(layout);
		mainGroup.setFont(font);

		fMainText = new Text(mainGroup, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fMainText.setLayoutData(gd);
		fMainText.setFont(font);
		fMainText.addModifyListener(fModifyListener);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		IJavaElement javaElement = getContext();
		if (javaElement != null) {
			initializeJavaProject(javaElement, config);
		} else {
			System.out.println("JavaElement == null");
			// We set empty attributes for project & main type so that when one config is
			// compared to another, the existence of empty attributes doesn't cause an
			// incorrect result (the performApply() method can result in empty values
			// for these attributes being set on a config if there is nothing in the
			// corresponding text boxes)
			config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
		}
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "");
	}

	public void initializeFrom(ILaunchConfiguration config) {
		updateProjectFromConfig(config);
		updateMainTypeFromConfig(config);
		updateStopInMainFromConfig(config);
		updateUseStepFilterFromConfig(config);
	}

	public void performApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText.getText().trim());
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, fMainText.getText().trim());
		
		// attribute added in 2.1, so null must be used instead of false for backwards compatibility
		if (fStopInMainCheckButton.getSelection()) {
			config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, true);
		} else {
			config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, (String)null);
		}
		if (fUseStepFilterCheckButton.getSelection()) {
			config.setAttribute(CaesarMainTab.ATTR_USE_STEP_FILTER, true);
		} else {
			config.setAttribute(CaesarMainTab.ATTR_USE_STEP_FILTER, (String)null);
		}
		
	}

	public String getName() {
		return CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.name");
	}
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return CaesarPluginImages.DESC_OBJS_INNER_CCLASS_DEFAULT.createImage();
	}
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration config) {
		setErrorMessage(null);
		setMessage(null);
		
		// Verify project
		String name= fProjText.getText().trim();
		if (name.length() > 0) {
			if (!ResourcesPlugin.getWorkspace().getRoot().getProject(name).exists()) {
				setErrorMessage(CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.projectErrorDoesNotExist")); //$NON-NLS-1$
				return false;
			}
		}

		// Verify main class
		name = fMainText.getText().trim();
		if (name.length() == 0) {
			setErrorMessage(CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.mainClassErrorTypeNotSpecified")); //$NON-NLS-1$
			return false;
		}
		
		return true;
	}
	
	/**
	 * Convenience method to get the workspace root.
	 */
	private IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/**
	 * Return the IJavaProject corresponding to the project name in the project name
	 * text field, or null if the text does not match a project name.
	 */
	protected IJavaProject getJavaProject() {
		String projectName = fProjText.getText().trim();
		if (projectName.length() < 1) {
			return null;
		}
		return getJavaModel().getJavaProject(projectName);		
	}
	
	/**
	 * Convenience method to get access to the java model.
	 */
	private IJavaModel getJavaModel() {
		return JavaCore.create(getWorkspaceRoot());
	}

	/**
	 * Realize a Java Project selection dialog and return the first selected project,
	 * or null if there was none.
	 */
	protected IJavaProject chooseJavaProject() {
		IJavaProject[] projects;
		try {
			projects= JavaCore.create(getWorkspaceRoot()).getJavaProjects();
		} catch (JavaModelException e) {
			JDIDebugUIPlugin.log(e);
			projects= new IJavaProject[0];
		}
		
		ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle(CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.projectSelection")); //$NON-NLS-1$
		dialog.setMessage(CaesarJPluginResources.getResourceString("Launching.tabGroup.mainTab.projectSelectionMessage")); //$NON-NLS-1$
		dialog.setElements(projects);
		
		IJavaProject javaProject = getJavaProject();
		if (javaProject != null) {
			dialog.setInitialSelections(new Object[] { javaProject });
		}
		if (dialog.open() == Window.OK) {			
			return (IJavaProject) dialog.getFirstResult();
		}			
		return null;		
	}
	
	/**
	 * Show a dialog that lets the user select a project.  This in turn provides
	 * context for the main type, allowing the user to key a main type name, or
	 * constraining the search for main types to the specified project.
	 */
	private void handleProjectButtonSelected() {
		IJavaProject project= chooseJavaProject();
		if (project == null) {
			return;
		}
		
		String projectName= project.getElementName();
		fProjText.setText(projectName);	
	}
	
	protected void updateProjectFromConfig(ILaunchConfiguration config) {
		String projectName = ""; //$NON-NLS-1$
		try {
			projectName = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);	
		} catch (CoreException ce) {
			JDIDebugUIPlugin.log(ce);
		}
		fProjText.setText(projectName);
	}
	
	protected void updateMainTypeFromConfig(ILaunchConfiguration config) {
		String mainTypeName = ""; //$NON-NLS-1$
		try {
			mainTypeName = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, EMPTY_STRING);
		} catch (CoreException ce) {
			JDIDebugUIPlugin.log(ce);	
		}	
		fMainText.setText(mainTypeName);	
	}
	
	protected void updateStopInMainFromConfig(ILaunchConfiguration configuration) {
		boolean stop = false;
		try {
			stop = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, false);
		} catch (CoreException e) {
			JDIDebugUIPlugin.log(e);
		}
		fStopInMainCheckButton.setSelection(stop);
	}
	
	protected void updateUseStepFilterFromConfig(ILaunchConfiguration configuration) {
		boolean filter = false;
		try {
			filter = configuration.getAttribute(CaesarMainTab.ATTR_USE_STEP_FILTER, false);
		} catch (CoreException e) {
			JDIDebugUIPlugin.log(e);
		}
		fUseStepFilterCheckButton.setSelection(filter);
	}
}
