package org.caesarj.ui.javamodel;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitDocumentProvider;

public class CJCompilationUnitDocumentProvider extends CompilationUnitDocumentProvider {
	protected ICompilationUnit createCompilationUnit(IFile file) {
		return CJCompilationUnitManager.INSTANCE.getCJCompilationUnit(file);
	}
}
