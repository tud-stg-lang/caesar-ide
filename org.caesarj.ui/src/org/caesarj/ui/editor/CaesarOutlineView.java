package org.caesarj.ui.editor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.asm.LinkNode;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureModelManager;
import org.aspectj.asm.StructureNode;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * Content Outline Page for Caesar Compilation Unit
 * 
 * @author ivica
 */
public class CaesarOutlineView extends ContentOutlinePage {
     
    /**
     * uses Structure Model to extract the data for treeViewer
     */
    protected class ContentProvider implements ITreeContentProvider {
        
        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        
        public Object[] getElements(Object inputElement) {
            return ((ProgramElementNode)inputElement).getChildren().toArray();
        }
        
        public Object[] getChildren(Object parentElement) {
            if(parentElement instanceof ProgramElementNode) {
                ProgramElementNode node = (ProgramElementNode)parentElement;
                if(node.getRelations().size() > 0)
                    return node.getRelations().toArray();
            }
            
            return ((StructureNode)parentElement).getChildren().toArray();                
        }

        public Object getParent(Object element) {
            return ((StructureNode)element).getParent();           
        }

        public boolean hasChildren(Object element) {
            if(element instanceof ProgramElementNode) {
                ProgramElementNode node = (ProgramElementNode)element;
                return
                    node.getChildren().size() > 0
                    || node.getRelations().size() > 0;
            }
            else {
                StructureNode node = (StructureNode)element;
                return node.getChildren().size() > 0;
            }   
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

    /* (non-Javadoc)
     * Method declared on ContentOutlinePage
     */
    public void createControl(Composite parent) {

        super.createControl(parent);

        TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(new ContentProvider());
        viewer.setLabelProvider(new LabelProvider());
        viewer.addSelectionChangedListener(this);

        update();
    }

    /* (non-Javadoc)
     * Method declared on ContentOutlinePage
     */
    public void selectionChanged(SelectionChangedEvent event) {

        super.selectionChanged(event);

        ISelection selection= event.getSelection();
        if (selection.isEmpty()) {
        }
        else {
            Object item = ((IStructuredSelection) selection).getFirstElement();
            
            if(item instanceof LinkNode) {
                item = ((LinkNode)item).getProgramElementNode();
            }
            
            StructureNode selectedNode = (StructureNode)item;
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

    /**
     * Updates the outline page.
     */
    public void update() {   
        
        if(enabled) {            
            StructureNode input = getInput(StructureModelManager.INSTANCE.getStructureModel().getRoot());
                 
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
    
    protected StructureNode getInput(StructureNode node) {
        if(node == null) {
            return null;
        }

        if(node.getName().equals(caesarEditor.getEditorInput().getName())) {
            return node;
        }
        else {
            StructureNode res = null;
            for(Iterator it=node.getChildren().iterator(); it.hasNext() && res==null; ) {
                res = getInput((StructureNode)it.next());
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
