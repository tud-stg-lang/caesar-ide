package org.caesarj.ui.javamodel;

import org.caesarj.ui.javamodel.bridge.CompilationUnitExt;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.internal.core.BufferManager;
import org.eclipse.jdt.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.jdt.internal.core.PackageFragment;

public class CJCompilationUnit extends CompilationUnitExt {

	//private IFile cjFile;
	int originalContentMode = 0;
	protected JavaCompatibleBuffer javaCompBuffer;
		
	public boolean isInOriginalContentMode(){
		return originalContentMode > 0;
	}
	
	public void requestOriginalContentMode(){
		originalContentMode++;
	}
	
	public void discardOriginalContentMode(){
		originalContentMode--;
	}
	
	public CJCompilationUnit(PackageFragment parent, String name, WorkingCopyOwner owner) {
		super(parent, name, owner);
	/*	if (parent.getResource() instanceof IProject) {
			IProject p = (IProject)parent.getResource();
			this.cjFile = (IFile)p.findMember(name);
		} else {
			IFolder f = (IFolder)parent.getResource();
			this.cjFile = (IFile)f.findMember(name);
		} */	
	}
	
	public CJCompilationUnit(IFile cjFile){
		super(CJCompilationUnitTools.getParentPackage(cjFile), cjFile.getName(), DefaultWorkingCopyOwner.PRIMARY);
		//this.cjFile = cjFile;
	}
	
	public IBuffer getBuffer() throws JavaModelException {
		return convertBuffer(super.getBuffer());
	}
	
	protected void closeBuffer() {
		if (javaCompBuffer != null){
			javaCompBuffer.close();
			javaCompBuffer = null;
		}
		super.closeBuffer();
	}
	
	public char[] getContents() {
		try {
			IBuffer buffer = this.getBuffer();
			return buffer == null ? CharOperation.NO_CHAR : buffer.getCharacters();
		} catch (JavaModelException e) {
			return CharOperation.NO_CHAR;
		}
	}

	public IBuffer convertBuffer(IBuffer buf) {
		if (isInOriginalContentMode() || (buf == null))
			return buf;
		
		if (javaCompBuffer == null){
			BufferManager bm = BufferManager.getDefaultBufferManager();
			IBuffer myBuffer = bm.createBuffer(this);
			javaCompBuffer = new JavaCompatibleBuffer(buf, myBuffer);
		} else {
			if (buf != javaCompBuffer)
				javaCompBuffer.reinitialize(buf);
		}

		return javaCompBuffer;
	}
	
//	 copied from super, but changed to use an AJReconcileWorkingCopyOperation
	public org.eclipse.jdt.core.dom.CompilationUnit reconcile(int astLevel,
			boolean forceProblemDetection,
			boolean enableStatementsRecovery,
			WorkingCopyOwner workingCopyOwner,
			IProgressMonitor monitor) throws JavaModelException {
		if (!isWorkingCopy()) return null; // Reconciling is not supported on non working copies
		if (workingCopyOwner == null) workingCopyOwner = DefaultWorkingCopyOwner.PRIMARY;
		
		boolean createAST = false;
		if (astLevel == AST.JLS3) {
			// client asking for level 3 ASTs; these are supported
			createAST = true;
		} else {
			// client asking for no AST (0) or unknown ast level
			// either way, request denied
			createAST = false;
		}
		AJReconcileWorkingCopyOperation op = new AJReconcileWorkingCopyOperation(this, createAST, astLevel, workingCopyOwner);
		op.runOperation(monitor);
		return op.ast;
	}
}
