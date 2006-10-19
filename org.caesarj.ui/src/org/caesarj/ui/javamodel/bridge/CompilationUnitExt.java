package org.caesarj.ui.javamodel.bridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ISourceElementRequestor;
import org.eclipse.jdt.internal.compiler.SourceElementParser;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.CompilationUnitProblemFinder;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.OpenableElementInfo;
import org.eclipse.jdt.internal.core.PackageFragment;

/**
 * CompilationUnitExt is functionally equivalent to CompilationUnit
 * but refactored to introduce more overridable methods
 */

public class CompilationUnitExt extends CompilationUnit {

	public CompilationUnitExt(PackageFragment parent, String name, WorkingCopyOwner owner) {
		super(parent, name, owner);
	}
	
	protected SourceElementParser createSourceElementParser(
			final ISourceElementRequestor requestor, 
			IProblemFactory problemFactory,
			CompilerOptions options,
			boolean reportLocalDeclarations,
			boolean optimizeStringLiterals) {
		return new SourceElementParser(
				requestor, 
				problemFactory, 
				options,
				reportLocalDeclarations,
				!optimizeStringLiterals);
	}
	
	public org.eclipse.jdt.core.dom.CompilationUnit makeConsistent(int astLevel, boolean resolveBindings, boolean statementsRecovery, HashMap problems, IProgressMonitor monitor) throws JavaModelException {
		if (isConsistent()) return null;
			
		// create a new info and make it the current info
		// (this will remove the info and its children just before storing the new infos)
		if (astLevel != NO_AST || problems != null) {
			ASTHolderCUInfoExt info = new ASTHolderCUInfoExt();
			info.astLevel = astLevel;
			info.resolveBindings = resolveBindings;
			info.statementsRecovery = statementsRecovery;
			info.problems = problems;
			openWhenClosed(info, monitor);
			org.eclipse.jdt.core.dom.CompilationUnit result = info.ast;
			info.ast = null;
			return result;
		} else {
			openWhenClosed(createElementInfo(), monitor);
			return null;
		}
	}
	
	/*
	 * Opens an <code>Openable</code> that is known to be closed (no check for <code>isOpen()</code>).
	 * Returns the created element info.
	 */
	public Object openWhenClosed(Object info, IProgressMonitor monitor) throws JavaModelException {
		return super.openWhenClosed(info, monitor);
	}
	
	/**
	 * Returns a new element info for this element.
	 */
	public Object createElementInfo() {
		return new ASTHolderCUInfoExt();
	}
	
	protected boolean buildStructure(OpenableElementInfo info, final IProgressMonitor pm, Map newElements, IResource underlyingResource) throws JavaModelException {
		// check if this compilation unit can be opened
		if (!isWorkingCopy()) { // no check is done on root kind or exclusion pattern for working copies
			IStatus status = validateCompilationUnit(underlyingResource);
			if (!status.isOK()) throw newJavaModelException(status);
		}
		
		// prevents reopening of non-primary working copies (they are closed when they are discarded and should not be reopened)
		if (!isPrimary() && getPerWorkingCopyInfo() == null) {
			throw newNotPresentException();
		}
		
		if (!(info instanceof ASTHolderCUInfoExt)) {
			return false;
		}

		ASTHolderCUInfoExt unitInfo = (ASTHolderCUInfoExt) info;

		// get buffer contents
		IBuffer buffer = getBufferManager().getBuffer(this);
		if (buffer == null) {
			buffer = openBuffer(pm, unitInfo); // open buffer independently from the info, since we are building the info
		}
		final char[] contents = buffer == null ? null : buffer.getCharacters();

		// generate structure and compute syntax problems if needed
		CompilationUnitStructureRequestorExt requestor = new CompilationUnitStructureRequestorExt(this, unitInfo, newElements);
		JavaModelManager.PerWorkingCopyInfo perWorkingCopyInfo = getPerWorkingCopyInfo();
		IJavaProject project = getJavaProject();

		boolean createAST;
		boolean resolveBindings;
		boolean statementsRecovery;
		HashMap problems;
		if (info instanceof ASTHolderCUInfoExt) {
			ASTHolderCUInfoExt astHolder = (ASTHolderCUInfoExt) info;
			createAST = astHolder.astLevel != NO_AST;
			resolveBindings = astHolder.resolveBindings;
			statementsRecovery = astHolder.statementsRecovery;
			problems = astHolder.problems;
		} else {
			createAST = false;
			resolveBindings = false;
			statementsRecovery = false;
			problems = null;
		}
		
		boolean computeProblems = perWorkingCopyInfo != null && perWorkingCopyInfo.isActive() && project != null && JavaProject.hasJavaNature(project.getProject());
		IProblemFactory problemFactory = new DefaultProblemFactory();
		Map options = project == null ? JavaCore.getOptions() : project.getOptions(true);
		if (!computeProblems) {
			// disable task tags checking to speed up parsing
			options.put(JavaCore.COMPILER_TASK_TAGS, ""); //$NON-NLS-1$
		}
		SourceElementParser parser = createSourceElementParser(
				requestor, 
				problemFactory, 
				new CompilerOptions(options),
				true/*report local declarations*/,
				!createAST /*optimize string literals only if not creating a DOM AST*/);
		parser.reportOnlyOneSyntaxError = !computeProblems;
		parser.setStatementsRecovery(statementsRecovery);
		
		if (!computeProblems && !resolveBindings && !createAST) // disable javadoc parsing if not computing problems, not resolving and not creating ast
			parser.javadocParser.checkDocComment = false;
		requestor.setParser(parser);
		CompilationUnitDeclaration unit = parser.parseCompilationUnit(
			new org.eclipse.jdt.internal.compiler.env.ICompilationUnit() {
				public char[] getContents() {
					return CompilationUnitExt.this.getContents();
				}
				public char[] getMainTypeName() {
					return CompilationUnitExt.this.getMainTypeName();
				}
				public char[][] getPackageName() {
					return CompilationUnitExt.this.getPackageName();
				}
				public char[] getFileName() {
					return CompilationUnitExt.this.getFileName();
				}
			}, 
			true /*full parse to find local elements*/);
		
		// update timestamp (might be IResource.NULL_STAMP if original does not exist)
		if (underlyingResource == null) {
			underlyingResource = getResource();
		}
		// underlying resource is null in the case of a working copy on a class file in a jar
		if (underlyingResource != null)
			unitInfo.setTimestamp(((IFile)underlyingResource).getModificationStamp());
		
		// compute other problems if needed
		CompilationUnitDeclaration compilationUnitDeclaration = null;
		try {
			if (computeProblems) {
				if (problems == null) {
					// report problems to the problem requestor
					problems = new HashMap();
					compilationUnitDeclaration = CompilationUnitProblemFinder.process(unit, this, contents, parser, this.owner, problems, createAST, true, pm);
					try {
						perWorkingCopyInfo.beginReporting();
						for (Iterator iteraror = problems.values().iterator(); iteraror.hasNext();) {
							CategorizedProblem[] categorizedProblems = (CategorizedProblem[]) iteraror.next();
							if (categorizedProblems == null) continue;
							for (int i = 0, length = categorizedProblems.length; i < length; i++) {
								perWorkingCopyInfo.acceptProblem(categorizedProblems[i]);
							}
						}
					} finally {
						perWorkingCopyInfo.endReporting();
					}
				} else {
					// collect problems
					compilationUnitDeclaration = CompilationUnitProblemFinder.process(unit, this, contents, parser, this.owner, problems, createAST, true, pm);
				}
			}
			
			if (createAST) {
				int astLevel = ((ASTHolderCUInfoExt) info).astLevel;
				org.eclipse.jdt.core.dom.CompilationUnit cu = AST.convertCompilationUnit(astLevel, unit, contents, options, computeProblems, this, pm);
				((ASTHolderCUInfoExt) info).ast = cu;
			}
		} finally {
		    if (compilationUnitDeclaration != null) {
		        compilationUnitDeclaration.cleanUp();
		    }
		}
		
		return unitInfo.isStructureKnown();
	}

}
