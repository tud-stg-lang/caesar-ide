package org.caesarj.ui.editor;

import org.caesarj.ui.CaesarPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
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
    
    
    public CaesarEditor() {
        super();
        
        CaesarTextTools textTools =
            CaesarPlugin.getDefault().getCaesarTextTools();
        
        JavaSourceViewerConfiguration svConfig = 
            new JavaSourceViewerConfiguration(textTools, this);  
        
        setSourceViewerConfiguration(svConfig);
    }
    
    public Object getAdapter(Class key) {
        if ( key.equals( IContentOutlinePage.class ) ) {            
            return new OutlinePage();
        } 

        return super.getAdapter( key );
    }


    public void setFocus() {
        IEditorInput input = getEditorInput();
        if (input instanceof IFileEditorInput) {
            IFileEditorInput fInput = (IFileEditorInput)input;
        }
        super.setFocus();
    }
    
}
