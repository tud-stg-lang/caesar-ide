package org.caesarj.ui.javamodel;

import org.caesarj.ui.project.CaesarJProjectTools;
import org.eclipse.contribution.jdt.cuprovider.ICompilationUnitProvider;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;

public class CJCompilationUnitProvider implements ICompilationUnitProvider {

	public CompilationUnit create(PackageFragment parent, String name,
			WorkingCopyOwner owner) {
		IJavaProject project = parent.getJavaProject();
		if (project != null && CaesarJProjectTools.isCJProject(project.getProject())) {
			// A java file inside CaesarJ project
			return new CJCompilationUnit(parent, name, owner);
		}
		else {
			// A java file outside CaesarJ project 
			// We assume that it is a pure Java file then
			return new CompilationUnit(parent, name, owner);
		}
	}
}
