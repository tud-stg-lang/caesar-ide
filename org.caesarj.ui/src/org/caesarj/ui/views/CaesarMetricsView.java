package org.caesarj.ui.views;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import java.util.Vector;

import org.apache.log4j.Logger;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * This class will work with a <code>CaesarMetrics</code> to enable the user
 * to see basic code metrics updated in real-time as the underlying Java model
 * is changed. This view shows the metrics of the currently selected <code>ICompilationUnit</code>,
 * whether it is in the Package Explorer, Outline, or Hierarchy view.
 * 
 * @see	org.caesarj.ui.views.CaesarMetrics
 */
public class CaesarMetricsView
	extends ViewPart
	implements ISelectionListener, ICaesarJMetricsListener {
	static final String NO_SELECTION_MESSAGE =
		"No inheritance information available for the current class. Need to compile";
	Text message;
	CaesarMetrics jm;
	
	private TreeViewer treeViewer;
	
	private static Logger logger = Logger.getLogger(CaesarMetricsView.class);
	
	protected class HierarchyContentProvider implements ITreeContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			Object[] ret = new Object[1];
			ret[0] = new String("Hello");
//			ret[0] = ((PackageNode) ((CaesarProgramElementNode) inputElement).getParent()).clone();
//			ret[1] = ((CaesarProgramElementNode) inputElement).getImports();
//			CaesarOutlineView.this.imports = ret[1];
//			ret[2] = ((CaesarProgramElementNode) inputElement).getChildren().toArray()[0];
			return ret;
		}

		public Object[] getChildren(Object parentElement) {
			Vector vec = new Vector();

//			if (parentElement instanceof ProgramElementNode) {
//				ProgramElementNode node = (ProgramElementNode) parentElement;
//				Iterator it = node.getRelations().iterator();
//				while(it.hasNext()){
//					Object te = it.next();	
//					if (!(te instanceof AdviceDeclarationNode))
//						vec.add(te);
//				}
//			}
//			StructureNode node = (StructureNode) parentElement;
//			for (Iterator it = node.getChildren().iterator(); it.hasNext();)
//				vec.add(it.next());
			return vec.toArray();
	
		}

		public Object getParent(Object element) {
			return ((StructureNode) element).getParent();
		}

		public boolean hasChildren(Object element) {
			if (element instanceof ProgramElementNode) {
				ProgramElementNode node = (ProgramElementNode) element;
				return node.getChildren().size() > 0 || node.getRelations().size() > 0;
			} else {
				StructureNode node = (StructureNode) element;
				return node.getChildren().size() > 0;
			}
		}
	}
	
	class HierarchyLabelProvider extends LabelProvider {

		public String getText(Object element) {
			try {
				
				/*
				if (element instanceof CaesarProgramElementNode) {
					CaesarProgramElementNode cNode = (CaesarProgramElementNode) element;
					return cNode.getText(super.getText(element));
				} else if (element instanceof LinkNode) {
					LinkNode lNode = (LinkNode) element;
					String returnString = lNode.toString();
					returnString = returnString.substring(returnString.lastIndexOf(']') + 2);
					try {
						String className = returnString.substring(0, returnString.lastIndexOf(':'));
						String advice =
							returnString.substring(
								returnString.lastIndexOf(':') + 2,
								returnString.length());
						return advice + ":" + className;
					} catch (RuntimeException e1) {
						return returnString.substring(0, returnString.length())+"()";
					}
				} else
					return super.getText(element);*/
			} catch (NullPointerException e) {
				logger.error("Sollte es nicht geben!", e);
				return "ERROR";
			}
			return "ERROR";
		}

		/*private String getArgument() {
			if (signature.charAt(0) == '[') {
				signature = signature.substring(1);
				return extractTyp() + "[]";
			} else {
				return extractTyp();
			}
		}

		private String extractTyp() {
			switch (signature.charAt(0)) {
				case 'I' :
					signature = signature.substring(1);
					return "int";
				case 'F' :
					signature = signature.substring(1);
					return "float";
				case 'D' :
					signature = signature.substring(1);
					return "double";
				case 'J' :
					signature = signature.substring(1);
					return "long";
				case 'C' :
					signature = signature.substring(1);
					return "char";
				case 'V' :
					signature = signature.substring(1);
					return "void";
				case 'L' :
					String sig = signature.substring(1, signature.indexOf(';'));
					sig = sig.substring(sig.lastIndexOf('/') + 1);
					signature = signature.substring(signature.indexOf(';') + 1);
					return sig;
				default :
					return "";
			}
		}*/

		public Image getImage(Object element) {
			try {
				throw(new Exception("No images implemented yet."));
			} catch (Exception e) {
				logger.error("Error while loading icon images for caesar outline view.", e);
				return super.getImage(element);
			}
		}
	}

	
	/**
	 * Return a new instance of <code>CaesarMetricsView</code>.
	 */
	public CaesarMetricsView() {
		super();
	}
	
	/**
	 * Create a very simple view to display the Java metrics. This is intentionally
	 * a trivial view, since the focus of this solution is the interaction with the
	 * Java model.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(Composite)
	 */
	 public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());		
		TreeViewer viewer = getTreeViewer();
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new HierarchyContentProvider());
		viewer.setLabelProvider(new HierarchyLabelProvider());
		viewer.expandAll();
		
	    getViewSite().
			getWorkbenchWindow().
			getSelectionService().
			addSelectionListener(this);
			
		// Create the model and listen for changes in it.
		jm = new CaesarMetrics();
		jm.addListener(this);
	}

	/* non-Javadoc
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		//treeViewer.s .setFocus();
	}

	/**
	 * Update the view to match the selection, if it is an <code>ICompilationUnit</code>.
	 * 
	 * @see ISelectionService#addSelectionListener
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			ICompilationUnit cu = 
				getCompilationUnit((IStructuredSelection) selection);
			//jm.reset(cu);
		}
		//log.debug("Selection in Editor!");
	}
	
	
	
	/* non-Javadoc
	 * Remove the listeners that were established in <code>createPartControl</code>.
	 */
	public void dispose() {
		getViewSite().
			getWorkbenchWindow().
			getSelectionService().
			removeSelectionListener(this);
			
		jm.removeListener(this);			
		
		super.dispose();
	}

	/**
	 * If the selection corresponds to an <code>ICompilationUnit</code>
	 * (or *.java file), return it.
	 * 
	 * @return	an <code>ICompilationUnit</code> or null
	 */
	private ICompilationUnit getCompilationUnit(IStructuredSelection ss) {
		if (ss.getFirstElement() instanceof IJavaElement) {
			IJavaElement je = (IJavaElement) ss.getFirstElement();
			return (ICompilationUnit) je.getAncestor(IJavaElement.COMPILATION_UNIT);
		}
		if (ss.getFirstElement() instanceof IFile) {
			IFile f = (IFile) ss.getFirstElement();
			if (f.getFileExtension() != null &&
				f.getFileExtension().compareToIgnoreCase("java") == 0)
			return (ICompilationUnit) JavaCore.create(f);
		}

		return null;
	}

	/**
	 * Update the view to reflect changes in the metrics.
	 * 
	 * @see ICaesarJMetricsListener#refresh(CaesarMetrics)
	 */
	public void refresh(CaesarMetrics unused) {
		
		// Notifications don't necessarily occur on the UI thread,
		// so make sure the update does run on it.
		logger.debug("Updated TreeView");
		Display.getDefault().syncExec(
			new Runnable() {
				public void run() {
					treeViewer.refresh();	
		}});
	}

	private TreeViewer getTreeViewer()
	{	
		return treeViewer;
	}
	
}