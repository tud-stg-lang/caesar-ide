package org.caesarj.ui.editor;

import org.apache.log4j.Logger;
import org.caesarj.ui.CaesarPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * CaesarEditor
 * 
 * TODO [feature] solve the problem with annotations, at the moment user has to disable "Analyse Annotations while typing"
 * TODO [feature] completer for caesar keywords
 * TODO [feature] add new file type (e.g.: .cj) in order to avoid conflicts with coexisting pure java projects
 * TODO [feature] add new Wizardtype for Caesar Files
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */

public class CaesarEditor extends CompilationUnitEditor {
	
	/**
	 * @author Jochen
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */

    private static Logger log = Logger.getLogger(CaesarEditor.class);
/* V.G. START: Model switched off    
    private CaesarOutlineView outlineView;
V.G. END: Model switched off */       
    private CompositeRuler caesarVerticalRuler;
    
    public CaesarEditor() {
        super();
    }
    
    protected void initializeEditor() 
    {
		super.initializeEditor();
		
		log.debug("Initializing CaesarJ Editor.");
        try 
		{
        	IPreferenceStore store = this.getPreferenceStore();
    		CaesarTextTools textTools = new CaesarTextTools(store);
    		JavaSourceViewerConfiguration svConfig =  new CaesarSourceViewerConfiguration(textTools, this, store);
           	setSourceViewerConfiguration(svConfig);
        	
           	log.debug("CaesarJ Editor Initialized.");
        }
        catch(Exception e)
		{
        	log.error("Initalizing CaesarJ Editor.",e);
		}
	}
    
    /* overriden to create correct source viewer configuration */
    protected void setPreferenceStore(IPreferenceStore store) 
    {
		super.setPreferenceStore(store);
		
		CaesarTextTools textTools = new CaesarTextTools(store);
		JavaSourceViewerConfiguration svConfig =  new CaesarSourceViewerConfiguration(textTools, this, store);
       	setSourceViewerConfiguration(svConfig);
	}
   
    public Object getAdapter(Class key) {                  
/* V.G. START: Model switched off 
    	if(key.equals(IContentOutlinePage.class)) {
            if(outlineView == null) {
                outlineView = new CaesarOutlineView(this);
                outlineView.setEnabled(true);
            }
            return outlineView;
        }
V.G. END: Model switched off */
        return super.getAdapter(key);
    }    
    
    public void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);
    }
    
    public void dispose() {
        log.debug("dispose");
/* V.G. START: Model switched off
        outlineView.setEnabled(false);
V.G. END: Model switched off */
    }  	
}
