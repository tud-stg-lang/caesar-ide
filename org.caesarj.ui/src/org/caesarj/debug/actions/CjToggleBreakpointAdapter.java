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
 
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.caesarj.compiler.KjcClassReader;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.KjcOptions;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.JPackageName;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.types.KjcSignatureParser;
import org.caesarj.compiler.types.KjcTypeFactory;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.java.JavaTypeNode;
import org.caesarj.ui.builder.CaesarAdapter;
import org.caesarj.ui.editor.CaesarEditor;
import org.caesarj.ui.resources.CaesarJPluginResources;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.ActionMessages;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;


/** 
 * Supports the creation/deletion of breakpoints in caesar classes.
 * Based on the equivalent adapter of the ajdt.
 * @see org.eclipse.ajdt.internal.debug.ui.actions.ToggleBreakpointAdapter
 * 
 * @author meffert
 */
public class CjToggleBreakpointAdapter implements IToggleBreakpointsTarget {
	

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		if(selection instanceof ITextSelection && part instanceof CaesarEditor) {
			CaesarEditor editor = (CaesarEditor)part;
			IEditorInput editorInput = editor.getEditorInput();
			int lineNumber = ((ITextSelection)selection).getStartLine() + 1;
			File file = null;
			JCompilationUnit ast = null;
			ProjectProperties properties = ProjectProperties.create(editor.getInputJavaElement().getJavaProject().getProject());

			//System.out.println("--- Toggle BreakPoint: ");
			if(editorInput instanceof IPathEditorInput){
				file = ((IPathEditorInput)editorInput).getPath().toFile();
				ast = buildAST(file);
				//System.out.println("---       File: " + file.getAbsolutePath());
			}else{
				//System.out.println("--- [ERR] File: NULL");
			}

			IStatusLineManager statusLine = editor.getEditorSite().getActionBars().getStatusLineManager();
			// clear statusline (remove possible error-message)
			report(null, statusLine); 

			try {
				List list = getMarkers(editor, lineNumber);
				if (list.isEmpty()) {
					// No breakpoint-marker found for line.
					// Create new breakpoint!
					
					// 1) extract type information
					//String typeName = null;
					JavaQualifiedName jqTypeName = null; 
					JTypeDeclaration[] types = ast.getInners();
					JTypeDeclaration publicType = null;
					JPackageName jpkgName = ast.getPackageName();
					boolean isExternalizedClass = jpkgName.isCollaboration();
					
					String pkgName = jpkgName.getName();
					//System.out.println("---       Pack: " + pkgName);
					for(int i = 0; i < types.length; i++){
						if(types[i].getCClass() != null){
							if(types[i].getCClass().isPublic()){
								publicType = types[i];
								break;
							}
						}else{
							// gdw. isExternalizedClass == true: System.out.println("--- [INF] CClass ist NULL");
							publicType = types[i];
							break;
						}
					}
					if(publicType != null){
						if(!pkgName.equals("")){
							if(isExternalizedClass){
								jqTypeName = new JavaQualifiedName(pkgName + "/" + publicType.getIdent().substring(0, publicType.getIdent().lastIndexOf("_Impl"))).convertToExternalizedClassName();
							}else{
								jqTypeName = new JavaQualifiedName(pkgName + "/" + publicType.getIdent());
							}
						}else{
							jqTypeName = new JavaQualifiedName(publicType.getIdent());
						}
						//System.out.println("---       Type: " + jqTypeName);
					}else{
						//System.out.println("--- [ERR] Type: NULL");
					}
					
					// 2) verify the breakpoint position
					boolean isValidBreakpoint = false;
					if(editorInput instanceof IPathEditorInput){
						Vector elementsAtLine = ASTUtil.getBreakpointableElements(ast, lineNumber);
						isValidBreakpoint = !elementsAtLine.isEmpty();
					}
					
					// 3) set breakpoint
					if(jqTypeName != null && isValidBreakpoint){
						// resolve mixin copies and set breakpoint in each copy
						//note: wie sieht es mit dem richtigen qualified classname aus? 
						// wie ist es mit inneren klassen und externalisierten klassen?
						CaesarTypeNode typeNode = properties.getKjcEnvironment().getCaesarTypeSystem().getCaesarTypeGraph().getType(jqTypeName.convertToIfcName());
						if(typeNode == null){
							//System.out.println("--- CaesarTypeNode == NULL");
						}
						Collection mixins = properties.getKjcEnvironment().getCaesarTypeSystem().getJavaTypeGraph().findMixinCopies(typeNode);
						Iterator it = mixins.iterator();
						while(typeNode != null && it.hasNext()){
							JavaTypeNode mixinNode = (JavaTypeNode)it.next();
							JavaQualifiedName copyQn = mixinNode.getQualifiedName();
							JDIDebugModel.createLineBreakpoint(
									getResource(editor),
									copyQn.convertPkgSeperator(".").toString(), //qnCopy, 
									lineNumber, 
									-1, 
									-1, 
									0, 
									true, 
									new HashMap(10));
						}
						JDIDebugModel.createLineBreakpoint(
								getResource(editor),
								jqTypeName.convertPkgSeperator(".").toString(), 
								lineNumber, 
								-1, 
								-1, 
								0, 
								true,
								new HashMap(10));
					} else {
						if(!isValidBreakpoint){
							//System.out.println("---       Breakpoint cannot be set at this line.");
						}
						report(CaesarJPluginResources.getResourceString("Debugging.statusline.breakpointCanNotBeSet"), statusLine);
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
				JDIDebugUIPlugin
						.errorDialog(
								ActionMessages
										.getString("ManageBreakpointRulerAction.error.adding.message1"), e); //$NON-NLS-1$
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
		return selection instanceof ITextSelection;
	}
	
	/**
	 * Builds the abstract syntax tree for the given file. Makes use of
	 * the Caesar-compiler to parse the file.
	 * 
	 * @param file
	 * @return the root of the AST
	 */
	protected JCompilationUnit buildAST(File file){
		JCompilationUnit ast = null;
		if(file == null){
			System.out.println("--- [ERR] File: NULL");
			return null;
		}
		
		try{
			// Create a caesar adapter
			CaesarAdapter caesarAdapter = new CaesarAdapter(null);
	
			// Build AST for active file
			// - refactoring: move code into CaesarAdapter.buildAST()?
			KjcOptions options = new KjcOptions();
			KjcClassReader reader = new KjcClassReader(
	                options.classpath,
	                options.extdirs,
	                new KjcSignatureParser());
			KjcEnvironment env = new KjcEnvironment(
					caesarAdapter,
		            reader,
		            new KjcTypeFactory(reader),
		            options);
			caesarAdapter.parseArguments(new String[0]);
			ast = caesarAdapter.buildAST(file, env);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ast;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleWatchpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleWatchpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	private IResource getResource(ITextEditor editor) {
		IResource resource = null;
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			resource = ((IFileEditorInput) editorInput).getFile();
		}
		return resource;
	}

	/**
	 * Returns a list of markers that exist at the current ruler location.
	 * 
	 * @return a list of markers that exist at the current ruler location
	 */
	private List getMarkers(ITextEditor editor, int lineNumber) {

		List breakpoints = new ArrayList();

		AbstractMarkerAnnotationModel model = getAnnotationModel(editor);
		IResource resource = getResource(editor);
		IDocument document = getDocument(editor);
		 

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
								&& breakpointManager.isRegistered(breakpoint) && includesRulerLine(model
										.getMarkerPosition(markers[i]),
										document, lineNumber)) {
							breakpoints.add(markers[i]);
						}
					}
				}
			} catch (CoreException x) {
				JDIDebugUIPlugin.log(x.getStatus());
			}
		}
		return breakpoints;
	}
	
	
	private IDocument getDocument(ITextEditor editor) {
		IDocumentProvider provider = editor.getDocumentProvider();
		return provider.getDocument(editor.getEditorInput());
	}

	
	/**
	 * Returns the <code>AbstractMarkerAnnotationModel</code> of the editor's
	 * input.
	 * 
	 * @return the marker annotation model
	 */
	private AbstractMarkerAnnotationModel getAnnotationModel(ITextEditor editor) {
		IDocumentProvider provider = editor.getDocumentProvider();
		IAnnotationModel model = provider.getAnnotationModel(editor
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
	private boolean includesRulerLine(Position position, IDocument document, int lineNumber) {

		if (position != null) {
			try {
				int markerLine = document.getLineOfOffset(position.getOffset());
				if (lineNumber == markerLine + 1) {
					return true;
				}
			} catch (BadLocationException x) {
			}
		}

		return false;
	}
	
	protected void report(String message, IStatusLineManager statusLine) {
		if (statusLine != null) {
			if (message != null) {
				statusLine.setErrorMessage(message);
			} else {
				statusLine.setErrorMessage(null);
			}
		}		
	}
}
