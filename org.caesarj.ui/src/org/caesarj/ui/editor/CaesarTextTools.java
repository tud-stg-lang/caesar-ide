package org.caesarj.ui.editor;

import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.RuleBasedScanner;

/**
 * CaesarTextTools, replaces JavaTextTools code scanner with CaesarCodeScanner.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarTextTools extends JavaTextTools {
    
    private CaesarCodeScanner codeScanner;

    public CaesarTextTools(IPreferenceStore ips) {
        super(ips);
        this.codeScanner = new CaesarCodeScanner(getColorManager(), ips);
    } 

    public RuleBasedScanner getCodeScanner() {
        return this.codeScanner;
    }
	/**
	 * Disposes all the individual tools of this tools collection.
	 */
	public void dispose() {

		this.codeScanner = null;
		super.dispose();
	}
}
