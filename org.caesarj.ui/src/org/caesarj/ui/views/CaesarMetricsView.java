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

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

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
		message = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		message.setText(NO_SELECTION_MESSAGE);
		
		// Listen for changes in the Workbench selection.
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
		message.setFocus();
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
			jm.reset(cu);
		}
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
		Display.getDefault().syncExec(
			new Runnable() {
				public void run() {
					if (jm.hasValidMetrics())
						message.setText(jm.summaryString());
					else
						message.setText(NO_SELECTION_MESSAGE);
			
		}});
	}
}