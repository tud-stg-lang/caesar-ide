package org.caesarj.ui.preferences;

import org.caesarj.ui.CaesarPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Holds the preferences for CaesarJ as set via the Workbench->preferences
 * pages.
 */
public class CaesarJPreferences {

	/**
	 * Identifier (key) for indication of whether AJDTPrefConfigWizard should be
	 * shown again. If true, don't show.
	 */
	public static final String CAESAR_PREF_CONFIG_DONE = "org.caesarj.ui.preferences.ajdtPrefConfigDone";

	public static final String PDE_AUTO_IMPORT_CONFIG_DONE = "org.caesarj.ui.preferences.pdeAutoImportConfigDone";

	public static final String ASK_PDE_AUTO_IMPORT = "org.caesarj.ui.preferences.askPdeAutoImport";

	static public void setCAESARPrefConfigDone(boolean done) {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(CAESAR_PREF_CONFIG_DONE, done);
	}

	static public boolean isCAESARPrefConfigDone() {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		return store.getBoolean(CAESAR_PREF_CONFIG_DONE);
	}

	static public void setAskPDEAutoImport(boolean ask) {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(ASK_PDE_AUTO_IMPORT, ask);
	}

	static public boolean askPDEAutoImport() {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		return store.getBoolean(ASK_PDE_AUTO_IMPORT);
	}

	static public void setPDEAutoImportConfigDone(boolean done) {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(PDE_AUTO_IMPORT_CONFIG_DONE, done);
	}

	public static boolean isPDEAutoImportConfigDone() {
		IPreferenceStore store = CaesarPlugin.getDefault()
				.getPreferenceStore();
		return store.getBoolean(PDE_AUTO_IMPORT_CONFIG_DONE);
	}
}