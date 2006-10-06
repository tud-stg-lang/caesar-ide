package org.caesarj.ui.preferences;

import org.caesarj.ui.CaesarPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;

public class CaesarPreferenceInitializer extends AbstractPreferenceInitializer {

	public CaesarPreferenceInitializer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = CaesarPlugin.getDefault().getPreferenceStore();

		prefs.setValue(CaesarJPreferences.CAESAR_AUTO_SWITCH, false);
		prefs.setValue(CaesarJPreferences.CAESAR_PREF_CONFIG_DONE, false);
		prefs.setValue(CaesarJPreferences.CAESAR_ANALIZE_ANNOTATIONS, false);
		prefs.setValue(CaesarJPreferences.CAESAR_IS_DEFAULT_EDITOR, true);
		prefs.setValue(CaesarJPreferences.CAESAR_RUN_WEAVER, true);
	}
}
