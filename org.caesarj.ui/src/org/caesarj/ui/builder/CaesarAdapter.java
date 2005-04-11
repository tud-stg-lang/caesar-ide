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
 * $Id: CaesarAdapter.java,v 1.28 2005-04-11 09:03:28 thiago Exp $
 */

package org.caesarj.ui.builder;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.aspectj.asm.IHierarchy;
import org.apache.log4j.Logger;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.asm.CaesarAsmBuilder;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.util.CWarning;
import org.caesarj.util.PositionedError;
import org.caesarj.util.UnpositionedError;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This is the main interface to caesar compiler. Responsible for invoking the
 * compiler, displaying progress, building the structure model and collecting
 * compiler errors.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public final class CaesarAdapter extends Main {
	
	//protected final static boolean buildAsm = true;
    //protected final static boolean printAsm = true;
    
    private static Logger log = Logger.getLogger(CaesarAdapter.class);

	public static IProgressMonitor NULL_PROGRESS_MONITOR = new NullProgressMonitor();

	private Collection errors;

	private IProgressMonitor progressMonitor;

	static Logger logger = Logger.getLogger(CaesarAdapter.class);

	public CaesarAdapter(String projectLocation) {
		super(projectLocation, null);
		CaesarAdapter.buildAsm = true;
		CaesarAdapter.printAsm = true;
	}

	public void reportTrouble(PositionedError error) {
		/**
		 * TODO: move filtering of messages to compiler
		 */
		if (error instanceof CWarning) {
            if (options.warning != 0 && filterWarning((CWarning)error)) {
            	this.errors.add(error);
            }
        }
		else {
			this.errors.add(error);
		}
		super.reportTrouble(error);
	}
	
	public void reportTrouble(UnpositionedError error) {
		this.errors.add(error);
		super.reportTrouble(error);
    }

	public boolean compile(Collection sourceFiles, String classPath,
			String outputPath, Collection errorsArg) {
		return compile(sourceFiles, classPath, outputPath, errorsArg, null);
	}

	public boolean compile(Collection sourceFiles, String classPath,
			String outputPath, Collection errorsArg,
			IProgressMonitor progressMonitorArg) {

		// The singleton is not used anymore, because the plugin can have more than
		// one project and each project must have a structure model
		//this.model = StructureModelManager.INSTANCE.getStructureModel();
		this.model = CaesarAsmBuilder.createHierarchy();
		
		boolean success = false;
		String args[] = new String[sourceFiles.size() + 4];

		int i = 0;

		args[i++] = "-d"; //$NON-NLS-1$
		args[i++] = outputPath;

		args[i++] = "-classpath"; //$NON-NLS-1$
		args[i++] = classPath;

		for (Iterator it = sourceFiles.iterator(); it.hasNext();) {
			args[i++] = it.next().toString();
		}

		this.errors = errorsArg;
		this.progressMonitor = progressMonitorArg != null ? progressMonitorArg
				: NULL_PROGRESS_MONITOR;

		this.progressMonitor.beginTask("compiling source files", sourceFiles //$NON-NLS-1$
				.size() + 1);

		try {
			success = run(args);
		} 
		catch (RuntimeException e) {
			e.printStackTrace();
			errors.add("internal compiler error: " + e.toString());	
		}
		
		return success;
	}
	
	protected JCompilationUnit parseFile(File file, KjcEnvironment env) {
		if (this.progressMonitor.isCanceled()) {
			return null;
		}

		JCompilationUnit res;
		this.progressMonitor.subTask("compiling " + file.getName()); //$NON-NLS-1$
		System.out.println("compiling " + file.getName());
		res = super.parseFile(file, env);
		this.progressMonitor.worked(1);
		
		//AsmBuilder.build(res, this.model);
		System.out.println("parseFile completed: " + file.getName());
		return res;
	}
	
	protected void preWeaveProcessing(JCompilationUnit[] cu) {
		super.preWeaveProcessing(cu);
    }
	

	protected void weaveClasses() {
		if (this.progressMonitor.isCanceled()) {
			return;
		}

		this.progressMonitor.subTask("weaving classes..."); //$NON-NLS-1$

		super.weaveClasses();

		this.progressMonitor.worked(1);
	}
	/**
	 * @return Returns the hierarchy.
	 */
	public IHierarchy getHierarchy() {
		return model;
	}

}