package org.caesarj.ui.editor;

import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * CaesarTextTools, replaces JavaTextTools code scanner with CaesarCodeScanner.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarTextTools extends JavaTextTools {
    
    private CaesarCodeScanner codeScanner;

    public CaesarTextTools(IPreferenceStore ips) {
        super(ips);

        codeScanner = new CaesarCodeScanner(getColorManager(), ips);
    } 

    public RuleBasedScanner getCodeScanner() {
        return codeScanner;
    }

    public void dispose() {
        codeScanner = null;

        super.dispose();
    }

    public boolean affectsBehavior(PropertyChangeEvent event) {
        return
            codeScanner.affectsBehavior(event); // Changed was depricatied||
            //super.affectsBehavior(event);
    }

    /**
     * Adapts the behavior of the contained components to the change
     * encoded in the given event.
     * 
     * @param event the event to whch to adapt
     */
    protected void adaptToPreferenceChange(PropertyChangeEvent event) {
        if (codeScanner.affectsBehavior(event))
            codeScanner.adaptToPreferenceChange(event);
        
        super.adaptToPreferenceChange(event);
    }
}
