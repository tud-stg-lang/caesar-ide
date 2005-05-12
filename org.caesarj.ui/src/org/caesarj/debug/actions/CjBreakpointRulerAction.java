/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package org.caesarj.debug.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.ActionMessages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * "Toggle Breakpoint" action for the vertical ruler of the caesarj-editor.
 * Based on the equivalent action of the ajdt. 
 * @see org.eclipse.ajdt.internal.ui.editor.AspectJBreakpointRulerAction
 * 
 * @author meffert
 */
public class CjBreakpointRulerAction extends Action {

	private IVerticalRulerInfo fRuler;
	private ITextEditor fTextEditor;
	private IEditorStatusLine fStatusLine;
	private CjToggleBreakpointAdapter fBreakpointAdapter;

	public CjBreakpointRulerAction(IVerticalRulerInfo ruler,
			ITextEditor editor, IEditorPart editorPart) {
		
		super(ActionMessages.getString("ManageBreakpointRulerAction.label"));
		fRuler = ruler;
		fTextEditor = editor;
		fStatusLine = (IEditorStatusLine) editorPart.getAdapter(IEditorStatusLine.class);
		fBreakpointAdapter = new CjToggleBreakpointAdapter();
	}
	
	/**
	 * Disposes this action
	 */
	public void dispose() {
		fTextEditor = null;
		fRuler = null;
	}

	/**
	 * Returns this action's vertical ruler info.
	 * 
	 * @return this action's vertical ruler
	 */
	protected IVerticalRulerInfo getVerticalRulerInfo() {
		return fRuler;
	}

	/**
	 * Returns this action's editor.
	 * 
	 * @return this action's editor
	 */
	protected ITextEditor getTextEditor() {
		return fTextEditor;
	}

	/**
	 * Returns the <code>IDocument</code> of the editor's input.
	 * 
	 * @return the document of the editor's input
	 */
	protected IDocument getDocument() {
		IDocumentProvider provider = fTextEditor.getDocumentProvider();
		return provider.getDocument(fTextEditor.getEditorInput());
	}
	
	protected IResource getResource() {
		IResource resource = null;
		IEditorInput editorInput = fTextEditor.getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			resource = ((IFileEditorInput) editorInput).getFile();
		}
		return resource;
	}
	
	/**
	 * Returns the <code>AbstractMarkerAnnotationModel</code> of the editor's
	 * input.
	 * 
	 * @return the marker annotation model
	 */
	protected AbstractMarkerAnnotationModel getAnnotationModel() {
		IDocumentProvider provider = fTextEditor.getDocumentProvider();
		IAnnotationModel model = provider.getAnnotationModel(fTextEditor
				.getEditorInput());
		if (model instanceof AbstractMarkerAnnotationModel) {
			return (AbstractMarkerAnnotationModel) model;
		}
		return null;
	}
	
	/**
	 * Checks whether a position includes the ruler's line of activity.
	 * 
	 * @param position
	 *            the position to be checked
	 * @param document
	 *            the document the position refers to
	 * @return <code>true</code> if the line is included by the given position
	 */
	protected boolean includesRulerLine(Position position, IDocument document) {

		if (position != null) {
			try {
				int markerLine = document.getLineOfOffset(position.getOffset());
				int line = fRuler.getLineOfLastMouseButtonActivity();
				if (line == markerLine) {
					return true;
				}
			} catch (BadLocationException x) {
			}
		}

		return false;
	}
	
	/**
	 * Returns a list of markers that exist at the current ruler location.
	 * 
	 * @return a list of markers that exist at the current ruler location
	 */
	protected List getMarkers() {

		List breakpoints = new ArrayList();

		IResource resource = getResource();
		IDocument document = getDocument();
		AbstractMarkerAnnotationModel model = getAnnotationModel();

		if (model != null) {
			try {

				IMarker[] markers = null;
				if (resource instanceof IFile)
					markers = resource.findMarkers(
							IBreakpoint.BREAKPOINT_MARKER, true,
							IResource.DEPTH_INFINITE);
				else {
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
							.getRoot();
					markers = root.findMarkers(IBreakpoint.BREAKPOINT_MARKER,
							true, IResource.DEPTH_INFINITE);
				}

				if (markers != null) {
					IBreakpointManager breakpointManager = DebugPlugin
							.getDefault().getBreakpointManager();
					for (int i = 0; i < markers.length; i++) {
						IBreakpoint breakpoint = breakpointManager
								.getBreakpoint(markers[i]);
						if (breakpoint != null
								&& breakpointManager.isRegistered(breakpoint)
								&& includesRulerLine(model
										.getMarkerPosition(markers[i]),
										document))
							breakpoints.add(markers[i]);
					}
				}
			} catch (CoreException x) {
				JDIDebugUIPlugin.log(x.getStatus());
			}
		}
		return breakpoints;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		try {
			/*
			 * Test if there are any markers at the current position.
			 * if there are any, remove breakpointmarker, otherwise create a 
			 * new one.
			 */
			List list = getMarkers();
			if (list.isEmpty()) {
				// create new markers
				IDocument document= getDocument();
				int lineNumber= getVerticalRulerInfo().getLineOfLastMouseButtonActivity();
				if (lineNumber >= document.getNumberOfLines()) {
					return;
				}
				try {
					IRegion line= document.getLineInformation(lineNumber);
					ITextSelection selection = new TextSelection(document, line.getOffset(), line.getLength());
					fBreakpointAdapter.toggleLineBreakpoints(fTextEditor, selection);
				} catch (BadLocationException e) {
					//likely document is folded so you cannot get the line information of the folded line
				}
			} else {
				// remove existing breakpoints of any type
				IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
				Iterator iterator = list.iterator();
				while (iterator.hasNext()) {
					IMarker marker = (IMarker) iterator.next();
					IBreakpoint breakpoint = manager.getBreakpoint(marker);
					if (breakpoint != null) {
						breakpoint.delete();
					}
				}
			}
		} catch (CoreException e) {
			JDIDebugUIPlugin.errorDialog(ActionMessages.getString("ManageBreakpointRulerAction.error.adding.message1"), e); //$NON-NLS-1$
		}	
	}
	
	protected void report(final String message) {
		JDIDebugUIPlugin.getStandardDisplay().asyncExec(new Runnable() {
			public void run() {
				if (fStatusLine != null) {
					fStatusLine.setMessage(true, message, null);
				}
				if (message != null
						&& JDIDebugUIPlugin.getActiveWorkbenchShell() != null) {
					Display.getCurrent().beep();
				}
			}
		});
	}
}
