package org.caesarj.ui.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.IRelationshipMap;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.ui.CaesarPlugin;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * Content Outline Page for Caesar Compilation Unit
 * 
 * TODO bugs! ClassCastExcpetion, nochmal alles ueberdenken
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarOutlineView extends ContentOutlinePage {
         
    private static HashMap categoryMap = new HashMap();
    
    static {
        // provides sorting order for ProgramElementNode categories  
        categoryMap.put(IProgramElement.Kind.ASPECT,      new Integer(0));
        categoryMap.put(IProgramElement.Kind.CLASS,       new Integer(1));
        categoryMap.put(IProgramElement.Kind.FIELD,       new Integer(11));
        categoryMap.put(IProgramElement.Kind.CONSTRUCTOR, new Integer(12));
        categoryMap.put(IProgramElement.Kind.METHOD,      new Integer(13));        
        categoryMap.put(IProgramElement.Kind.ADVICE,      new Integer(14));        
    }
         

    class LexicalSorter extends ViewerSorter {
        /**
         * Return a category code for the element. This is used
         * to sort alphabetically within categories. Categories are:
         * pointcuts, advice, introductions, declarations, other. i.e.
         * all pointcuts will be sorted together rather than interleaved
         * with advice.
         */
        public int category(Object element) {
            try {
                return ((Integer)categoryMap.get(((IProgramElement)element).getKind())).intValue();
            } 
            catch(Exception e) {
                return 999;
            }                   
        }
    };
    
     
    /**
     * uses Structure Model to extract the data for TreeViewer
     */
    protected class ContentProvider implements ITreeContentProvider {
        
        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        
        public Object[] getElements(Object inputElement) {
            if(!(inputElement instanceof IProgramElement))
                return new Object[]{};
            
            return ((IProgramElement)inputElement).getChildren().toArray();            
        }   
        
        public Object[] getChildren(Object parentElement) {
            try {            
                IProgramElement parent = (IProgramElement)parentElement;
                Vector vec = new Vector();
                IRelationshipMap map = AsmManager.getDefault().getRelationshipMap();
                
                List relations = map.get(parent);
                if(relations != null) {                
                    for(Iterator it=relations.iterator(); it.hasNext(); )
                        vec.add(it.next());
                }
    
                for(Iterator it=parent.getChildren().iterator(); it.hasNext(); ) 
                    vec.add(it.next());
                            
                return vec.toArray();
            }
            catch(Throwable t) {
                t.printStackTrace();
                return new Object[]{};
            }
        }

        public Object getParent(Object element) {
            return ((IProgramElement)element).getParent();           
        }

        public boolean hasChildren(Object element) {
            IRelationshipMap map = AsmManager.getDefault().getRelationshipMap();
            IProgramElement node = (IProgramElement)element;
            
            return
                node.getChildren().size() > 0
                || map.get(node) != null;
        }
    };

    protected CaesarEditor caesarEditor;
    protected boolean enabled;

    /**
     * Creates a content outline page using the given provider and the given editor.
     */
    public CaesarOutlineView(CaesarEditor caesarEditor) {
        super();
        allViews.add(this);
        this.caesarEditor = caesarEditor;              
    }


    public void createControl(Composite parent) {
        super.createControl(parent);

        TreeViewer viewer = getTreeViewer();
        viewer.setSorter(new LexicalSorter());
        viewer.setContentProvider(new ContentProvider());
        viewer.setLabelProvider(new LabelProvider());
        viewer.addSelectionChangedListener(this);
         
        update();
    }


    public void selectionChanged(SelectionChangedEvent event) {

        super.selectionChanged(event);

        ISelection selection= event.getSelection();
        if (selection.isEmpty()) {
        }
        else {
            Object item = ((IStructuredSelection) selection).getFirstElement();
            
            if(item instanceof IProgramElement) {
                IProgramElement selectedNode = (IProgramElement)item;
                ISourceLocation sourceLocation = selectedNode.getSourceLocation();
                
                if(sourceLocation != null) {
                    int line = sourceLocation.getLine();
                    try {
                                           
                        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                        
                        IPath path = new Path(sourceLocation.getSourceFile().getAbsolutePath());
                        IResource resource = root.getFileForLocation(path);
                        IMarker marker;
    
                        if (resource != null) {
                            marker = resource.createMarker(IMarker.MARKER);
                            marker.setAttribute(IMarker.LINE_NUMBER, sourceLocation.getLine());
                            marker.setAttribute(IMarker.CHAR_START, sourceLocation.getColumn());
                            
                            IEditorPart ePart =
                                CaesarPlugin.getDefault().
                                getWorkbench().
                                getActiveWorkbenchWindow().
                                getActivePage().
                                openEditor(marker);
                        }
                    } 
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Updates the outline page.
     */
    public void update() {   
        
        if(enabled) {            
            IProgramElement input = getInput(AsmManager.getDefault().getHierarchy().getRoot());
                 
            TreeViewer viewer = getTreeViewer();
            if (viewer != null && input != null) {
                Control control = viewer.getControl();
                if (control != null && !control.isDisposed()) {
                    control.setRedraw(false);
                    viewer.setInput(input);
                    viewer.expandAll();
                    control.setRedraw(true);
                }
            }
        }

    }

	public void setEnabled(boolean enabled) {
        this.enabled = enabled;
	}
    
    protected IProgramElement getInput(IProgramElement node) {               
        if(node == null) {
            return null;
        }

        if(node.getName().equals(caesarEditor.getEditorInput().getName())) {
            return node;
        }
        else {
            IProgramElement res = null;
            for(Iterator it=node.getChildren().iterator(); it.hasNext() && res==null; ) {
                res = getInput((IProgramElement)it.next());
            }
    
            return res;
        }
    }    
    
    private static List allViews = new LinkedList(); 
    
    public static void updateAll() {
        for(Iterator it=allViews.iterator(); it.hasNext(); ) {
            ((CaesarOutlineView)it.next()).update();
        }
    }
}
