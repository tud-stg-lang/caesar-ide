package org.caesarj.ui.editor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * A content outline page which always represents the content of the
 * connected editor in 10 segments.
 */
public class OutlinePage extends ContentOutlinePage {

    /**
     * Divides the editor's document into ten segments and provides elements for them.
     */
    protected class ContentProvider implements ITreeContentProvider {

        /*
         * @see IContentProvider#inputChanged(Viewer, Object, Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            
        }

        /*
         * @see IContentProvider#dispose
         */
        public void dispose() {
           
        }

        /*
         * @see IStructuredContentProvider#getElements(Object)
         */
        public Object[] getElements(Object element) {
            return null;
        }

        /*
         * @see ITreeContentProvider#hasChildren(Object)
         */
        public boolean hasChildren(Object element) {
            return element == fInput;
        }

        /*
         * @see ITreeContentProvider#getParent(Object)
         */
        public Object getParent(Object element) {
            return null;
        }

        /*
         * @see ITreeContentProvider#getChildren(Object)
         */
        public Object[] getChildren(Object element) {           
            return null;
        }
    };

    protected Object fInput;
    protected IDocumentProvider fDocumentProvider;
    protected ITextEditor fTextEditor;

    /**
     * Creates a content outline page using the given provider and the given editor.
     */
    public OutlinePage() {
        super();
    }
    
    /* (non-Javadoc)
     * Method declared on ContentOutlinePage
     */
    public void createControl(Composite parent) {

        super.createControl(parent);

        TreeViewer viewer= getTreeViewer();
        viewer.setContentProvider(new ContentProvider());
        viewer.setLabelProvider(new LabelProvider());
        viewer.addSelectionChangedListener(this);

        if (fInput != null)
            viewer.setInput(fInput);
    }
    
    /* (non-Javadoc)
     * Method declared on ContentOutlinePage
     */
    public void selectionChanged(SelectionChangedEvent event) {

        super.selectionChanged(event);

        ISelection selection= event.getSelection();
        if (selection.isEmpty())
            fTextEditor.resetHighlightRange();
        else {
            
        }
    }
    
    /**
     * Sets the input of the outline page
     */
    public void setInput(Object input) {
        fInput= input;
        update();
    }
    
    /**
     * Updates the outline page.
     */
    public void update() {
        TreeViewer viewer= getTreeViewer();

        if (viewer != null) {
            Control control= viewer.getControl();
            if (control != null && !control.isDisposed()) {
                control.setRedraw(false);
                viewer.setInput(fInput);
                viewer.expandAll();
                control.setRedraw(true);
            }
        }
    }
}
