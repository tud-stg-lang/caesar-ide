package org.caesarj.ui.actions;

import java.util.Iterator;
import java.util.Vector;

import org.caesarj.ui.util.CaesarJNatureChange;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class AddCJNatureAction implements IObjectActionDelegate {

	private Vector selected = new Vector();

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction arg0) {

		for (Iterator iter = selected.iterator(); iter.hasNext();) {

			try {
				IProject project = (IProject) iter.next();
				IJavaProject jp = JavaCore.create(project);
				// Add the CaesarJ nature to the project and update the
				// build classpath with the aspectjrt.jar	
				CaesarJNatureChange.addCaesarJNature(jp,true);
			} catch (CoreException e) {
			}
		}
	}

	/**
	 * From IActionDelegate - set the availability or otherwise of this
	 * action.
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		selected.clear();
		boolean enable = true;
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) sel;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object object = iter.next();
				if (object instanceof IAdaptable) {
					IProject project = (IProject) ((IAdaptable)object).getAdapter(IProject.class);	
					if(project != null) {
						selected.add(project);
					} else {
						enable = false;
						break;
					}
				} else {
					enable = false;
					break;
				}
			}
			action.setEnabled(enable);
		}
	}

	/**
	 * From IObjectActionDelegate
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
