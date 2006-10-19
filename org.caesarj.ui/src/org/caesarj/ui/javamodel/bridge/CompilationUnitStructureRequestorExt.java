package org.caesarj.ui.javamodel.bridge;

import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.core.CompilationUnitElementInfo;
import org.eclipse.jdt.internal.core.CompilationUnitStructureRequestor;

public class CompilationUnitStructureRequestorExt extends
		CompilationUnitStructureRequestor {

	public CompilationUnitStructureRequestorExt(ICompilationUnit unit,
			CompilationUnitElementInfo unitInfo, Map newElements) {
		super(unit, unitInfo, newElements);
		// TODO Auto-generated constructor stub
	}
	
	public void setParser(Parser parser) {
		this.parser = parser;
	}

}
