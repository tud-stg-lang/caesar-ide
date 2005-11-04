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
import org.caesarj.compiler.ast.phylum.declaration.JFieldDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CType;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
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
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditor;


/** 
 * Supports the creation/deletion of breakpoints in caesar classes.
 * Based on the equivalent adapter of the ajdt.
 * @see org.eclipse.ajdt.internal.debug.ui.actions.ToggleBreakpointAdapter
 * 
 * @author meffert
 */
public class CjToggleBreakpointAdapter implements IToggleBreakpointsTargetExtension {
	
	protected static final int LINE_BREAKPOINT = 1;
	protected static final int METHOD_BREAKPOINT = 2;
	protected static final int WATCH_POINT = 3;
	protected static final int ANY_BREAKPOINT = 4;

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleBreakpointsImpl(part, selection, CjToggleBreakpointAdapter.LINE_BREAKPOINT);
	}
	
	/**
	 * Creates a linebreakpoint for the given type at the given line. If there are any mixin copies of this type, a breakpoint in each 
	 * copy at the corresponding line will be created.
	 * 
	 * @param editor		
	 * @param type			
	 * @param lineNumber
	 * @throws CoreException
	 */
	protected void createLineBreakpoint(CaesarEditor editor, JavaQualifiedName type, int lineNumber) throws CoreException {
		// resolve mixin copies and set breakpoint in each copy
		ProjectProperties properties = ProjectProperties.create(editor.getInputJavaElement().getJavaProject().getProject());

		CaesarTypeNode typeNode = properties.getKjcEnvironment().getCaesarTypeSystem().getCaesarTypeGraph().getType(type.convertToIfcName());
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
				type.convertPkgSeperator(".").toString(), 
				lineNumber, 
				-1, 
				-1, 
				0, 
				true,
				new HashMap(10));
		
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
	
	/**
	 * @param ast	the source of the abstract syntax tree
	 * @return		the qualified java type name for the ast
	 */
	protected JavaQualifiedName getTypeName(JCompilationUnit ast){
		JavaQualifiedName jqTypeName = null; 
		JTypeDeclaration[] types = ast.getInners();
		JTypeDeclaration publicType = null;
		JPackageName jpkgName = ast.getPackageName();
		boolean isExternalizedClass = jpkgName.isCollaboration();
		
		if(isExternalizedClass){
			publicType = types[0];
		}else{
			// find public type
			for(int i = 0; i < types.length; i++){
				if(types[i].getSourceClass().isPublic()){
					publicType = types[i];
					break;
				}
			}
		}
		if(publicType != null){
			jqTypeName = getTypeName(jpkgName, publicType);
		}
		return jqTypeName;
	}
	
	/**
	 * @param pkg
	 * @param type
	 * @return		the qualified java typename for the type declaration
	 */
	protected JavaQualifiedName getTypeName(JPackageName pkg, JTypeDeclaration type){
		JavaQualifiedName jqName = null;
		if(pkg.isCollaboration()){
			jqName = new JavaQualifiedName(pkg.getName() + "/" + type.getIdent().substring(0, type.getIdent().lastIndexOf("_Impl"))).convertToExternalizedClassName();
		}else{
			jqName = new JavaQualifiedName(type.getSourceClass().getQualifiedName());
		}
		
		return jqName;
	}
	
	/**
	 * @param methDec
	 * @return the method signature for the given method declaration
	 */
	protected String getMethodSignature(JMethodDeclaration methDec){
		JFormalParameter[] params = methDec.getParameters();
		String sig = "(";
		for(int i=0; i < params.length; i++){
			sig += params[i].getType().getSignature();
		}
		sig += ")" + methDec.getReturnType().getSignature();
		return sig;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleBreakpointsImpl(part, selection, CjToggleBreakpointAdapter.METHOD_BREAKPOINT);
	}
	
	/**
	 * Creates a method breakpoint. If there are any mixin copies of the given type, a corresponding methodbreakpoint in each
	 * copy will be created.
	 * 
	 * @param editor
	 * @param type
	 * @param methodName
	 * @param methodSignature
	 * @param lineNumber
	 * @throws CoreException
	 */
	protected void createMethodBreakpoint(CaesarEditor editor, JavaQualifiedName type, String methodName, String methodSignature, int lineNumber) throws CoreException {
		// resolve mixin copies and set breakpoint in each copy
		ProjectProperties properties = ProjectProperties.create(editor.getInputJavaElement().getJavaProject().getProject());

		CaesarTypeNode typeNode = properties.getKjcEnvironment().getCaesarTypeSystem().getCaesarTypeGraph().getType(type.convertToIfcName());
		Collection mixins = properties.getKjcEnvironment().getCaesarTypeSystem().getJavaTypeGraph().findMixinCopies(typeNode);
		Iterator it = mixins.iterator();
		while(typeNode != null && it.hasNext()){
			JavaTypeNode mixinNode = (JavaTypeNode)it.next();
			JavaQualifiedName copyQn = mixinNode.getQualifiedName();
			JDIDebugModel.createMethodBreakpoint(
					getResource(editor), 
					copyQn.convertPkgSeperator(".").toString(), 
					methodName, 
					methodSignature, 
					true, 
					false, 
					false, 
					lineNumber, 
					-1, 
					-1, 
					0, 
					true, 
					new HashMap(10));
		}
		JDIDebugModel.createMethodBreakpoint(
				getResource(editor), 
				type.convertPkgSeperator(".").toString(), 
				methodName, 
				methodSignature, 
				true, 
				false, 
				false, 
				lineNumber, 
				-1, 
				-1, 
				0, 
				true, 
				new HashMap(10));
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		return selection instanceof ITextSelection;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleWatchpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleBreakpointsImpl(part, selection, CjToggleBreakpointAdapter.WATCH_POINT);
	}
	
	/**
	 * Creates a watchpoint. If there are any mixin copies of the given type, a corresponding watchpoint in each copy
	 * will be created.
	 * 
	 * @param editor
	 * @param type
	 * @param fieldName
	 * @param lineNumber
	 * @throws CoreException
	 */
	protected void createWatchpoint(CaesarEditor editor, JavaQualifiedName type, String fieldName, int lineNumber) throws CoreException {
		// resolve mixin copies and set breakpoint in each copy
		ProjectProperties properties = ProjectProperties.create(editor.getInputJavaElement().getJavaProject().getProject());

		CaesarTypeNode typeNode = properties.getKjcEnvironment().getCaesarTypeSystem().getCaesarTypeGraph().getType(type.convertToIfcName());
		Collection mixins = properties.getKjcEnvironment().getCaesarTypeSystem().getJavaTypeGraph().findMixinCopies(typeNode);
		Iterator it = mixins.iterator();
		while(typeNode != null && it.hasNext()){
			JavaTypeNode mixinNode = (JavaTypeNode)it.next();
			JavaQualifiedName copyQn = mixinNode.getQualifiedName();
			JDIDebugModel.createWatchpoint(
					getResource(editor), 
					copyQn.convertPkgSeperator(".").toString(), 
					fieldName, 
					lineNumber, 
					-1, 
					-1, 
					0, 
					true, 
					new HashMap(10));
		}
		JDIDebugModel.createWatchpoint(
				getResource(editor), 
				type.convertPkgSeperator(".").toString(), 
				fieldName, 
				lineNumber, 
				-1, 
				-1, 
				0, 
				true, 
				new HashMap(10));
				
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleWatchpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		return selection instanceof ITextSelection;
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
	
	/**
	 * Displays message on the status line. If message is null the status line will be cleared.
	 * @param message
	 * @param part
	 */
	protected void report(final String message, final IWorkbenchPart part) {
        JDIDebugUIPlugin.getStandardDisplay().asyncExec(new Runnable() {
            public void run() {
                IEditorStatusLine statusLine = (IEditorStatusLine) part.getAdapter(IEditorStatusLine.class);
                if (statusLine != null) {
                    if (message != null) {
                        statusLine.setMessage(true, message, null);
                    } else {
                        statusLine.setMessage(true, null, null);
                    }
                }
                if (message != null && JDIDebugUIPlugin.getActiveWorkbenchShell() != null) {
                    JDIDebugUIPlugin.getActiveWorkbenchShell().getDisplay().beep();
                }
            }
        });
    }

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension#toggleBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleBreakpointsImpl(part, selection, CjToggleBreakpointAdapter.ANY_BREAKPOINT);
	}	
		
	/**
	 * @param part
	 * @param selection
	 * @param bpType 		which type of breakpoint should be toggled (ANY_BREAKPOINT, LINE_BREAKPOINT, METHOD_BREAKPOINT, WATCH_POINT)
	 * @throws CoreException
	 */
	protected void toggleBreakpointsImpl(final IWorkbenchPart part, final ISelection selection, final int bpType) throws CoreException {
		Job job = new Job("Toggle Breakpoint") { //$NON-NLS-1$
            protected IStatus run(IProgressMonitor monitor) {
            	if(selection instanceof ITextSelection && part instanceof CaesarEditor) {
            		if (monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }
					CaesarEditor editor = (CaesarEditor)part;
					IEditorInput editorInput = editor.getEditorInput();
					int lineNumber = ((ITextSelection)selection).getStartLine() + 1;
					
					
					// clear statusline (remove possible error-message)
					report(null, part);
		
					try {
						// delete breakpoints at the given line
						boolean createNewBP = ! deleteBreakpoints(editor, lineNumber);
						if (createNewBP) {
							// No breakpoints found for this line.
							// Create new breakpoint!
							
							// Create AST
							JCompilationUnit ast = null;
							if(editorInput instanceof IPathEditorInput) {
								File file = ((IPathEditorInput)editorInput).getPath().toFile();
								ast = buildAST(file);
							} else {
								// can't build AST, do nothing
								return Status.CANCEL_STATUS;
							}
							
							 
							
							// 1) verify the breakpoint position
							ASTUtil astUtil = new ASTUtil(ast, lineNumber);
							if(astUtil.canSetWatchpoint() && (bpType == CjToggleBreakpointAdapter.ANY_BREAKPOINT || bpType == CjToggleBreakpointAdapter.WATCH_POINT)){
								//toggleWatchpoints(part, selection);
								JFieldDeclaration fieldDec = astUtil.getFieldDeclaration();
								// 2) extract type information
								JavaQualifiedName jqTypeName = null;
								if(ast.getPackageName().isCollaboration()){
									// create java type name for an externalized class
									String extClassPrefix = new JavaQualifiedName(ast.getPackageName().getName()).convertToExternalizedClassName().toString();
									jqTypeName = new JavaQualifiedName(extClassPrefix + "$" + astUtil.getTypeName());
								}else{
									jqTypeName = getTypeName(ast.getPackageName(), astUtil.getTypeDeclaration());
								}
								if(fieldDec != null){
									createWatchpoint(editor, jqTypeName, fieldDec.getVariable().getIdent(), lineNumber);
								}
								
							}else if(astUtil.canSetMethodBreakpoint() && (bpType == CjToggleBreakpointAdapter.ANY_BREAKPOINT || bpType == CjToggleBreakpointAdapter.METHOD_BREAKPOINT)){
								//toggleMethodBreakpoints(part, selection);
								JMethodDeclaration methDec = astUtil.getMethodDeclaration();
								// 2) extract type information
								JavaQualifiedName jqTypeName = null;
								if(ast.getPackageName().isCollaboration()){
									// create java type name for an externalized class
									String extClassPrefix = new JavaQualifiedName(ast.getPackageName().getName()).convertToExternalizedClassName().toString();
									jqTypeName = new JavaQualifiedName(extClassPrefix + "$" + astUtil.getTypeName());
								}else{
									jqTypeName = getTypeName(ast.getPackageName(), astUtil.getTypeDeclaration());
								}
								if(methDec != null){
									createMethodBreakpoint(editor, jqTypeName, methDec.getIdent(), getMethodSignature(methDec), lineNumber);
								}
		
							}else if(astUtil.canSetLineBreakpoint() && (bpType == CjToggleBreakpointAdapter.ANY_BREAKPOINT || bpType == CjToggleBreakpointAdapter.LINE_BREAKPOINT)){
								//toggleLineBreakpoints(part, selection);
								// 2) extract type information
								JavaQualifiedName jqTypeName = getTypeName(ast);
								createLineBreakpoint(editor, jqTypeName, lineNumber);
								
							}else{
								report(CaesarJPluginResources.getResourceString("Debugging.statusline.breakpointCanNotBeSet"), part);
							}
							
						}
					} catch (CoreException ce) {
						return ce.getStatus();
					}
				}
                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        job.schedule();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension#canToggleBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleBreakpoints(IWorkbenchPart part, ISelection selection) {
		return selection instanceof ITextSelection;
	}
	
	/**
	 * Deletes all breakpoints (including line-, method-, watchpoints) at the given line.
	 * Returns false if no breakpoints where found, otherwise true.
	 * 
	 * @param editor
	 * @param lineNumber
	 * @return			returns true if a breakpoint has been deleted, false otherwise
	 * @throws CoreException
	 */
	protected boolean deleteBreakpoints(CaesarEditor editor, int lineNumber) throws CoreException {
		List list = getMarkers(editor, lineNumber);
		if(list.isEmpty()) {
			// no markers found, no breakpoints deleted
			return false;
		}
		
		boolean deleted = false;
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			IMarker marker = (IMarker) iterator.next();
			IBreakpoint breakpoint = manager.getBreakpoint(marker);
			if (breakpoint != null) {
				breakpoint.delete();
				deleted = true;
			}
		}
		return deleted;
	}
}
