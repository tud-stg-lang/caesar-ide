package org.caesarj.ui.javamodel.bridge;

import java.util.HashMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.ASTHolderCUInfo;

public class ASTHolderCUInfoExt extends ASTHolderCUInfo {
	
	public int astLevel = ICompilationUnit.NO_AST;
	public boolean resolveBindings = false;
	public boolean statementsRecovery = false;
	public HashMap problems = null;
	public CompilationUnit ast = null;
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
