
package org.caesarj.ui.wizard;


import org.apache.log4j.Logger;
import org.caesarj.ui.CJDTConfigSettings;
import org.caesarj.ui.preferences.CaesarJPreferences;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;


public class CaesarConfigPage extends WizardPage {
	private IWorkbench workbench;
	private static Logger log = Logger.getLogger(CaesarConfigPage.class);

	// widgets
	private Button caesarJAnnotationCheckbox = null;
	private Button caesarEditorDefaultCheckbox = null;
	private Button caesarJAnnotationAutoSwitshCheckbox = null;
	private Button dontAskAgainCheckbox = null;
	
	public CaesarConfigPage(){

		super("Caesar Preferences");
		this.setTitle("Caesar Preferences");		
		this.setDescription("To costomize your CaesarJ Plugin choose your preferences");
	}
	
	
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		setControl(composite);
	
		// create workbench preferences group
		try {
			Group workbench = new Group(composite, SWT.NONE);
			workbench.setLayout(new GridLayout());
			workbench.setText("CaesarJ Preference");
			workbench.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
				caesarJAnnotationCheckbox = new Button(workbench, SWT.CHECK);		
				caesarJAnnotationCheckbox.setText("Default setting: annotation while typing");
				caesarJAnnotationCheckbox.setSelection(JavaPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS));
			
				caesarJAnnotationAutoSwitshCheckbox = new Button(workbench, SWT.CHECK);		
				caesarJAnnotationAutoSwitshCheckbox.setText("Auto annotation switch while changing editors");
				caesarJAnnotationAutoSwitshCheckbox.setSelection(CaesarJPreferences.isCAESARAutoSwitch());
				
				caesarEditorDefaultCheckbox = new Button(workbench, SWT.CHECK);	
				caesarEditorDefaultCheckbox.setText("Make the CaesarJ editor the default java - editor");
				caesarEditorDefaultCheckbox.setSelection(CJDTConfigSettings.isCaesarJEditorDefault());
							
		new Label(composite, SWT.NONE); // vertical spacer
		}
		catch (Exception e)
		{
			log.warn("Error while drawing ConfigPage", e);
		}
		
		dontAskAgainCheckbox = new Button(composite, SWT.CHECK);
		dontAskAgainCheckbox.setText("Open this dialog next time you open the CaesarJ perspective?");
		dontAskAgainCheckbox.setSelection(CaesarJPreferences.isCAESARPrefConfigDone());
	}
	
	
	public boolean finish() {
		
		if (caesarJAnnotationCheckbox != null) {
			if (!caesarJAnnotationCheckbox.getSelection())
				CJDTConfigSettings.disableAnalyzeAnnotations();
			else
				CJDTConfigSettings.enableAnalyzeAnnotations();
		}
		if (caesarJAnnotationAutoSwitshCheckbox != null) {
			CaesarJPreferences.setCAESARAutoSwitch(caesarJAnnotationAutoSwitshCheckbox.getSelection());
		}
		if (caesarEditorDefaultCheckbox != null) {
			if (caesarEditorDefaultCheckbox.getSelection())
				CJDTConfigSettings.enableCaesarJEditorDefault();
			else
				CJDTConfigSettings.disableCaesarJEditorDefault();
		}
		boolean dontAskAgain = dontAskAgainCheckbox.getSelection();
		CaesarJPreferences.setCAESARPrefConfigDone(dontAskAgain);
		return true;
	}
}