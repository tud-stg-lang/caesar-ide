package org.caesarj.ui.javamodel;

import org.caesarj.ui.javamodel.bridge.CompilationUnitExt;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.CharOperation;
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
	}
	
	public CJCompilationUnit(IFile cjFile){
		super(CJCompilationUnitTools.getParentPackage(cjFile), cjFile.getName(), DefaultWorkingCopyOwner.PRIMARY);
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
}
